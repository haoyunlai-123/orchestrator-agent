package com.my.agent.repository;

import com.my.agent.domain.entity.AgentStepRunEntity;

import java.util.List;

public interface AgentStepRunRepository {

    void batchInsert(List<AgentStepRunEntity> steps);

    java.util.List<AgentStepRunEntity> findByRunId(String runId);
}