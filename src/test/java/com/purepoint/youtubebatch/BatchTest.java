package com.purepoint.youtubebatch;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BatchTest {

    @Autowired
    private YoutubeBatchApplication youtubeBatchApplication;

    @Test
    @DisplayName("스프링 배치 수동 테스트")
    void testScheduleJob() {
        youtubeBatchApplication.scheduleJob();
    }
}

