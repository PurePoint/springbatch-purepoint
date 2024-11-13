package com.purepoint.youtubebatch;

import com.purepoint.youtubebatch.domain.Video;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfiguration {

    @Bean
    public Job youtubeApiJob(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) {
        return new JobBuilder("youtubeApiJob", jobRepository)
                .listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    public Step youtubeApiStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               VideoItemReader videoItemReader,
                               VideoItemProcessor videoItemProcessor,
                               VideoItemWriter videoItemWriter
                               ) {
        return new StepBuilder("youtubeApiStep", jobRepository)
                .<Video, Video>chunk(10, transactionManager)
                .reader(videoItemReader)
                .processor(videoItemProcessor)
                .writer(videoItemWriter)
                .build();
    }

    @Bean
    public VideoItemReader videoItemReader() {
        return new VideoItemReader();
    }

    @Bean
    public VideoItemProcessor videoItemProcessor() {
        return new VideoItemProcessor();
    }

    @Bean
    public VideoItemWriter videoItemWriter() {
        return new VideoItemWriter();
    }
}
