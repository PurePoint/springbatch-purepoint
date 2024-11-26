package com.purepoint.youtubebatch.playlist;

import com.purepoint.youtubebatch.domain.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, String> {

    @Query(value = """
            SELECT playlist_id
            FROM (
                SELECT p.playlist_id, ROW_NUMBER() OVER () AS rownum
                FROM playlist p
            ) sub
            ORDER BY rownum DESC
            LIMIT 10
        """, nativeQuery = true)
    List<String> findPlaylistIdBy();
}
