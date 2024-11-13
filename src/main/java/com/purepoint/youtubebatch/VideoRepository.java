package com.purepoint.youtubebatch;

import com.purepoint.youtubebatch.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, String> {
}
