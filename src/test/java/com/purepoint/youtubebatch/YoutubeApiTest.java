package com.purepoint.youtubebatch;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class YoutubeApiTest {

    // 시크릿 키
    @Value("${youtube.api.key}")
    private String apiKey;

    private static final String APPLICATION_NAME = "YouTube Data API";
    private static final Long MAX_RESULTS = 10L;


    // YouTube API 클라이언트 서비스 생성
    private YouTube getService() throws GeneralSecurityException, IOException {
        return new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                request -> {})
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    @Test
    @DisplayName("YouTube API를 통해 동영상 조회")
    public void searchVideos() {
        List<String> videoTitles = new ArrayList<>();
        String query = "클라우드 강의";

        try {
            YouTube youtubeService = getService();
            YouTube.Search.List search = youtubeService.search().list("id,snippet");
            search.setKey(apiKey);
            search.setQ(query);
            search.setType("video");
            search.setMaxResults(MAX_RESULTS);

            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResults = searchResponse.getItems();

            for (SearchResult result : searchResults) {
                videoTitles.add("영상 " + result);
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }


        for(String result : videoTitles) {
            log.info(result);
        }
    }

    @Test
    @DisplayName("YouTube API를 통해 플레이리스트 조회")
    public void searchPlaylists() {
        Map<String, List<String>> playlistVideosMap = new HashMap<>();
        String query = "클라우드 강의";

        try {
            YouTube youtubeService = getService();

            // 1. 재생 목록 검색
            YouTube.Search.List search = youtubeService.search().list("id,snippet");
            search.setKey(apiKey);
            search.setQ(query);
            search.setType("playlist");
            search.setMaxResults(MAX_RESULTS);
            search.setRegionCode("KR");

            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResults = searchResponse.getItems();

            // 2. 각 재생 목록의 모든 영상 가져오기
            for (SearchResult playlist : searchResults) {
                String playlistId = playlist.getId().getPlaylistId();
                List<String> videoTitles = new ArrayList<>();

                // playlistId를 사용해 모든 영상 가져오기
                String nextPageToken = null;
                do {
                    YouTube.PlaylistItems.List playlistItemsRequest = youtubeService.playlistItems().list("snippet");
                    playlistItemsRequest.setKey(apiKey);
                    playlistItemsRequest.setPlaylistId(playlistId);
                    playlistItemsRequest.setMaxResults(50L);
                    playlistItemsRequest.setPageToken(nextPageToken);

                    PlaylistItemListResponse playlistItemsResponse = playlistItemsRequest.execute();
                    List<PlaylistItem> items = playlistItemsResponse.getItems();

                    for (PlaylistItem item : items) {
                        videoTitles.add("영상 제목: " + item.getSnippet().getTitle());

                        if(item.getSnippet().getThumbnails().getDefault() != null) {
                            videoTitles.add("영상 썸네일: " + item.getSnippet().getThumbnails().getDefault().getUrl());
                        }

                        videoTitles.add("플레이리스트 ID: " + item.getSnippet().getPlaylistId());
                        videoTitles.add("영상 ID: " + item.getSnippet().getResourceId().getVideoId());
                    }

                    nextPageToken = playlistItemsResponse.getNextPageToken();
                } while (nextPageToken != null);

                playlistVideosMap.put("플레이리스트 제목: " + playlist.getSnippet().getTitle(), videoTitles);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("결과: " + playlistVideosMap);
    }
}
