package com.purepoint.youtubebatch.video;

import com.purepoint.youtubebatch.domain.Video;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoItemWriter implements ItemWriter<Video> {

    private final VideoRepository videoRepository;

    @Override
    public void write(@NonNull Chunk<? extends Video> videos) {
        videoRepository.saveAll(videos);
    }
}
