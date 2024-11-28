package com.purepoint.youtubebatch;

import com.purepoint.youtubebatch.domain.Playlist;
import com.purepoint.youtubebatch.domain.Video;
import com.purepoint.youtubebatch.playlist.PlaylistItemProcessor;
import com.purepoint.youtubebatch.playlist.PlaylistItemReader;
import com.purepoint.youtubebatch.playlist.PlaylistItemWriter;
import com.purepoint.youtubebatch.playlist_video.PlaylistVideoItemProcessor;
import com.purepoint.youtubebatch.playlist_video.PlaylistVideoItemReader;
import com.purepoint.youtubebatch.playlist_video.PlaylistVideoItemWriter;
import com.purepoint.youtubebatch.video.VideoItemProcessor;
import com.purepoint.youtubebatch.video.VideoItemReader;
import com.purepoint.youtubebatch.video.VideoItemWriter;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class BatchConfiguration {

    private final JobRepository jobRepository;
    private final JobCompletionNotificationListener jobCompletionNotificationListener;
    private final PlatformTransactionManager transactionManager;
    private final VideoItemReader videoItemReader;
    private final VideoItemProcessor videoItemProcessor;
    private final VideoItemWriter videoItemWriter;
    private final PlaylistItemReader playlistItemReader;
    private final PlaylistItemProcessor playlistItemProcessor;
    private final PlaylistItemWriter playlistItemWriter;
    private final PlaylistVideoItemReader playlistVideoItemReader;
    private final PlaylistVideoItemProcessor playlistVideoItemProcessor;
    private final PlaylistVideoItemWriter playlistVideoItemWriter;


    /**
     * 동영상 데이터를 처리하기 위한 배치 작업을 정의합니다.
     *
     * @param step 이 작업에서 실행할 단계
     * @return 설정된 {@link Job} 인스턴스
     */
    @Bean
    public Job youtubeApiJob1(@Qualifier("youtubeVideoStep") Step step) {
        return new JobBuilder("youtubeApiJob1", jobRepository)
                .listener(jobCompletionNotificationListener)
                .start(step)
                .build();
    }

    /**
     * 동영상 데이터를 처리하기 위한 단계를 정의합니다.
     * 이 단계는 {@link Video} 항목을 읽고, 처리하고, 저장합니다.
     *
     * @return 설정된 {@link Step} 인스턴스
     */
    @Bean
    public Step youtubeVideoStep() {
        return new StepBuilder("youtubeVideoStep", jobRepository)
                .<Video, Video>chunk(10, transactionManager)
                .reader(videoItemReader)
                .processor(videoItemProcessor)
                .writer(videoItemWriter)
                .build();
    }

    /**
     * 재생목록 및 재생목록-동영상 데이터를 처리하기 위한 배치 작업을 정의합니다.
     *
     * @param step1 재생목록 데이터를 처리할 단계
     * @param step2 재생목록-동영상 데이터를 처리할 단계
     * @return 설정된 {@link Job} 인스턴스
     */
    @Bean
    public Job youtubeApiJob2(@Qualifier("youtubePlaylistStep") Step step1,
                              @Qualifier("youtubePlaylistVideoStep") Step step2) {
        return new JobBuilder("youtubeApiJob2", jobRepository)
                .listener(jobCompletionNotificationListener)
                .start(step1)
                .next(step2)
                .build();
    }

    /**
     * 재생목록 데이터를 처리하기 위한 단계를 정의합니다.
     * 이 단계는 {@link Playlist} 항목을 읽고, 처리하고, 저장합니다.
     *
     * @return 설정된 {@link Step} 인스턴스
     */
    @Bean
    public Step youtubePlaylistStep() {
        return new StepBuilder("youtubePlaylistStep", jobRepository)
                .<Playlist, Playlist>chunk(10, transactionManager)
                .reader(playlistItemReader)
                .processor(playlistItemProcessor)
                .writer(playlistItemWriter)
                .build();
    }

    /**
     * 재생목록-동영상 데이터를 처리하기 위한 단계를 정의합니다.
     * 이 단계는 {@link Video} 항목을 읽고, 처리하고, 저장합니다.
     *
     * @return 설정된 {@link Step} 인스턴스
     */
    @Bean
    public Step youtubePlaylistVideoStep() {
        return new StepBuilder("youtubePlaylistVideoStep", jobRepository)
                .<Video, Video>chunk(100, transactionManager)
                .reader(playlistVideoItemReader)
                .processor(playlistVideoItemProcessor)
                .writer(playlistVideoItemWriter)
                .build();
    }

}
