package com.my.agent.repository;

import com.my.agent.domain.entity.AgentRunEntity;

public interface AgentRunRepository {

    void insert(AgentRunEntity entity);

    AgentRunEntity findByRunId(String runId);

    void updateStatus(String runId, String status);

    void updatePlan(String runId, String status, String planJson, String currentStepId);

    void updateFinalResult(String runId, String status, String finalResult, String errorMessage);
}