package com.my.agent.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.agent.domain.dto.AgentPlan;
import com.my.agent.domain.dto.AgentPlanStep;
import com.my.agent.domain.entity.AgentStepRunEntity;
import com.my.agent.domain.enums.AgentRunStatus;
import com.my.agent.executor.context.AgentActionContext;
import com.my.agent.executor.context.AgentActionResult;
import com.my.agent.executor.handler.AgentActionHandler;
import com.my.agent.executor.registry.ActionHandlerRegistry;
import com.my.agent.repository.AgentRunRepository;
import com.my.agent.repository.AgentStepRunRepository;
import com.my.agent.service.AgentDagExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AgentDagExecutorImpl implements AgentDagExecutor {

    private final AgentRunRepository agentRunRepository;
    private final AgentStepRunRepository agentStepRunRepository;
    private final ActionHandlerRegistry actionHandlerRegistry;
    private final ObjectMapper objectMapper;

    public AgentDagExecutorImpl(AgentRunRepository agentRunRepository,
                                AgentStepRunRepository agentStepRunRepository,
                                ActionHandlerRegistry actionHandlerRegistry,
                                ObjectMapper objectMapper) {
        this.agentRunRepository = agentRunRepository;
        this.agentStepRunRepository = agentStepRunRepository;
        this.actionHandlerRegistry = actionHandlerRegistry;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(noRollbackFor = Exception.class)
    public void execute(String runId, AgentPlan plan) {
        agentRunRepository.updateCurrentStep(runId, null, AgentRunStatus.RUNNING.name());

        AgentActionContext context = new AgentActionContext();
        context.setRunId(runId);
        context.setPlan(plan);

        List<AgentPlanStep> steps = new ArrayList<>(plan.getSteps());
        steps.sort(Comparator.comparing(AgentPlanStep::getStepIndex));

        for (AgentPlanStep step : steps) {
            if (!dependenciesSatisfied(runId, step)) {
                agentRunRepository.updateFinalResult(runId, AgentRunStatus.FAILED.name(), null,
                        "步骤依赖未满足: " + step.getStepId());
                throw new IllegalStateException("步骤依赖未满足: " + step.getStepId());
            }

            context.setCurrentStepId(step.getStepId());
            agentRunRepository.updateCurrentStep(runId, step.getStepId(), AgentRunStatus.RUNNING.name());
            agentStepRunRepository.updateStepRunning(runId, step.getStepId());

            try {
                AgentActionHandler handler = actionHandlerRegistry.getHandler(step.getAction());
                AgentActionResult result = handler.execute(context, step);

                if (!result.isSuccess()) {
                    agentStepRunRepository.updateStepFailed(runId, step.getStepId(), result.getMessage());
                    agentRunRepository.updateFinalResult(runId, AgentRunStatus.FAILED.name(), null, result.getMessage());
                    throw new IllegalStateException(result.getMessage());
                }

                Long jobId = extractLong(result.getOutputs().get("jobId"));
                Long jobInstanceId = extractLong(result.getOutputs().get("jobInstanceId"));
                String outputJson = toJson(result.getOutputs());

                agentStepRunRepository.updateStepSuccess(
                        runId, step.getStepId(), outputJson, jobId, jobInstanceId
                );
            } catch (Exception e) {
                agentStepRunRepository.updateStepFailed(runId, step.getStepId(), e.getMessage());
                agentRunRepository.updateFinalResult(runId, AgentRunStatus.FAILED.name(), null, e.getMessage());
                throw e;
            }
        }

        String summary = (String) context.getSharedData().get("summary");
        agentRunRepository.updateFinalResult(runId, AgentRunStatus.SUCCESS.name(), summary, null);
    }

    private boolean dependenciesSatisfied(String runId, AgentPlanStep step) {
        if (step.getDependsOn() == null || step.getDependsOn().isEmpty()) {
            return true;
        }

        for (String depStepId : step.getDependsOn()) {
            AgentStepRunEntity dep = agentStepRunRepository.findByRunIdAndStepId(runId, depStepId);
            if (dep == null || !"SUCCESS".equals(dep.getStatus())) {
                return false;
            }
        }
        return true;
    }

    private Long extractLong(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Number n) {
            return n.longValue();
        }
        return Long.parseLong(String.valueOf(obj));
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json序列化失败", e);
        }
    }
}