package com.purepoint.youtubebatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class YoutubeBatchApplication {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job youtubeApiJob1;
    @Autowired
    private Job youtubeApiJob2;

    public static void main(String[] args) {
        SpringApplication.run(YoutubeBatchApplication.class, args);
    }

    @Scheduled(cron = "0 0 1 * * Mon")
    public void scheduleJob1() {
        try {
            // Job Parameters 설정
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            // Job 실행
            jobLauncher.run(youtubeApiJob1, jobParameters);

            log.info("Job executed successfully at 1 AM");
        } catch (Exception e) {
            log.info("Exception occurred", e);
        }
    }

    @Scheduled(cron = "0 0 1 * * Tue")
    public void scheduleJob2() {
        try {
            // Job Parameters 설정
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            // Job 실행
            jobLauncher.run(youtubeApiJob2, jobParameters);

            log.info("Job executed successfully at 1 AM");
        } catch (Exception e) {
            log.info("Exception occurred", e);
        }
    }
}

