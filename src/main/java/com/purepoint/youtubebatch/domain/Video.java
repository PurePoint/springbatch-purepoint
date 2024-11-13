package com.purepoint.youtubebatch.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.scheduling.annotation.Scheduled;

@Entity
@Table(name = "video")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Video {

    @Id
    @Column(name = "video_id", nullable = false)
    private String videoId;

    @Column(name = "video_title", nullable = false)
    private String videoTitle;

    @Column(name = "video_description")
    private String videoDescription;

    @Column(name = "video_published_at")
    private String videoPublishedAt;

    @Column(name = "video_thumbnail")
    private String videoThumbnail;

}
