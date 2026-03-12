package com.my.agent.executor.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.agent.client.SchedulerOpenApiClient;
import com.my.agent.client.dto.JobInstanceDTO;
import com.my.agent.domain.dto.AgentPlanStep;
import com.my.agent.executor.context.AgentActionContext;
import com.my.agent.executor.context.AgentActionResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class WaitJobFinishActionHandler implements AgentActionHandler {

    private final SchedulerOpenApiClient schedulerOpenApiClient;
    private final ObjectMapper objectMapper;

    public WaitJobFinishActionHandler(SchedulerOpenApiClient schedulerOpenApiClient,
                                      ObjectMapper objectMapper) {
        this.schedulerOpenApiClient = schedulerOpenApiClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public String action() {
        return "WAIT_JOB_FINISH";
    }

    @Override
    public AgentActionResult execute(AgentActionContext context, AgentPlanStep step) {
        Object jobIdObj = context.getSharedData().get("jobId");
        Object instanceIdObj = context.getSharedData().get("jobInstanceId");

        if (jobIdObj == null || instanceIdObj == null) {
            return AgentActionResult.fail("缺少jobId或jobInstanceId，无法等待任务完成");
        }

        Long jobId = ((Number) jobIdObj).longValue();
        Long instanceId = ((Number) instanceIdObj).longValue();

        Map<String, Object> params = step.getParams();
        int pollIntervalMs = ((Number) params.getOrDefault("pollIntervalMs", 1000)).intValue();
        int maxWaitMs = ((Number) params.getOrDefault("maxWaitMs", 10000)).intValue();

        long deadline = System.currentTimeMillis() + maxWaitMs;

        while (System.currentTimeMillis() < deadline) {
            List<JobInstanceDTO> instances = schedulerOpenApiClient.latestInstances(jobId, 20);
            JobInstanceDTO target = instances.stream()
                    .filter(it -> it.getId() != null && it.getId().equals(instanceId))
                    .findFirst()
                    .orElse(null);

            if (target != null) {
                String status = target.getStatus();
                if (isSuccess(status)) {
                    context.getSharedData().put("finalJobStatus", status);
                    context.getSharedData().put("finalJobInstance", target);

                    AgentActionResult result = AgentActionResult.success();
                    result.setMessage("job finished successfully");
                    result.getOutputs().put("jobId", jobId);
                    result.getOutputs().put("jobInstanceId", instanceId);
                    result.getOutputs().put("jobStatus", status);
                    result.getOutputs().put("instance", safeJson(target));
                    return result;
                }

                if (isFailed(status)) {
                    context.getSharedData().put("finalJobStatus", status);
                    context.getSharedData().put("finalJobInstance", target);
                    return AgentActionResult.fail("job执行失败, status=" + status);
                }
            }

            try {
                Thread.sleep(pollIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return AgentActionResult.fail("等待任务完成被中断");
            }
        }

        return AgentActionResult.fail("等待任务完成超时");
    }

    private boolean isSuccess(String status) {
        return "SUCCESS".equalsIgnoreCase(status);
    }

    private boolean isFailed(String status) {
        return "FAILED".equalsIgnoreCase(status) || "TIMEOUT".equalsIgnoreCase(status);
    }

    private String safeJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return String.valueOf(obj);
        }
    }
}