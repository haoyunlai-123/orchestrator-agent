package com.my.agent.executor.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.agent.client.SchedulerOpenApiClient;
import com.my.agent.client.dto.TriggerOnceResponse;
import com.my.agent.domain.dto.AgentPlanStep;
import com.my.agent.executor.context.AgentActionContext;
import com.my.agent.executor.context.AgentActionResult;
import org.springframework.stereotype.Component;

@Component
public class TriggerJobActionHandler implements AgentActionHandler {

    private final SchedulerOpenApiClient schedulerOpenApiClient;
    private final ObjectMapper objectMapper;

    public TriggerJobActionHandler(SchedulerOpenApiClient schedulerOpenApiClient,
                                   ObjectMapper objectMapper) {
        this.schedulerOpenApiClient = schedulerOpenApiClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public String action() {
        return "TRIGGER_JOB";
    }

    @Override
    public AgentActionResult execute(AgentActionContext context, AgentPlanStep step) {
        Object jobIdObj = context.getSharedData().get("jobId");
        if (jobIdObj == null) {
            return AgentActionResult.fail("缺少jobId，无法触发任务");
        }

        Long jobId = ((Number) jobIdObj).longValue();
        TriggerOnceResponse resp = schedulerOpenApiClient.triggerJob(jobId);

        if (!resp.isResult()) {
            return AgentActionResult.fail("调度中心返回触发失败");
        }

        context.getSharedData().put("jobInstanceId", resp.getInstanceId());

        AgentActionResult result = AgentActionResult.success();
        result.setMessage("trigger job success");
        result.getOutputs().put("jobId", jobId);
        result.getOutputs().put("jobInstanceId", resp.getInstanceId());
        result.getOutputs().put("response", safeJson(resp));
        return result;
    }

    private String safeJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return String.valueOf(obj);
        }
    }
}