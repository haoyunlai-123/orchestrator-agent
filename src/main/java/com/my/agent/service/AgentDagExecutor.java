package com.my.agent.service;

import com.my.agent.domain.dto.AgentPlan;

public interface AgentDagExecutor {

    void execute(String runId, AgentPlan plan);
}
