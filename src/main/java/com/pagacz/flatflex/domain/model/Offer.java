package com.pagacz.flatflex.domain.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Table
@Entity(name = "offer")
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
    private Character writeToDocs = 'N';
    @Column(name = "SEND_BY_EMAIL")
    private Character sendByEmail = 'N';
    @Column(name = "WRITE_TO_DOCS_TIME")
    private LocalDateTime writeToDocsTime;
    @Column(name = "SEND_BY_EMAIL_TIME")
    private LocalDateTime sendByEmailTime;

    public Offer() {
    }

    public Offer(String link, String title, String source, Integer price, Integer originalPrice, Double space, String address) {
        this.link = link;
        this.title = title;
        this.source = source;
        this.price = price;
        this.originalPrice = originalPrice;
        this.space = space;
        this.address = address;
    }

    public String getSource() {
        return source;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getPrice() {
        return price;
    }

    public Double getSpace() {
        return space;
    }

    public String getAddress() {
        return address;
    }

    public void setInsertDate(LocalDateTime insertDate) {
        this.insertDate = insertDate;
    }

    public LocalDateTime getInsertDate() {
        return insertDate;
    }

    public void setLastModifiedTime(LocalDateTime lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public Character getWriteToDocs() {
        return writeToDocs;
    }

    public void setWriteToDocs(Character writeToDocs) {
        this.writeToDocs = writeToDocs;
    }

    public Character getSendByEmail() {
        return sendByEmail;
    }

    public void setSendByEmail(Character sendByEmail) {
        this.sendByEmail = sendByEmail;
    }

    public LocalDateTime getWriteToDocsTime() {
        return writeToDocsTime;
    }

    public void setWriteToDocsTime(LocalDateTime writeToDocsTime) {
        this.writeToDocsTime = writeToDocsTime;
    }

    public LocalDateTime getSendByEmailTime() {
        return sendByEmailTime;
    }

    public void setSendByEmailTime(LocalDateTime sendByEmailTime) {
        this.sendByEmailTime = sendByEmailTime;
    }

    public Integer getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Integer originalPrice) {
        this.originalPrice = originalPrice;
    }
}
