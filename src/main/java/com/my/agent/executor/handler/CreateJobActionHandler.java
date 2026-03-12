package com.my.agent.executor.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.agent.client.SchedulerOpenApiClient;
import com.my.agent.client.dto.CreateJobRequest;
import com.my.agent.domain.dto.AgentPlanStep;
import com.my.agent.executor.context.AgentActionContext;
import com.my.agent.executor.context.AgentActionResult;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CreateJobActionHandler implements AgentActionHandler {

    private final SchedulerOpenApiClient schedulerOpenApiClient;
    private final ObjectMapper objectMapper;

    public CreateJobActionHandler(SchedulerOpenApiClient schedulerOpenApiClient,
                                  ObjectMapper objectMapper) {
        this.schedulerOpenApiClient = schedulerOpenApiClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public String action() {
        return "CREATE_JOB";
    }

    @Override
    public AgentActionResult execute(AgentActionContext context, AgentPlanStep step) {
        Map<String, Object> params = step.getParams();

        CreateJobRequest req = new CreateJobRequest();
        req.setName((String) params.get("name"));
        req.setScheduleType((String) params.get("scheduleType"));
        req.setScheduleExpr((String) params.get("scheduleExpr"));
        req.setHandlerType((String) params.get("handlerType"));
        req.setHandlerParam((String) params.get("handlerParam"));
        req.setRouteStrategy((String) params.get("routeStrategy"));
        req.setRetryMax((Integer) params.get("retryMax"));
        req.setTimeoutMs((Integer) params.get("timeoutMs"));
        req.setEnabled((Boolean) params.get("enabled"));

        Long jobId = schedulerOpenApiClient.createJob(req);

        context.getSharedData().put("jobId", jobId);

        AgentActionResult result = AgentActionResult.success();
        result.setMessage("create job success");
        result.getOutputs().put("jobId", jobId);
        result.getOutputs().put("request", safeJson(req));
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