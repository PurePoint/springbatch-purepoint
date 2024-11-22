package com.purepoint.youtubebatch.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "token")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id", nullable = false)
    private Long tokenId;

    @Column(name = "query", nullable = false)
    private String query;

    @Column(name = "page_token")
    private String pageToken;
}
