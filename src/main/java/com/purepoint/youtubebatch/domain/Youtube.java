package com.purepoint.youtubebatch.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "youtube")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Youtube {

    @Id
    @Column(name = "item_id", nullable = false)
    private String itemId;

    @Column(name = "item_kind", nullable = false)
    private String itemKind;

    @Column(name = "item_title", nullable = false)
    private String itemTitle;

    @Column(name = "item_description")
    private String itemDescription;

    @Column(name = "item_published_at")
    private LocalDateTime itemPublishedAt;

    @Column(name = "item_thumbnail")
    private String itemThumbnail;

    @Column(name = "item_category")
    private Integer itemCategory;

}
