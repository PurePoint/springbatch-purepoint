package com.purepoint.youtubebatch.playlist;

import com.purepoint.youtubebatch.domain.Youtube;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Youtube, String> {
}
