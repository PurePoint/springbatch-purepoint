package com.purepoint.youtubebatch.playlist;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.purepoint.youtubebatch.domain.Token;
import com.purepoint.youtubebatch.domain.Playlist;
import com.purepoint.youtubebatch.domain.Video;
import com.purepoint.youtubebatch.domain.VideoPlaylist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PlaylistItemReader implements ItemReader<VideoPlaylist> {

    @Autowired
    private TokenRepository tokenRepository;
    @Value("${youtube.api.key}")
    private String apiKey;
    @Autowired
    private WebClient webClient;
    private final List<Playlist> playlists = new ArrayList<>();
    private final List<Video> videos = new ArrayList<>();
    private int nextVideoIndex = 0;
    private String pageToken = null;


    @Override
    public VideoPlaylist read() throws InterruptedException {
        if (playlists.isEmpty()) {
//            fetchPlaylistFromApi("자바 강의", "");
//
//            sleep(1000); // API 호출 대기
//
//            fetchPlaylistFromApi("파이썬 강의", "");
//
//            sleep(1000);

            fetchPlaylistFromApi("클라우드 강의", "");

            sleep(1000);

//            fetchPlaylistFromApi("알고리즘 강의", "");
//
//            sleep(1000);
//
//            fetchPlaylistFromApi("네트워크 강의", "");
//
//            sleep(1000);

            int count = 0;
            // 재생목록 별로 영상을 가져옴
            for (Playlist playlist : playlists) {
                fetchPlaylistItemsFromApi(playlist);
                count++;
                if(count % 4 == 0) { sleep(1000); }
            }
        }

        VideoPlaylist nextItem = null;
        log.info("playlist.size(): {}, video.size(): {}", playlists.size(), videos.size());

        // playlist를 읽어오는 부분

        if (nextVideoIndex < playlists.size()) {
            Playlist currentPlaylist = playlists.get(nextVideoIndex);
            log.info("currentPlaylist: {}", currentPlaylist);
            Video firstVideo = getFirstVideoForPlaylist(currentPlaylist);
            log.info("firstVideo: {}", firstVideo);
//            nextItem = VideoPlaylist.builder()
//                    .video(firstVideo)
//                    .playlist(currentPlaylist)
//                    .build();
//            log.info("nextItem: {}", nextItem);
            nextVideoIndex++;

        // video를 읽어오는 부분
        } else if (nextVideoIndex - playlists.size() < videos.size()) {
            Video currentVideo = videos.get(nextVideoIndex - playlists.size());
            Playlist associatedPlaylist = getPlaylistForVideo(currentVideo);
            log.info("associatedPlaylist: {}", associatedPlaylist);
//            nextItem = VideoPlaylist.builder()
//                    .video(currentVideo)
//                    .playlist(associatedPlaylist)
//                    .build();
//            log.info("nextItem: {}", nextItem);
            nextVideoIndex++;
        }

        return nextItem;
    }

    private void fetchPlaylistFromApi(String query, String queryEx) {
        String part = "?part=snippet";
        String queryParam = "q=" + query + queryEx + "&type=playlist&order=relevance";
        fetchPlaylistsFromApi(part, queryParam, playlists);
    }

    private void fetchPlaylistItemsFromApi(Playlist playlist) {
        String part = "?part=snippet,contentDetails";
        String queryParam = "playlistId=" + playlist.getPlaylistId();
        fetchVideosFromApi(part, queryParam, videos);
    }

    private String getPageToken(String queryParam) {
        List<String> token = tokenRepository.findPageTokenByQuery(queryParam);
        if (token != null && !token.isEmpty()) {
            return token.get(0);
        }
        return null;
    }

    private void saveNextPageToken(String queryParam, String pageToken) {
        Token token = Token.builder()
                .query(queryParam)
                .pageToken(pageToken)
                .build();
        tokenRepository.save(token);
    }

    private void fetchPlaylistsFromApi(String part, String queryParam, List<Playlist> playlists) {
        pageToken = getPageToken(queryParam);

        String apiUrl = "https://www.googleapis.com/youtube/v3/" + "search" + part
                + (queryParam != null ? "&" + queryParam : "")
                + "&maxResults=10"
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
            JsonObject idObject = item.has("id") && item.get("id").isJsonObject() ? item.getAsJsonObject("id") : null;
            JsonObject snippet = item.has("snippet") && item.get("snippet").isJsonObject() ? item.getAsJsonObject("snippet") : null;


            Playlist playlist = Playlist.builder()
                    // playlistId 예외처리
                    .playlistId(idObject != null && idObject.has("playlistId") ? idObject.get("playlistId").getAsString() : null)
                    // kind 예외처리
                    .playlistKind(idObject != null && idObject.has("kind") ? idObject.get("kind").getAsString() : null)
                    // title 예외처리
                    .playlistTitle(snippet != null && snippet.has("title") ? snippet.get("title").getAsString() : null)
                    // description 예외처리
                    .playlistDescription(snippet != null && snippet.has("description") ? snippet.get("description").getAsString() : null)
                    // publishedAt 예외처리
                    .playlistPublishedAt(snippet != null && snippet.has("publishedAt") ?
                            ZonedDateTime.parse(snippet.get("publishedAt").getAsString()).toLocalDateTime() : null)
                    // thumbnails 예외처리
                    .playlistThumbnail((snippet != null && snippet.has("thumbnails")
                            && snippet.getAsJsonObject("thumbnails").has("high")
                            && snippet.getAsJsonObject("thumbnails").getAsJsonObject("high").has("url")) ?
                            snippet.getAsJsonObject("thumbnails").getAsJsonObject("high").get("url").getAsString() : null)
                    .build();

            log.info("playlistId: " + idObject.get("playlistId").getAsString());
            playlists.add(playlist);

        }

        pageToken = jsonResponse.has("nextPageToken") ? jsonResponse.get("nextPageToken").getAsString() : null;
        saveNextPageToken(queryParam, pageToken);
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

                Video video = fetchVideoDetails(videoId, playlistId, position);
                videos.add(video);

            }

            nextpageToken = jsonResponse.has("nextPageToken") ? jsonResponse.get("nextPageToken").getAsString() : null;
        } while (nextpageToken != null);

    }

    private Video fetchVideoDetails(String videoId, String playlistId, Integer position) {
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

    private Video getFirstVideoForPlaylist(Playlist playlist) {

        return videos.stream()
                .filter(video -> playlist.getPlaylistId().equals(video.getPlaylistId())) // Playlist ID 비교
                .findFirst()
                .orElse(null); // 해당 Playlist에 맞는 Video가 없다면 null 반환
    }

    private Playlist getPlaylistForVideo(Video video) {

        return playlists.stream()
                .filter(playlist -> playlist.getPlaylistId().equals(video.getPlaylistId())) // Playlist ID 비교
                .findFirst()
                .orElse(null); // 해당 Video에 맞는 Playlist가 없다면 null 반환
    }

}
