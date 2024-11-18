package com.purepoint.youtubebatch.video;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.purepoint.youtubebatch.domain.Youtube;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;


@Component
@Slf4j
@RequiredArgsConstructor
public class VideoItemReader implements ItemReader<Youtube> {

    @Value("${youtube.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder().baseUrl("https://www.googleapis.com").build();
    private final List<Youtube> videos = new ArrayList<>();
    private int nextVideoIndex = 0;
    private String pageToken = null;

    private void fetchVideosFromApi(String query, String queryEx) {
        String apiUrl = "/youtube/v3/search?part=snippet&videoDuration=medium&type=video&q=" + query + queryEx + "&maxResults=50"
                + (pageToken != null ? "&pageToken=" + pageToken : "")
                + "&key=" + apiKey;

        String response = webClient
                .method(HttpMethod.GET)
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (response == null) {
            log.error("Failed to fetch data from YouTube API: response is null");
            return;
        }

        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
        JsonArray items = jsonResponse.getAsJsonArray("items");

        for (int i = 0; i < items.size(); i++) {
            JsonObject item = items.get(i).getAsJsonObject();
            JsonObject idObject = item.getAsJsonObject("id");
            JsonObject snippet = item.getAsJsonObject("snippet");

            Youtube youtube = new Youtube();

            // videoId 예외처리
            if (idObject != null && idObject.has("videoId")) {
                youtube.setItemId(idObject.get("videoId").getAsString());
            }

            // kind 예외처리
            if (idObject != null && idObject.has("kind")) {
                youtube.setItemKind(idObject.get("kind").getAsString());
            }

            // title 예외처리
            if (snippet != null && snippet.has("title")) {
                youtube.setItemTitle(snippet.get("title").getAsString());
            }

            // description 예외처리
            if (snippet != null && snippet.has("description")) {
                youtube.setItemDescription(snippet.get("description").getAsString());
            }

            // publishedAt 예외처리
            if (snippet != null && snippet.has("publishedAt")) {
                String publishedAtStr = snippet.get("publishedAt").getAsString();

                // ZonedDateTime으로 변환
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(publishedAtStr);

                // LocalDateTime으로 변환 (UTC로 유지)
                LocalDateTime publishedAt = zonedDateTime.toLocalDateTime();
                youtube.setItemPublishedAt(publishedAt);
            }

            // thumbnails 예외처리
            if (snippet != null && snippet.has("thumbnails")) {
                JsonObject thumbnails = snippet.getAsJsonObject("thumbnails");
                if (thumbnails != null && thumbnails.has("high")) {
                    JsonObject defaultThumbnail = thumbnails.getAsJsonObject("high");
                    if (defaultThumbnail != null && defaultThumbnail.has("url")) {
                        youtube.setItemThumbnail(defaultThumbnail.get("url").getAsString());
                    }
                }
            }

            videos.add(youtube);
        }

        pageToken = jsonResponse.has("nextPageToken") ? jsonResponse.get("nextPageToken").getAsString() : null;
        log.info("pageToken: " + pageToken);
        if(pageToken != null) { fetchVideosFromApi(query, queryEx); }
    }

    @Override
    public Youtube read() throws InterruptedException {
        if (videos.isEmpty()) {
            fetchVideosFromApi("자바 강의", "-자바스크립트");

            sleep(100); // API 호출 대기

            fetchVideosFromApi("파이썬 강의", null);

            sleep(100);

            fetchVideosFromApi("클라우드 강의", null);

            sleep(100);

            fetchVideosFromApi("알고리즘 강의", null);

            sleep(100);

            fetchVideosFromApi("네트워크 강의", null);
        }
        Youtube nextVideo = null;
        if (nextVideoIndex < videos.size()) {
            nextVideo = videos.get(nextVideoIndex);
            nextVideoIndex++;
        }
        return nextVideo;
    }
}