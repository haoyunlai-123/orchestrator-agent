package com.my.agent.repository;

import com.my.agent.domain.entity.AgentStepRunEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AgentStepRunRepositoryImpl implements AgentStepRunRepository {

    private final JdbcTemplate jdbcTemplate;

    public AgentStepRunRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<AgentStepRunEntity> rowMapper = (rs, rowNum) -> {
        AgentStepRunEntity entity = new AgentStepRunEntity();
        entity.setId(rs.getLong("id"));
        entity.setRunId(rs.getString("run_id"));
        entity.setStepId(rs.getString("step_id"));
        entity.setStepIndex(rs.getInt("step_index"));
        entity.setAction(rs.getString("action"));
        entity.setStepName(rs.getString("step_name"));
        entity.setDependsOn(rs.getString("depends_on"));
        entity.setStatus(rs.getString("status"));
        entity.setInputJson(rs.getString("input_json"));
        entity.setOutputJson(rs.getString("output_json"));
        Object jobId = rs.getObject("job_id");
        if (jobId != null) {
            entity.setJobId(((Number) jobId).longValue());
        }
        Object jobInstanceId = rs.getObject("job_instance_id");
        if (jobInstanceId != null) {
            entity.setJobInstanceId(((Number) jobInstanceId).longValue());
        }
        entity.setRetryCount(rs.getInt("retry_count"));
        entity.setErrorMessage(rs.getString("error_message"));

        if (rs.getTimestamp("started_at") != null) {
            entity.setStartedAt(rs.getTimestamp("started_at").toLocalDateTime());
        }
        if (rs.getTimestamp("ended_at") != null) {
            entity.setEndedAt(rs.getTimestamp("ended_at").toLocalDateTime());
        }
        if (rs.getTimestamp("created_at") != null) {
            entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            entity.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return entity;
    };

    @Override
    public void batchInsert(List<AgentStepRunEntity> steps) {
        String sql = """
                INSERT INTO agent_step_run
                (run_id, step_id, step_index, action, step_name, depends_on, status,
                 input_json, output_json, job_id, job_instance_id, retry_count, error_message,
                 started_at, ended_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(sql, steps, steps.size(), (ps, step) -> {
            ps.setString(1, step.getRunId());
            ps.setString(2, step.getStepId());
            ps.setInt(3, step.getStepIndex());
            ps.setString(4, step.getAction());
            ps.setString(5, step.getStepName());
            ps.setString(6, step.getDependsOn());
            ps.setString(7, step.getStatus());
            ps.setString(8, step.getInputJson());
            ps.setString(9, step.getOutputJson());

            if (step.getJobId() != null) {
                ps.setLong(10, step.getJobId());
            } else {
                ps.setObject(10, null);
            }

            if (step.getJobInstanceId() != null) {
                ps.setLong(11, step.getJobInstanceId());
            } else {
                ps.setObject(11, null);
            }

            ps.setInt(12, step.getRetryCount() == null ? 0 : step.getRetryCount());
            ps.setString(13, step.getErrorMessage());

            if (step.getStartedAt() != null) {
                ps.setTimestamp(14, java.sql.Timestamp.valueOf(step.getStartedAt()));
            } else {
                ps.setObject(14, null);
            }

            if (step.getEndedAt() != null) {
                ps.setTimestamp(15, java.sql.Timestamp.valueOf(step.getEndedAt()));
            } else {
                ps.setObject(15, null);
            }
        });
    }

    @Override
    public List<AgentStepRunEntity> findByRunId(String runId) {
        String sql = "SELECT * FROM agent_step_run WHERE run_id = ? ORDER BY step_index ASC";
        return jdbcTemplate.query(sql, rowMapper, runId);
    }
}