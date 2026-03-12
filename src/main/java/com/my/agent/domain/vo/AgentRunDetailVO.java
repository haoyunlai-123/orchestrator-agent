package com.my.agent.domain.vo;

import java.util.List;

public class AgentRunDetailVO {

    private String runId;
    private String goal;
    private String status;
    private String currentStepId;
    private String finalResult;
    private String errorMessage;
    private List<AgentStepDetailVO> steps;

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentStepId() {
        return currentStepId;
    }

    public void setCurrentStepId(String currentStepId) {
        this.currentStepId = currentStepId;
    }

    public String getFinalResult() {
        return finalResult;
    }

    public void setFinalResult(String finalResult) {
        this.finalResult = finalResult;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<AgentStepDetailVO> getSteps() {
        return steps;
    }

    public void setSteps(List<AgentStepDetailVO> steps) {
        this.steps = steps;
    }
}