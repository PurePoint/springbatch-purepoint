package com.purepoint.youtubebatch.video;

import com.purepoint.youtubebatch.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, String> {
}
