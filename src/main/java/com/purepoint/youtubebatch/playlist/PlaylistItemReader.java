package com.purepoint.youtubebatch.playlist;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.purepoint.youtubebatch.domain.Playlist;
import com.purepoint.youtubebatch.domain.Token;
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
public class PlaylistItemReader implements ItemReader<Playlist> {

    private final TokenRepository tokenRepository;
    @Value("${youtube.api.key}")
    private String apiKey;
    private final WebClient webClient;
    private final List<Playlist> playlists = new ArrayList<>();
    private int nextVideoIndex = 0;
    private String pageToken = null;


    @Override
    public Playlist read() throws InterruptedException {
        if (playlists.isEmpty()) {
            fetchPlaylistFromApi("자바 강의", "");

            sleep(1000); // API 호출 대기

            fetchPlaylistFromApi("파이썬 강의", "");

            sleep(1000);

            fetchPlaylistFromApi("클라우드 강의", "");

            sleep(1000);

            fetchPlaylistFromApi("알고리즘 강의", "");

            sleep(1000);

            fetchPlaylistFromApi("네트워크 강의", "");

            sleep(1000);

        }

        Playlist nextPlaylist = null;

        // playlist를 읽어오는 부분
        if (nextVideoIndex < playlists.size()) {
            nextPlaylist = playlists.get(nextVideoIndex);
            nextVideoIndex++;
        }
        return nextPlaylist;
    }

    private void fetchPlaylistFromApi(String query, String queryEx) {
        String part = "?part=snippet";
        String queryParam = "q=" + query + queryEx;
        fetchPlaylistsFromApi(part, queryParam, playlists);
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
                + "&type=playlist&order=relevance"
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

}
