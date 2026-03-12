package com.my.agent.executor.registry;

import com.my.agent.executor.handler.AgentActionHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ActionHandlerRegistry {

    private final Map<String, AgentActionHandler> handlerMap = new HashMap<>();

    public ActionHandlerRegistry(List<AgentActionHandler> handlers) {
        for (AgentActionHandler handler : handlers) {
            handlerMap.put(handler.action(), handler);
        }
    }

    public AgentActionHandler getHandler(String action) {
        AgentActionHandler handler = handlerMap.get(action);
        if (handler == null) {
            throw new IllegalArgumentException("未找到对应的ActionHandler: " + action);
        }
        return handler;
    }
}