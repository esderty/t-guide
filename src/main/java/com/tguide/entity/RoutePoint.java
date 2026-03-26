package com.tguide.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "points")
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double latitude;
    private Double longitude;
    private String description;
    private String audioUrl;
    private String imageUrl;
    private Integer orderNumber;

    @ManyToOne
    @JoinColumn(name = "excursion_id")
    private Excursion excursion;

    public Point() {}

    public Point(String name, Double latitude, Double longitude, String description,
                 String audioUrl, String imageUrl, Integer orderNumber) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.audioUrl = audioUrl;
        this.imageUrl = imageUrl;
        this.orderNumber = orderNumber;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Integer getOrderNumber() { return orderNumber; }
    public void setOrderNumber(Integer orderNumber) { this.orderNumber = orderNumber; }
    public Excursion getExcursion() { return excursion; }
    public void setExcursion(Excursion excursion) { this.excursion = excursion; }
}