package com.purepoint.youtubebatch.domain;


import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VideoPlaylist {
    private Video video;
    private Playlist playlist;
}
