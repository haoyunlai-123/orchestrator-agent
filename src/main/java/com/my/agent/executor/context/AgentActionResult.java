package com.my.agent.executor.context;

import java.util.HashMap;
import java.util.Map;

public class AgentActionResult {

    private boolean success;
    private String message;
    private Map<String, Object> outputs = new HashMap<>();

    public static AgentActionResult success() {
        AgentActionResult result = new AgentActionResult();
        result.setSuccess(true);
        return result;
    }

    public static AgentActionResult fail(String message) {
        AgentActionResult result = new AgentActionResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getOutputs() {
        return outputs;
    }

    public void setOutputs(Map<String, Object> outputs) {
        this.outputs = outputs;
    }
}