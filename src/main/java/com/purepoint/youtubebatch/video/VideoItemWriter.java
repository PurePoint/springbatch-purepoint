package com.purepoint.youtubebatch.video;

import com.purepoint.youtubebatch.domain.Youtube;
import lombok.NonNull;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VideoItemWriter implements ItemWriter<Youtube> {

    @Autowired
    private VideoRepository videoRepository;

    @Override
    public void write(@NonNull Chunk<? extends Youtube> videos) {
        videoRepository.saveAll(videos);
    }
}
