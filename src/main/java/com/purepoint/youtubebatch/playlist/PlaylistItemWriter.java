package com.purepoint.youtubebatch.playlist;

import com.purepoint.youtubebatch.domain.Playlist;
import com.purepoint.youtubebatch.domain.Video;
import com.purepoint.youtubebatch.domain.VideoPlaylist;
import com.purepoint.youtubebatch.video.VideoRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class PlaylistItemWriter implements ItemWriter<VideoPlaylist>  {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Transactional
    @Override
    public void write(Chunk<? extends VideoPlaylist> videoPlaylists) {
        List<Playlist> playlists = new ArrayList<>();
        List<Video> videos = new ArrayList<>();

        // Chunk의 모든 VideoPlaylist를 순회하여 Playlist와 Video를 수집
        for (VideoPlaylist videoPlaylist : videoPlaylists) {
            playlists.add(videoPlaylist.getPlaylist());
            videos.add(videoPlaylist.getVideo());
        }

        // Playlist와 Video를 한 번에 저장
        playlistRepository.saveAll(playlists);
        videoRepository.saveAll(videos);
    }
}
