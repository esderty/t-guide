package t.lab.guide.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "route_points")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;          // название достопримечательности
    private String description;   // описание места
    private Double latitude;      // широта (для карты)
    private Double longitude;     // долгота (для карты)
    private String audioUrl;      // ссылка на аудио
    private String imageUrl;      // ссылка на картинку
    private Integer orderNumber;  // порядок в маршруте (1,2,3 и т д)

    // свзяь с экскурсией:
    @ManyToOne
    @JoinColumn(name = "excursion_id")
    @JsonBackReference
    private Excursion excursion;
}