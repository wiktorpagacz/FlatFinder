package com.pagacz.flatflex.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Table
@Entity(name = "offer")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = "OFFERIDSEQUENCE", sequenceName = "OFFERIDSEQUENCE", allocationSize = 1)
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OFFERIDSEQUENCE")
    private Long id;
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
    @Column(name = "KAFKA_SEND")
    @Builder.Default
    private Character kafkaSend = 'N';

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
