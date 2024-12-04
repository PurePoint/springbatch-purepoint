package com.purepoint.youtubebatch.playlist_video;

import com.purepoint.youtubebatch.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistVideoRepository extends JpaRepository<Video, String> {
}
