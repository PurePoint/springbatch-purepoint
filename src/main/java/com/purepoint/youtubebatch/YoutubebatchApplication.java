package com.purepoint.youtubebatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableBatchProcessing
public class YoutubebatchApplication implements CommandLineRunner {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job youtubeApiJob;
    @Autowired
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(YoutubebatchApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // Job Parameters 설정
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            // Job 실행
            jobLauncher.run(youtubeApiJob, jobParameters);

            // Job 완료 후 애플리케이션 종료
            SpringApplication.exit(applicationContext, () -> 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

