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
        // DB에서 이미 존재하는 경우
        return playlistVideoRepository.findById(video.getVideoId())
                .map(existingVideo -> {
                    // 기존 엔티티를 빌더로 복사 및 업데이트
                    return Video.builder()
                            // 동일 유지
                            .videoId(existingVideo.getVideoId())
                            .videoKind(existingVideo.getVideoKind())
                            .videoTitle(existingVideo.getVideoTitle())
                            .videoDescription(existingVideo.getVideoDescription())
                            .videoThumbnail(existingVideo.getVideoThumbnail())
                            .videoPublishedAt(existingVideo.getVideoPublishedAt())
                            .videoCategory(existingVideo.getVideoCategory())
                            .videoLikes(existingVideo.getVideoLikes())
                            // 새 데이터로 업데이트
                            .videoPosition(video.getVideoPosition())
                            .playlistId(video.getPlaylistId())
                            .build();
                })
                .orElse(video); // 존재하지 않을 경우 새 객체 그대로 반환
    }
}
