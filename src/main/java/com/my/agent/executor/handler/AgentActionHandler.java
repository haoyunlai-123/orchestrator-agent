package com.my.agent.executor.handler;

import com.my.agent.domain.dto.AgentPlanStep;
import com.my.agent.executor.context.AgentActionContext;
import com.my.agent.executor.context.AgentActionResult;

public interface AgentActionHandler {

    String action();

    AgentActionResult execute(AgentActionContext context, AgentPlanStep step);
}