package com.purepoint.youtubebatch;

import com.purepoint.youtubebatch.domain.Video;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class VideoItemProcessor implements ItemProcessor<Video, Video> {

    @Autowired
    private VideoRepository videoRepository;

    @Override
    public Video process(Video video) throws Exception {
        if (videoRepository.existsById(video.getVideoId())) {
            return null;
        }
        return video;
    }
}
