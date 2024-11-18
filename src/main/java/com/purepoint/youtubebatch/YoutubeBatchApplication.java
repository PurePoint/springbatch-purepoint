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
    private Job youtubeApiJob;

    public static void main(String[] args) {
        SpringApplication.run(YoutubeBatchApplication.class, args);
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void scheduleJob() {
        try {
            // Job Parameters 설정
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            // Job 실행
            jobLauncher.run(youtubeApiJob, jobParameters);

            log.info("Job executed successfully at 1 AM");
        } catch (Exception e) {
            log.info("Exception occurred", e);
        }
    }
}

