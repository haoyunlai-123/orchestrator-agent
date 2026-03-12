package com.my.agent.repository;

import com.my.agent.domain.entity.AgentRunEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AgentRunRepositoryImpl implements AgentRunRepository {

    private final JdbcTemplate jdbcTemplate;

    public AgentRunRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<AgentRunEntity> rowMapper = (rs, rowNum) -> {
        AgentRunEntity entity = new AgentRunEntity();
        entity.setId(rs.getLong("id"));
        entity.setRunId(rs.getString("run_id"));
        entity.setGoal(rs.getString("goal"));
        entity.setStatus(rs.getString("status"));
        entity.setPlanJson(rs.getString("plan_json"));
        entity.setCurrentStepId(rs.getString("current_step_id"));
        entity.setFinalResult(rs.getString("final_result"));
        entity.setErrorMessage(rs.getString("error_message"));
        if (rs.getTimestamp("created_at") != null) {
            entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            entity.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return entity;
    };

    @Override
    public void insert(AgentRunEntity entity) {
        String sql = """
                INSERT INTO agent_run
                (run_id, goal, status, plan_json, current_step_id, final_result, error_message)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                entity.getRunId(),
                entity.getGoal(),
                entity.getStatus(),
                entity.getPlanJson(),
                entity.getCurrentStepId(),
                entity.getFinalResult(),
                entity.getErrorMessage());
    }

    @Override
    public AgentRunEntity findByRunId(String runId) {
        String sql = "SELECT * FROM agent_run WHERE run_id = ?";
        return jdbcTemplate.query(sql, rowMapper, runId)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public void updateStatus(String runId, String status) {
        String sql = "UPDATE agent_run SET status = ? WHERE run_id = ?";
        jdbcTemplate.update(sql, status, runId);
    }

    @Override
    public void updatePlan(String runId, String status, String planJson, String currentStepId) {
        String sql = """
                UPDATE agent_run
                SET status = ?, plan_json = ?, current_step_id = ?
                WHERE run_id = ?
                """;
        jdbcTemplate.update(sql, status, planJson, currentStepId, runId);
    }

    @Override
    public void updateFinalResult(String runId, String status, String finalResult, String errorMessage) {
        String sql = """
                UPDATE agent_run
                SET status = ?, final_result = ?, error_message = ?
                WHERE run_id = ?
                """;
        jdbcTemplate.update(sql, status, finalResult, errorMessage, runId);
    }
}