package com.purepoint.youtubebatch;

import com.purepoint.youtubebatch.domain.Youtube;
import com.purepoint.youtubebatch.playlist.PlaylistItemProcessor;
import com.purepoint.youtubebatch.playlist.PlaylistItemReader;
import com.purepoint.youtubebatch.playlist.PlaylistItemWriter;
import com.purepoint.youtubebatch.video.VideoItemProcessor;
import com.purepoint.youtubebatch.video.VideoItemReader;
import com.purepoint.youtubebatch.video.VideoItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfiguration {

    @Bean
    public Job youtubeApiJob(JobRepository jobRepository, @Qualifier("youtubeVideoStep") Step step1,
                             @Qualifier("youtubePlaylistStep") Step step2,JobCompletionNotificationListener listener) {
        return new JobBuilder("youtubeApiJob", jobRepository)
                .listener(listener)
                .start(step1)
                .next(step2)
                .build();
    }

    @Bean
    public Step youtubeVideoStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               VideoItemReader videoItemReader,
                               VideoItemProcessor videoItemProcessor,
                               VideoItemWriter videoItemWriter
                               ) {
        return new StepBuilder("youtubeVideoStep", jobRepository)
                .<Youtube, Youtube>chunk(10, transactionManager)
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

    @Bean
    public Step youtubePlaylistStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               PlaylistItemReader playlistItemReader,
                               PlaylistItemProcessor playlistItemProcessor,
                               PlaylistItemWriter playlistItemWriter
    ) {
        return new StepBuilder("youtubePlaylistStep", jobRepository)
                .<Youtube, Youtube>chunk(10, transactionManager)
                .reader(playlistItemReader)
                .processor(playlistItemProcessor)
                .writer(playlistItemWriter)
                .build();
    }

    @Bean
    public PlaylistItemReader playlistItemReader() {
        return new PlaylistItemReader();
    }

    @Bean
    public PlaylistItemProcessor playlistItemProcessor() {
        return new PlaylistItemProcessor();
    }

    @Bean
    public PlaylistItemWriter playlistItemWriter() {
        return new PlaylistItemWriter();
    }
}
