package com.pagacz.flatflex.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Table
@Entity(name = "offer")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "SOURCE")
    private String source;
    @Column(name = "LINK")
    private String link;
    @Column(name = "COMMENT")
    @Builder.Default
    private String comment = "";
    @Column(name = "PRICE")
    private Integer price;
    @Column(name = "ORIGINAL_PRICE")
    private Integer originalPrice;
    @Column(name = "SPACE")
    private Double space;
    @Column(name = "ADDRESS")
    private String address;
    @Column(name = "INSERT_DATE", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private LocalDateTime insertDate;
    @Column(name = "LAST_MODIFIED_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private LocalDateTime lastModifiedTime;
    @Column(name = "WRITE_TO_DOCS")
    @Builder.Default
    private Character writeToDocs = 'N';
    @Column(name = "SEND_BY_EMAIL")
    @Builder.Default
    private Character sendByEmail = 'N';
    @Column(name = "WRITE_TO_DOCS_TIME")
    private LocalDateTime writeToDocsTime;
    @Column(name = "SEND_BY_EMAIL_TIME")
    private LocalDateTime sendByEmailTime;

    public Offer(String link, String title, String source, Integer price, Integer originalPrice, Double space, String address) {
        this.link = link;
        this.title = title;
        this.source = source;
        this.price = price;
        this.originalPrice = originalPrice;
        this.space = space;
        this.address = address;
    }
}
