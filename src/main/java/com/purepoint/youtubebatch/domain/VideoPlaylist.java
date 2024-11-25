package com.purepoint.youtubebatch.domain;


import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoPlaylist {
    private Video video;
    private Playlist playlist;
}
