package com.purepoint.youtubebatch;

import com.purepoint.youtubebatch.domain.Video;
import com.purepoint.youtubebatch.playlist.PlaylistRepository;
import com.purepoint.youtubebatch.playlist.TokenRepository;
import com.purepoint.youtubebatch.playlist_video.PlaylistVideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Slf4j
public class BatchTest {

    @Autowired
    private YoutubeBatchApplication youtubeBatchApplication;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private PlaylistVideoRepository playlistVideoRepository;

    private static List<Video> videos = new ArrayList<>();

    @Test
    @DisplayName("유튜브 videos 수동 테스트")
    void testScheduleJob1() {
        youtubeBatchApplication.scheduleJob1();
    }

    @Test
    @DisplayName("유튜브 playlists 수동 테스트")
    void testScheduleJob2() {
        youtubeBatchApplication.scheduleJob2();
    }

    @Test
    @DisplayName("토큰 get 테스트")
    void testGetToken() {
        List<String> token = tokenRepository.findPageTokenByQuery("test");
        log.info("getPageToken: " + token.get(0));

    }

//    @Test
//    @DisplayName("playlist get 테스트")
//    void testGetPlaylist() {
//        List<String> playlistId = playlistRepository.findPlaylistIdBy();
//
//        for(String result : playlistId) {
//            fetchPlaylistItemsFromApi(result);
//        }
//    }

    public static void fetchPlaylistItemsFromApi(String playlistId) {
        fetchVideosFromApi(playlistId, videos);
    }

    public static void fetchVideosFromApi(String playlistId, List<Video> videos) {
        // 더미 데이터 추가
        videos.add(Video.builder().videoId("vid1").videoPosition(1).build());
        videos.add(Video.builder().videoId("vid2").videoPosition(2).build());
        videos.add(Video.builder().videoId("vid3").videoPosition(3).build());

        log.info("playlistId: {}, videos: {}", playlistId, videos);

        // videoId 수만큼 fetchVideoDetail 호출
        videos.forEach(video -> fetchVideoDetail(video.getVideoId(), playlistId, video.getVideoPosition()));
    }


    public static void fetchVideoDetail(String videoId, String playlistId, Integer position) {
        log.info("videoId: {}, playlistId: {}, position: {}", videoId, playlistId, position);
    }

    @Test
    @DisplayName("video get 테스트")
    void testGetVideo() {
        Video video = Video.builder()
                .videoId("-Cp78AyzXic")
                .build();
        Optional<Video> playlistId = playlistVideoRepository.findById(video.getVideoId());

        log.info("getPlaylistId: " + playlistId);

    }
}

