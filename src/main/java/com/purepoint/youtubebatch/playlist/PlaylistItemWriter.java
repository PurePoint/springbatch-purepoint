package com.purepoint.youtubebatch.playlist;

import com.purepoint.youtubebatch.domain.Playlist;
import com.purepoint.youtubebatch.domain.VideoPlaylist;
import com.purepoint.youtubebatch.video.VideoRepository;
import lombok.NonNull;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlaylistItemWriter implements ItemWriter<VideoPlaylist>  {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Override
    public void write(@NonNull Chunk<? extends VideoPlaylist> videoPlaylists) {
        for (VideoPlaylist videoPlaylist : videoPlaylists) {
            Playlist playlist = videoPlaylist.getPlaylist();
            if (!playlistRepository.existsById(playlist.getPlaylistId())) {
                playlistRepository.save(playlist);
            }
            videoRepository.save(videoPlaylist.getVideo());
        }
    }
}
