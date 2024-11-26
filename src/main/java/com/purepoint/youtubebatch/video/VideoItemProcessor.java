package com.purepoint.youtubebatch.video;

import com.purepoint.youtubebatch.domain.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoItemProcessor implements ItemProcessor<Video, Video> {

    private final VideoRepository videoRepository;

    @Override
    public Video process(Video video) throws Exception {
        // DB에 이미 존재하는지 검증
        if (videoRepository.existsById(video.getVideoId())) {
            return null;
        }
        
        // 카테고리 분류
//        if(video.getVideoTitle().contains("자바") || video.getVideoDescription().contains("자바")) {
//            video.setVideoCategory(1);
//        }
//
//        if(video.getVideoTitle().contains("파이썬") || video.getVideoDescription().contains("파이썬")) {
//            video.setVideoCategory(2);
//        }
//
//        if(video.getVideoTitle().contains("클라우드") || video.getVideoDescription().contains("클라우드")) {
//            video.setVideoCategory(3);
//        }
//
//        if(video.getVideoTitle().contains("알고리즘") || video.getVideoDescription().contains("알고리즘")) {
//            video.setVideoCategory(4);
//        }
//
//        if(video.getVideoTitle().contains("네트워크") || video.getVideoDescription().contains("네트워크")) {
//            video.setVideoCategory(5);
//        }
//
//        if(video.getVideoCategory() == null) {
//            return null;
//        }

        return video;
    }
}
