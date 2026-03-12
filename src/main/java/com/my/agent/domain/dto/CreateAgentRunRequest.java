package com.my.agent.domain.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateAgentRunRequest {

    @NotBlank(message = "goal不能为空")
    private String goal;

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }
}