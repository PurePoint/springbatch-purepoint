package com.purepoint.youtubebatch.playlist_video;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.purepoint.youtubebatch.domain.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PlaylistVideoItemReader implements ItemReader<Video> {

    @Value("${youtube.api.key}")
    private String apiKey;
    private final WebClient webClient;
    private final List<Video> videos = new ArrayList<>();
    private List<String> playlistIds;
    private int playlistIdIndex = 0;
    private int nextVideoIndex = 0;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        // JobExecutionContext에서 playlistId를 가져와 설정
        this.playlistIds = (List<String>) stepExecution.getJobExecution().getExecutionContext().get("playlistIds");
    }

    @Override
    public Video read() throws InterruptedException {
        if (playlistIds == null || playlistIds.isEmpty()) {
            log.error("Playlist IDs are not available.");
            return null;
        }

        // playlistId를 하나씩 처리
        if (playlistIdIndex < playlistIds.size()) {
            String playlistId = playlistIds.get(playlistIdIndex);
            playlistIdIndex++;

            // fetchPlaylistItemsFromApi 메서드 호출 시 playlistId를 매개변수로 전달
            fetchPlaylistItemsFromApi(playlistId);
        }

        // 비디오 리스트에서 하나씩 꺼내기
        Video nextVideo = null;
        while (nextVideoIndex < videos.size()) {
            nextVideo = videos.get(nextVideoIndex);
            nextVideoIndex++;

            // 비디오가 반환되면 종료
            if (nextVideo != null) {
                log.info("Returning video: {}", nextVideo);
                return nextVideo;
            }
        }

        // 비디오 리스트에 더 이상 비디오가 없으면 null 반환
        log.info("No more videos available.");
        return null;
    }



    private void fetchPlaylistItemsFromApi(String playlistId) {
        log.info("Fetching playlist items from YouTube API: " + playlistId);
        String part = "?part=snippet,contentDetails";
        String queryParam = "playlistId=" + playlistId;
        fetchVideosFromApi(part, queryParam, videos);
    }

    private void fetchVideosFromApi(String part, String queryParam, List<Video> videos) {
        String nextpageToken = null;
        String videoId = null;
        String playlistId = null;
        Integer position = 0;

        do {
            String apiUrl = "https://www.googleapis.com/youtube/v3/" + "playlistItems" + part
                    + (queryParam != null ? "&" + queryParam : "")
                    + "&maxResults=50"
                    + (nextpageToken != null ? "&pageToken=" + nextpageToken : "")
                    + "&key=" + apiKey;

            log.info("Generated API URL: " + apiUrl);

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

            log.info("items.size(): {}", items.size());

            for (int i = 0; i < items.size(); i++) {
                JsonObject item = items.get(i).getAsJsonObject();
                JsonObject snippet = item.has("snippet") && item.get("snippet").isJsonObject() ? item.getAsJsonObject("snippet") : null;
                JsonObject contentDetails = item.has("contentDetails") && item.get("contentDetails").isJsonObject()
                        ? item.getAsJsonObject("contentDetails")
                        : null;


                // videoId를 contentDetails에서 가져옴.
                if (contentDetails != null && contentDetails.has("videoId")) {
                    videoId = contentDetails.get("videoId").getAsString();
                    log.info("videoId: " + videoId);
                }

                if (snippet != null && snippet.has("playlistId")) {
                    playlistId = snippet.get("playlistId").getAsString();
                    log.info("playlistId: " + playlistId);
                }

                if(snippet != null && snippet.has("position")) {
                    position = snippet.get("position").getAsInt();
                    log.info("position: " + position);
                }

                log.info("videoId: {}, playlistId: {}, position: {}", videoId, playlistId, position);

                Video video = fetchVideoDetail(videoId, playlistId, position);
                videos.add(video);

            }

            nextpageToken = jsonResponse.has("nextPageToken") ? jsonResponse.get("nextPageToken").getAsString() : null;
        } while (nextpageToken != null);

    }

    private Video fetchVideoDetail(String videoId, String playlistId, Integer position) {
        Video video = null;

        String videoApiUrl = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + videoId + "&key=" + apiKey;

        String videoResponse = webClient
                .method(HttpMethod.GET)
                .uri(videoApiUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (videoResponse == null) {
            log.error("Failed to fetch video details from YouTube API: response is null");
            return null;
        }

        JsonObject videoJsonResponse = JsonParser.parseString(videoResponse).getAsJsonObject();
        JsonArray videoItems = videoJsonResponse.getAsJsonArray("items");

        for (int i = 0; i < videoItems.size(); i++) {
            JsonObject videoItem = videoItems.get(i).getAsJsonObject();
            JsonObject snippet = videoItem.has("snippet") ? videoItem.getAsJsonObject("snippet") : null;
            String description = videoItem.has("description") ? videoItem.get("description").getAsString() : null;
            String publishedAtStr = snippet.has("publishedAt") ? snippet.get("publishedAt").getAsString() : null;
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(publishedAtStr);
            LocalDateTime publishedAt = zonedDateTime.toLocalDateTime();
            JsonObject thumbnails = snippet.has("thumbnails") ? snippet.getAsJsonObject("thumbnails") : null;
            JsonObject highThumbnail = thumbnails != null && thumbnails.has("high") ? thumbnails.getAsJsonObject("high") : null;

            video = Video.builder()
                    // videoId
                    .videoId(videoId)
                    // playlistId
                    .playlistId(playlistId)
                    // position
                    .videoPosition(position)
                    // kind
                    .videoKind(videoItem.has("kind") ? videoItem.get("kind").getAsString() : null)
                    // snippet
                    .videoTitle(snippet.has("title") ? snippet.get("title").getAsString() : null)
                    // Description(길이를 255자로 제한)
                    .videoDescription(description != null && description.length() > 255 ? description.substring(0, 255) : description)
                    // PublishedAt
                    .videoPublishedAt(publishedAt)
                    // Thumbnails
                    .videoThumbnail(highThumbnail != null && highThumbnail.has("url") ? highThumbnail.get("url").getAsString() : null)
                    .build();

        }

        return video;
    }

}
