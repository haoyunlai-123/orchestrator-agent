package com.my.agent.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.agent.domain.dto.AgentPlan;
import com.my.agent.domain.dto.AgentPlanStep;
import com.my.agent.domain.entity.AgentRunEntity;
import com.my.agent.domain.entity.AgentStepRunEntity;
import com.my.agent.domain.enums.AgentRunStatus;
import com.my.agent.domain.enums.AgentStepStatus;
import com.my.agent.domain.vo.AgentRunDetailVO;
import com.my.agent.domain.vo.AgentStepDetailVO;
import com.my.agent.planner.AgentPlanner;
import com.my.agent.repository.AgentRunRepository;
import com.my.agent.repository.AgentStepRunRepository;
import com.my.agent.service.AgentOrchestratorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AgentOrchestratorServiceImpl implements AgentOrchestratorService {

    private final AgentRunRepository agentRunRepository;
    private final AgentStepRunRepository agentStepRunRepository;
    private final AgentPlanner agentPlanner;
    private final ObjectMapper objectMapper;

    public AgentOrchestratorServiceImpl(AgentRunRepository agentRunRepository,
                                        AgentStepRunRepository agentStepRunRepository,
                                        AgentPlanner agentPlanner,
                                        ObjectMapper objectMapper) {
        this.agentRunRepository = agentRunRepository;
        this.agentStepRunRepository = agentStepRunRepository;
        this.agentPlanner = agentPlanner;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public String createRun(String goal) {
        String runId = UUID.randomUUID().toString().replace("-", "");

        AgentRunEntity entity = new AgentRunEntity();
        entity.setRunId(runId);
        entity.setGoal(goal);
        entity.setStatus(AgentRunStatus.INIT.name());

        agentRunRepository.insert(entity);
        return runId;
    }

    @Override
    @Transactional
    public void plan(String runId) {
        AgentRunEntity run = agentRunRepository.findByRunId(runId);
        if (run == null) {
            throw new IllegalArgumentException("run不存在: " + runId);
        }

        agentRunRepository.updateStatus(runId, AgentRunStatus.PLANNING.name());

        AgentPlan plan = agentPlanner.generatePlan(run.getGoal());
        if (plan == null || plan.getSteps() == null || plan.getSteps().isEmpty()) {
            agentRunRepository.updateFinalResult(runId, AgentRunStatus.FAILED.name(), null, "生成的计划为空");
            throw new IllegalStateException("生成的计划为空");
        }

        String planJson = toJson(plan);
        String firstStepId = plan.getSteps().get(0).getStepId();

        agentRunRepository.updatePlan(runId, AgentRunStatus.PLAN_READY.name(), planJson, firstStepId);

        List<AgentStepRunEntity> stepEntities = plan.getSteps().stream()
                .map(step -> buildStepEntity(runId, step))
                .collect(Collectors.toList());

        agentStepRunRepository.batchInsert(stepEntities);
    }

    @Override
    public void execute(String runId) {
        // 下一步再做：查plan -> 调DAG执行器
    }

    @Override
    public AgentRunDetailVO getRunDetail(String runId) {
        AgentRunEntity run = agentRunRepository.findByRunId(runId);
        if (run == null) {
            throw new IllegalArgumentException("run不存在: " + runId);
        }

        List<AgentStepRunEntity> stepEntities = agentStepRunRepository.findByRunId(runId);

        AgentRunDetailVO vo = new AgentRunDetailVO();
        vo.setRunId(run.getRunId());
        vo.setGoal(run.getGoal());
        vo.setStatus(run.getStatus());
        vo.setCurrentStepId(run.getCurrentStepId());
        vo.setFinalResult(run.getFinalResult());
        vo.setErrorMessage(run.getErrorMessage());
        vo.setSteps(stepEntities.stream().map(this::toStepVO).collect(Collectors.toList()));
        return vo;
    }

    private AgentStepRunEntity buildStepEntity(String runId, AgentPlanStep step) {
        AgentStepRunEntity entity = new AgentStepRunEntity();
        entity.setRunId(runId);
        entity.setStepId(step.getStepId());
        entity.setStepIndex(step.getStepIndex());
        entity.setAction(step.getAction());
        entity.setStepName(step.getName());
        entity.setDependsOn(step.getDependsOn() == null ? null : String.join(",", step.getDependsOn()));
        entity.setStatus(AgentStepStatus.PENDING.name());
        entity.setInputJson(toJson(step.getParams()));
        entity.setRetryCount(0);
        return entity;
    }

    private AgentStepDetailVO toStepVO(AgentStepRunEntity entity) {
        AgentStepDetailVO vo = new AgentStepDetailVO();
        vo.setStepId(entity.getStepId());
        vo.setStepIndex(entity.getStepIndex());
        vo.setStepName(entity.getStepName());
        vo.setAction(entity.getAction());
        vo.setStatus(entity.getStatus());
        vo.setJobId(entity.getJobId());
        vo.setJobInstanceId(entity.getJobInstanceId());
        vo.setErrorMessage(entity.getErrorMessage());
        return vo;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json序列化失败", e);
        }
    }
}