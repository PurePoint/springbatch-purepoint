package com.purepoint.youtubebatch.playlist_video;

import com.purepoint.youtubebatch.domain.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PlaylistVideoItemWriter implements ItemWriter<Video>  {

    private final PlaylistVideoRepository playlistVideoRepository;

    @Transactional
    @Override
    public void write(Chunk<? extends Video> videos) {
        playlistVideoRepository.saveAll(videos);
    }
}
