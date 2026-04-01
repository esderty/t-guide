package t.lab.guide.dto;

import java.util.List;

public record ExcursionFullDto(
        Long id,
        String name,
        String description,
        Integer duration,
        Double distance,
        String imageUrl,
        List<RoutePointDto>routePoints
) {
}
