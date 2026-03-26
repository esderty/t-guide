package com.tguide.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "excursions")
public class Excursion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Integer duration;
    private Double distance;

    @OneToMany(mappedBy = "excursion", cascade = CascadeType.ALL)
    @OrderBy("orderNumber ASC")
    private List<Point> points;

    public Excursion() {}

    public Excursion(String name, String description, Integer duration, Double distance) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.distance = distance;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
    public List<Point> getPoints() { return points; }
    public void setPoints(List<Point> points) { this.points = points; }
}