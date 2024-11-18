package com.purepoint.youtubebatch.playlist;

import com.purepoint.youtubebatch.domain.Youtube;
import lombok.NonNull;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlaylistItemWriter implements ItemWriter<Youtube>  {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Override
    public void write(@NonNull Chunk<? extends Youtube> youtube) {
        playlistRepository.saveAll(youtube);
    }
}
