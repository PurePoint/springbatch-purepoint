package com.purepoint.youtubebatch.playlist;

import com.purepoint.youtubebatch.domain.VideoPlaylist;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class PlaylistItemProcessor implements ItemProcessor<VideoPlaylist, VideoPlaylist> {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Override
    public VideoPlaylist process(VideoPlaylist videoPlaylist) throws Exception {

        // DB에 이미 존재하는지 검증
        if (playlistRepository.existsById(videoPlaylist.getPlaylist().getPlaylistId())) {
            return null;
        }

        return videoPlaylist;
    }
}
