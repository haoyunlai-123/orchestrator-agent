package com.my.agent.service;

import com.my.agent.domain.vo.AgentRunDetailVO;

public interface AgentOrchestratorService {

    String createRun(String goal);

    void plan(String runId);

    void execute(String runId);

    AgentRunDetailVO getRunDetail(String runId);
}
