package com.purepoint.youtubebatch.video;

import com.purepoint.youtubebatch.domain.Youtube;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Youtube, String> {
}
