package com.purepoint.youtubebatch.playlist;

import com.purepoint.youtubebatch.domain.Playlist;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlaylistItemWriter implements ItemWriter<Playlist> {

    private final PlaylistRepository playlistRepository;
    private StepExecution stepExecution;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public void write(@NonNull Chunk<? extends Playlist> playlists) {
        // Playlist 데이터를 저장
        playlistRepository.saveAll(playlists);

        // playlistId를 리스트로 저장
        List<String> playlistIds = new ArrayList<>();

        for (Playlist playlist : playlists) {
            String playlistId = playlist.getPlaylistId();
            playlistIds.add(playlistId);
            log.info("PlaylistId saved: {}", playlistId);
        }

        // ExecutionContext에 playlistId 리스트 저장
        stepExecution.getExecutionContext().put("playlistIds", playlistIds);
        stepExecution.getJobExecution().getExecutionContext().put("playlistIds", playlistIds);

    }
}
