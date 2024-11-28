package com.purepoint.youtubebatch.playlist_video;

import com.purepoint.youtubebatch.domain.Video;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlaylistVideoItemWriter implements ItemWriter<Video>  {

    private final PlaylistVideoRepository playlistVideoRepository;

    @Override
    @Transactional
    public void write(@NonNull Chunk<? extends Video> videos) {
        playlistVideoRepository.saveAll(videos);
        log.info("playlistVideo saved to database");
    }
}
