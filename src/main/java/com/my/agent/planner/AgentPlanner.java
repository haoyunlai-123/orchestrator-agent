package com.my.agent.planner;

import com.my.agent.domain.dto.AgentPlan;

public interface AgentPlanner {

    AgentPlan generatePlan(String goal);
}
