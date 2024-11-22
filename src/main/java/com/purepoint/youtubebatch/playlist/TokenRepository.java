package com.purepoint.youtubebatch.playlist;

import com.purepoint.youtubebatch.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("SELECT t.pageToken FROM Token t WHERE t.query = :query ORDER BY t.tokenId DESC")
    List<String> findPageTokenByQuery(@Param("query") String query);
}
