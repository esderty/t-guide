package t.lab.guide.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "excursions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Excursion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Integer duration;
    private Double distance;
    private String imageUrl;

    @OneToMany(mappedBy = "excursion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RoutePoint> routePoints = new ArrayList<>();

//    // допметод: добавляет точку и связывает её с этой экскурсией
//    public void addRoutePoint(RoutePoint point) {
//        routePoints.add(point);
//        point.setExcursion(this);
//    }
}