package com.my.agent.controller;

import com.my.agent.domain.dto.CreateAgentRunRequest;
import com.my.agent.domain.vo.AgentRunDetailVO;
import com.my.agent.service.AgentOrchestratorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/agent/runs")
public class AgentRunController {

    private final AgentOrchestratorService agentOrchestratorService;

    public AgentRunController(AgentOrchestratorService agentOrchestratorService) {
        this.agentOrchestratorService = agentOrchestratorService;
    }

    @PostMapping
    public Map<String, Object> createRun(@RequestBody @Valid CreateAgentRunRequest request) {
        String runId = agentOrchestratorService.createRun(request.getGoal());
        return Map.of(
                "runId", runId,
                "status", "INIT"
        );
    }

    @PostMapping("/{runId}/plan")
    public Map<String, Object> plan(@PathVariable String runId) {
        agentOrchestratorService.plan(runId);
        return Map.of("runId", runId, "message", "plan success");
    }

    @PostMapping("/{runId}/execute")
    public Map<String, Object> execute(@PathVariable String runId) {
        agentOrchestratorService.execute(runId);
        return Map.of("runId", runId, "message", "execute started");
    }

    @GetMapping("/{runId}")
    public AgentRunDetailVO getDetail(@PathVariable String runId) {
        return agentOrchestratorService.getRunDetail(runId);
    }
}