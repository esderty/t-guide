package t.lab.guide.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
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

    @OneToMany(mappedBy = "excursion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RoutePoint> routePoints = new ArrayList<>();

    public Excursion() {}


    // конструктор с парамтерами для создания объектов:
    public Excursion(String name, String description, Integer duration, Double distance) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.distance = distance;
    }

    // геттеры и сеттеры:
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

    public List<RoutePoint> getRoutePoints() { return routePoints; }
    public void setRoutePoints(List<RoutePoint> routePoints) { this.routePoints = routePoints; }


//    // допметод: добавляет точку и связывает её с этой экскурсией
//    public void addRoutePoint(RoutePoint point) {
//        routePoints.add(point);
//        point.setExcursion(this);
//    }
//}