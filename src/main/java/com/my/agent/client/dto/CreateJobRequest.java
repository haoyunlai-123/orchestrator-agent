package com.my.agent.client.dto;

public class CreateJobRequest {

    private String name;
    private String scheduleType;
    private String scheduleExpr;
    private String handlerType;
    private String handlerParam;
    private String routeStrategy;
    private Integer retryMax;
    private Integer timeoutMs;
    private Boolean enabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public String getScheduleExpr() {
        return scheduleExpr;
    }

    public void setScheduleExpr(String scheduleExpr) {
        this.scheduleExpr = scheduleExpr;
    }

    public String getHandlerType() {
        return handlerType;
    }

    public void setHandlerType(String handlerType) {
        this.handlerType = handlerType;
    }

    public String getHandlerParam() {
        return handlerParam;
    }

    public void setHandlerParam(String handlerParam) {
        this.handlerParam = handlerParam;
    }

    public String getRouteStrategy() {
        return routeStrategy;
    }

    public void setRouteStrategy(String routeStrategy) {
        this.routeStrategy = routeStrategy;
    }

    public Integer getRetryMax() {
        return retryMax;
    }

    public void setRetryMax(Integer retryMax) {
        this.retryMax = retryMax;
    }

    public Integer getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(Integer timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}