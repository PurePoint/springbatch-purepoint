package com.purepoint.youtubebatch.playlist;

import com.purepoint.youtubebatch.domain.Youtube;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class PlaylistItemProcessor implements ItemProcessor<Youtube, Youtube> {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Override
    public Youtube process(Youtube youtube) throws Exception {
        // DB에 이미 존재하는지 검증
        if (playlistRepository.existsById(youtube.getItemId())) {
            return null;
        }

        return youtube;
    }
}
