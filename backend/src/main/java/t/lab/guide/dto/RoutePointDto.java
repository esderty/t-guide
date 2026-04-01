package t.lab.guide.dto;

public record RoutePointDto(
        Long id,
        String name,
        String description,
        Double latitude,
        Double longitude,
        String audioUrl,
        String imageUrl
) {
}
