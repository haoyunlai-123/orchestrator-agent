package com.my.agent.client;

import com.my.agent.client.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class SchedulerOpenApiClient {

    private final RestTemplate restTemplate;

    @Value("${scheduler.base-url}")
    private String baseUrl;

    public SchedulerOpenApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Long createJob(CreateJobRequest request) {
        String url = baseUrl + "/api/jobs";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateJobRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ApiResponse<Long>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ApiResponse<Long>>() {}
        );

        ApiResponse<Long> body = response.getBody();
        if (body == null || !body.success() || body.getData() == null) {
            throw new IllegalStateException("create job失败: " + (body == null ? "响应为空" : body.getMsg()));
        }
        return body.getData();
    }

    public TriggerOnceResponse triggerJob(Long jobId) {
        String url = baseUrl + "/api/jobs/" + jobId + "/trigger";

        HttpEntity<Void> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<ApiResponse<TriggerOnceResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ApiResponse<TriggerOnceResponse>>() {}
        );

        ApiResponse<TriggerOnceResponse> body = response.getBody();
        if (body == null || !body.success() || body.getData() == null) {
            throw new IllegalStateException("trigger job失败: " + (body == null ? "响应为空" : body.getMsg()));
        }
        return body.getData();
    }

    public List<JobInstanceDTO> latestInstances(Long jobId, int limit) {
        String url = baseUrl + "/api/jobs/" + jobId + "/instances?limit=" + limit;

        HttpEntity<Void> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<ApiResponse<List<JobInstanceDTO>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<ApiResponse<List<JobInstanceDTO>>>() {}
        );

        ApiResponse<List<JobInstanceDTO>> body = response.getBody();
        if (body == null || !body.success() || body.getData() == null) {
            throw new IllegalStateException("query job instances失败: " + (body == null ? "响应为空" : body.getMsg()));
        }
        return body.getData();
    }
}