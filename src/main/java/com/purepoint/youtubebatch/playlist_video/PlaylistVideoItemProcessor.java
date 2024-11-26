package com.purepoint.youtubebatch.playlist_video;

import com.purepoint.youtubebatch.domain.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaylistVideoItemProcessor implements ItemProcessor<Video, Video> {

    private final PlaylistVideoRepository playlistVideoRepository;

    @Override
    public Video process(Video video) throws Exception {

        // DB에 이미 존재하는지 검증
        if (playlistVideoRepository.existsById(video.getVideoId())) {
            return null;
        }

        return video;
    }
}
