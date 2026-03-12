package com.my.agent.executor.handler;

import com.my.agent.client.dto.JobInstanceDTO;
import com.my.agent.domain.dto.AgentPlanStep;
import com.my.agent.executor.context.AgentActionContext;
import com.my.agent.executor.context.AgentActionResult;
import org.springframework.stereotype.Component;

@Component
public class SummarizeResultActionHandler implements AgentActionHandler {

    @Override
    public String action() {
        return "SUMMARIZE_RESULT";
    }

    @Override
    public AgentActionResult execute(AgentActionContext context, AgentPlanStep step) {
        Object jobIdObj = context.getSharedData().get("jobId");
        Object instanceIdObj = context.getSharedData().get("jobInstanceId");
        Object finalStatusObj = context.getSharedData().get("finalJobStatus");
        Object instanceObj = context.getSharedData().get("finalJobInstance");

        Long jobId = jobIdObj == null ? null : ((Number) jobIdObj).longValue();
        Long instanceId = instanceIdObj == null ? null : ((Number) instanceIdObj).longValue();
        String status = finalStatusObj == null ? "UNKNOWN" : String.valueOf(finalStatusObj);

        StringBuilder sb = new StringBuilder();
        sb.append("任务编排执行完成。");
        if (jobId != null) {
            sb.append(" jobId=").append(jobId).append(";");
        }
        if (instanceId != null) {
            sb.append(" instanceId=").append(instanceId).append(";");
        }
        sb.append(" 最终状态=").append(status).append(";");

        if (instanceObj instanceof JobInstanceDTO dto) {
            if (dto.getRetryCount() != null) {
                sb.append(" 重试次数=").append(dto.getRetryCount()).append(";");
            }
            if (dto.getExecuteResult() != null) {
                sb.append(" 执行结果=").append(dto.getExecuteResult()).append(";");
            }
            if (dto.getErrorMessage() != null) {
                sb.append(" 错误信息=").append(dto.getErrorMessage()).append(";");
            }
        }

        String summary = sb.toString();
        context.getSharedData().put("summary", summary);

        AgentActionResult result = AgentActionResult.success();
        result.setMessage("summarize success");
        result.getOutputs().put("summary", summary);
        return result;
    }
}