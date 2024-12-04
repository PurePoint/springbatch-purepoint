package com.purepoint.youtubebatch.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "video")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Video {

    @Id
    @Column(name = "video_id", nullable = false)
    private String videoId;

    @Column(name = "video_kind", nullable = false)
    private String videoKind;

    @Column(name = "video_title", nullable = false)
    private String videoTitle;

    @Column(name = "video_description")
    private String videoDescription;

    @Column(name = "video_published_at")
    private LocalDateTime videoPublishedAt;

    @Column(name = "video_thumbnail")
    private String videoThumbnail;

    @Column(name = "video_category")
    private Integer videoCategory;

    @Column(name = "video_position")
    private Integer videoPosition;

    @Column(name = "playlist_id")
    private String playlistId;


//    // `Playlist`와의 연관 관계 매핑
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "playlist_id", insertable = false, updatable = false) // `playlistId` 필드 사용
//    private Playlist playlist;
}
