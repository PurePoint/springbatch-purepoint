package com.purepoint.youtubebatch;


import com.purepoint.youtubebatch.playlist.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
public class BatchTest {

    @Autowired
    private YoutubeBatchApplication youtubeBatchApplication;

    @Autowired
    private TokenRepository tokenRepository;

    @Test
    @DisplayName("스프링 배치 수동 테스트")
    void testScheduleJob() {
        youtubeBatchApplication.scheduleJob();
    }

    @Test
    @DisplayName("토큰 get 테스트")
    void testGetToken() {
        List<String> token = tokenRepository.findPageTokenByQuery("aaa");
        log.info("getPageToken" + token.get(0));

    }
}

