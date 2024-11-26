package com.purepoint.youtubebatch.playlist;

import com.purepoint.youtubebatch.domain.Playlist;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaylistItemProcessor implements ItemProcessor<Playlist, Playlist> {

    private final PlaylistRepository playlistRepository;

    @Override
    public Playlist process(Playlist playlist) throws Exception {

        // DB에 이미 존재하는지 검증
        if (playlistRepository.existsById(playlist.getPlaylistId())) {
            return null;
        }

        return playlist;
    }
}
