package com.my.agent.planner;

import com.my.agent.domain.dto.AgentPlan;
import com.my.agent.domain.dto.AgentPlanStep;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MockAgentPlanner implements AgentPlanner {

    @Override
    public AgentPlan generatePlan(String goal) {
        AgentPlan plan = new AgentPlan();
        plan.setGoal(goal);

        AgentPlanStep s1 = new AgentPlanStep();
        s1.setStepId("s1");
        s1.setStepIndex(1);
        s1.setName("创建健康检查任务");
        s1.setAction("CREATE_JOB");
        s1.setDependsOn(List.of());
        s1.setParams(Map.of(
                "name", "health-check-job",
                "scheduleType", "FIXED_RATE",
                "scheduleExpr", "5000",
                "handlerType", "HTTP",
                "handlerParam", "{\"url\":\"http://127.0.0.1:9002/health\",\"method\":\"GET\"}",
                "routeStrategy", "ROUND_ROBIN",
                "retryMax", 1,
                "timeoutMs", 3000,
                "enabled", false
        ));

        AgentPlanStep s2 = new AgentPlanStep();
        s2.setStepId("s2");
        s2.setStepIndex(2);
        s2.setName("触发任务执行");
        s2.setAction("TRIGGER_JOB");
        s2.setDependsOn(List.of("s1"));
        s2.setParams(Map.of());

        AgentPlanStep s3 = new AgentPlanStep();
        s3.setStepId("s3");
        s3.setStepIndex(3);
        s3.setName("等待任务完成");
        s3.setAction("WAIT_JOB_FINISH");
        s3.setDependsOn(List.of("s2"));
        s3.setParams(Map.of(
                "pollIntervalMs", 1000,
                "maxWaitMs", 10000
        ));

        AgentPlanStep s4 = new AgentPlanStep();
        s4.setStepId("s4");
        s4.setStepIndex(4);
        s4.setName("汇总执行结果");
        s4.setAction("SUMMARIZE_RESULT");
        s4.setDependsOn(List.of("s3"));
        s4.setParams(Map.of());

        plan.setSteps(List.of(s1, s2, s3, s4));
        return plan;
    }
}
