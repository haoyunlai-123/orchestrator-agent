package com.my.agent.repository;

import com.my.agent.domain.entity.AgentStepRunEntity;

import java.util.List;

public interface AgentStepRunRepository {

    void batchInsert(List<AgentStepRunEntity> steps);

    java.util.List<AgentStepRunEntity> findByRunId(String runId);

    void updateStepStatus(String runId, String stepId, String status, String errorMessage);

    void updateStepRunning(String runId, String stepId);

    void updateStepSuccess(String runId, String stepId, String outputJson, Long jobId, Long jobInstanceId);

    AgentStepRunEntity findByRunIdAndStepId(String runId, String stepId);

    void updateStepFailed(String runId, String stepId, String errorMessage);
}