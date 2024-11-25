package com.purepoint.youtubebatch;

import com.purepoint.youtubebatch.domain.Video;
import com.purepoint.youtubebatch.domain.VideoPlaylist;
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


    /**
     * YouTube API 데이터를 처리하는 `youtubeVideoStep`로 구성된
     * Spring Batch Job을 정의합니다. 이 Job은 완료 알림을 위한 Listener와 연동됩니다.
     *
     * @param jobRepository Job 실행 및 구성을 위한 저장소
     * @param step YouTube 동영상 관련 데이터를 처리
     * @param listener Job 완료 시 알림을 제공하는 Listener
     * @return "youtubeApiJob1"이라는 이름의 구성된 Job 인스턴스
     */

    @Bean
    public Job youtubeApiJob1(JobRepository jobRepository,
                              @Qualifier("youtubeVideoStep") Step step,
                              JobCompletionNotificationListener listener) {
        return new JobBuilder("youtubeApiJob1", jobRepository)
                .listener(listener)
                .start(step)
                .build();
    }

    /**
     * YouTube API 데이터를 처리하는 `youtubePlaylistStep`로 구성된
     * Spring Batch Job을 정의합니다. 이 Job은 완료 알림을 위한 Listener와 연동됩니다.
     *
     * @param jobRepository Job 실행 및 구성을 위한 저장소
     * @param step YouTube 재생목록 관련 데이터를 처리
     * @param listener Job 완료 시 알림을 제공하는 Listener
     * @return "youtubeApiJob2"이라는 이름의 구성된 Job 인스턴스
     */

    @Bean
    public Job youtubeApiJob2(JobRepository jobRepository,
                             @Qualifier("youtubePlaylistStep") Step step,
                              JobCompletionNotificationListener listener) {
        return new JobBuilder("youtubeApiJob2", jobRepository)
                .listener(listener)
                .start(step)
                .build();
    }

    /**
     * YouTube 동영상 데이터를 처리하는 Batch Step을 정의합니다.
     * 데이터는 Reader, Processor, Writer의 3단계로 처리되며, 트랜잭션 관리를 포함합니다.
     *
     * @param jobRepository Job 실행 및 구성을 위한 저장소
     * @param transactionManager 트랜잭션 관리 매니저
     * @param videoItemReader YouTube 동영상 데이터를 읽어오는 Reader
     * @param videoItemProcessor YouTube 동영상 데이터를 가공하는 Processor
     * @param videoItemWriter 처리된 데이터를 저장하는 Writer
     * @return "youtubeVideoStep"이라는 이름의 구성된 Step 인스턴스
     */
    @Bean
    public Step youtubeVideoStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               VideoItemReader videoItemReader,
                               VideoItemProcessor videoItemProcessor,
                               VideoItemWriter videoItemWriter
                               ) {
        return new StepBuilder("youtubeVideoStep", jobRepository)
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

    /**
     * YouTube 재생목록 데이터를 처리하는 Batch Step을 정의합니다.
     * 데이터는 Reader, Processor, Writer의 3단계로 처리되며, 트랜잭션 관리를 포함합니다.
     *
     * @param jobRepository Job 실행 및 구성을 위한 저장소
     * @param transactionManager 트랜잭션 관리 매니저
     * @param playlistItemReader YouTube 재생목록 데이터를 읽어오는 Reader
//     * @param playlistItemProcessor YouTube 재생목록 데이터를 가공하는 Processor
     * @param playlistItemWriter 처리된 데이터를 저장하는 Writer
     * @return "youtubePlaylistStep"이라는 이름의 구성된 Step 인스턴스
     */
    @Bean
    public Step youtubePlaylistStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               PlaylistItemReader playlistItemReader,
//                               PlaylistItemProcessor playlistItemProcessor,
                               PlaylistItemWriter playlistItemWriter
    ) {
        return new StepBuilder("youtubePlaylistStep", jobRepository)
                .<VideoPlaylist, VideoPlaylist>chunk(10, transactionManager)
                .reader(playlistItemReader)
//                .processor(playlistItemProcessor)
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
