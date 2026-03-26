package t.lab.guide.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "route_points")
public class RoutePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;          // название достопримечательности
    private Double latitude;      // широта (для карты)
    private Double longitude;     // долгота (для карты)
    private String description;   // описание места
    private String audioUrl;      // ссылка на аудио
    private String imageUrl;      // ссылка на картинку
    private Integer orderNumber;  // порядок в маршруте (1,2,3 и т д)


    // свзяь с экскурсией:
    @ManyToOne
    @JoinColumn(name = "excursion_id")
    @JsonBackReference
    private Excursion excursion;

    public RoutePoint() {}

    public RoutePoint(String name, Double latitude, Double longitude, String description,
                      String audioUrl, String imageUrl, Integer orderNumber) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.audioUrl = audioUrl;
        this.imageUrl = imageUrl;
        this.orderNumber = orderNumber;
    }

    // Геттеры и сеттеры
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