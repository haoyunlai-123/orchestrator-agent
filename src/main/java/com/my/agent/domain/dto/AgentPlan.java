package com.my.agent.domain.dto;

import java.util.List;

public class AgentPlan {

    private String goal;
    private List<AgentPlanStep> steps;

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public List<AgentPlanStep> getSteps() {
        return steps;
    }

    public void setSteps(List<AgentPlanStep> steps) {
        this.steps = steps;
    }
}