package com.my.agent.executor.context;

import com.my.agent.domain.dto.AgentPlan;

import java.util.HashMap;
import java.util.Map;

public class AgentActionContext {

    private String runId;
    private String currentStepId;
    private AgentPlan plan;
    private Map<String, Object> sharedData = new HashMap<>();

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getCurrentStepId() {
        return currentStepId;
    }

    public void setCurrentStepId(String currentStepId) {
        this.currentStepId = currentStepId;
    }

    public AgentPlan getPlan() {
        return plan;
    }

    public void setPlan(AgentPlan plan) {
        this.plan = plan;
    }

    public Map<String, Object> getSharedData() {
        return sharedData;
    }

    public void setSharedData(Map<String, Object> sharedData) {
        this.sharedData = sharedData;
    }
}