package com.my.agent.service.impl;

import com.my.agent.domain.vo.AgentRunDetailVO;
import com.my.agent.service.AgentOrchestratorService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AgentOrchestratorServiceImpl implements AgentOrchestratorService {

    @Override
    public String createRun(String goal) {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public void plan(String runId) {
        // 后面补：查run -> 调planner -> 落plan_json
    }

    @Override
    public void execute(String runId) {
        // 后面补：查plan -> 跑DAG执行器
    }

    @Override
    public AgentRunDetailVO getRunDetail(String runId) {
        AgentRunDetailVO vo = new AgentRunDetailVO();
        vo.setRunId(runId);
        vo.setStatus("INIT");
        return vo;
    }
}