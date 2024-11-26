package com.purepoint.youtubebatch.video;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.purepoint.youtubebatch.domain.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;


@Component
@Slf4j
@RequiredArgsConstructor
public class VideoItemReader implements ItemReader<Video> {

    @Value("${youtube.api.key}")
    private String apiKey;
    private final WebClient webClient;
    private final List<Video> videos = new ArrayList<>();
    private int nextVideoIndex = 0;
    private String pageToken = null;

    @Override
    public Video read() throws InterruptedException {
        if (videos.isEmpty()) {
            fetchVideosFromApi("자바 강의", "-자바스크립트");

            sleep(100); // API 호출 대기

            fetchVideosFromApi("파이썬 강의", "");

            sleep(100);

            fetchVideosFromApi("클라우드 강의", "");

            sleep(100);

            fetchVideosFromApi("알고리즘 강의", "");

            sleep(100);

            fetchVideosFromApi("네트워크 강의", "");
        }
        Video nextVideo = null;
        if (nextVideoIndex < videos.size()) {
            nextVideo = videos.get(nextVideoIndex);
            nextVideoIndex++;
        }
        return nextVideo;
    }

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

            Video youtube = Video.builder()
                    // videoId 예외처리
                    .videoId(idObject != null && idObject.has("videoId") ? idObject.get("videoId").getAsString() : null)
                    // kind 예외처리
                    .videoKind(idObject != null && idObject.has("kind") ? idObject.get("kind").getAsString() : null)
                    // title 예외처리
                    .videoTitle(snippet != null && snippet.has("title") ? snippet.get("title").getAsString() : null)
                    // description 예외처리
                    .videoDescription(snippet != null && snippet.has("description") ? snippet.get("description").getAsString() : null)
                    // publishedAt 예외처리
                    .videoPublishedAt(snippet != null && snippet.has("publishedAt") ?
                            ZonedDateTime.parse(snippet.get("publishedAt").getAsString()).toLocalDateTime() : null)
                    // thumbnails 예외처리
                    .videoThumbnail((snippet != null && snippet.has("thumbnails")
                            && snippet.getAsJsonObject("thumbnails").has("high")
                            && snippet.getAsJsonObject("thumbnails").getAsJsonObject("high").has("url")) ?
                            snippet.getAsJsonObject("thumbnails").getAsJsonObject("high").get("url").getAsString() : null)
                    .build();


            videos.add(youtube);
        }

        pageToken = jsonResponse.has("nextPageToken") ? jsonResponse.get("nextPageToken").getAsString() : null;
        log.info("pageToken: " + pageToken);
        if(pageToken != null) { fetchVideosFromApi(query, queryEx); }
    }

}
