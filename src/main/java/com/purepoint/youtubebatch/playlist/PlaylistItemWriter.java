package com.purepoint.youtubebatch.playlist;

import com.purepoint.youtubebatch.domain.Playlist;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaylistItemWriter implements ItemWriter<Playlist>  {

    private final PlaylistRepository playlistRepository;

    @Override
    public void write(@NonNull Chunk<? extends Playlist> playlists) {
        playlistRepository.saveAll(playlists);
    }
}
