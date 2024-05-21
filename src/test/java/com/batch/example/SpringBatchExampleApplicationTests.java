package com.batch.example;

import com.batch.example.config.BatchConfig;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBatchTest
@EnableAutoConfiguration
@SpringJUnitConfig(classes = BatchConfig.class)
class SpringBatchExampleApplicationTests {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;


	@Test
	public void testJob() throws Exception {

		JobExecution jobExecution = jobLauncherTestUtils.launchJob();

		assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
	}
}
