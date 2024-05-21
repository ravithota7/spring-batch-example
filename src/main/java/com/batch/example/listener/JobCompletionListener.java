package com.batch.example.listener;

import com.batch.example.domain.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;


public class JobCompletionListener implements JobExecutionListener {
    private static final Logger log = LoggerFactory.getLogger(JobCompletionListener.class);

    private final JdbcTemplate jdbcTemplate;

    public JobCompletionListener(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Started Trade load job");
        JobExecutionListener.super.beforeJob(jobExecution);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus()== BatchStatus.COMPLETED){
            log.info("Ended Trade load job");
            log.info("Querying trades from db :");

            jdbcTemplate.query("select transaction_id, ticker, security_description, price, quantity, reporting_currency," +
                    " fx_rate, trade_type, transaction_date from trades", new DataClassRowMapper<>(Trade.class))
                    .forEach(trade -> log.info(String.valueOf(trade)));

            log.info("details in batch_job_instance table");
            jdbcTemplate.queryForList("select * from batch_job_instance")
                    .forEach( bji-> log.info(bji.toString()));

            log.info("details in batch_job_execution table");
            jdbcTemplate.queryForList("select * from batch_job_execution")
                    .forEach( bje-> log.info(bje.toString()));

            log.info("details in batch_job_execution_context table");
            jdbcTemplate.queryForList("select * from batch_job_execution_context")
                    .forEach( bjec-> log.info(bjec.toString()));

            log.info("details in batch_job_execution_params table");
            jdbcTemplate.queryForList("select * from batch_job_execution_params")
                    .forEach( bjep-> log.info(bjep.toString()));

            log.info("details in batch_step_execution table");
            jdbcTemplate.queryForList("select * from batch_step_execution")
                    .forEach( bse-> log.info(bse.toString()));

            log.info("details in batch_step_execution_context table");
            jdbcTemplate.queryForList("select * from batch_step_execution_context")
                    .forEach( bsec-> log.info(bsec.toString()));
        }
    }
}
