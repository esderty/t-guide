package t.lab.guide.mapper;

import t.lab.guide.dto.ExcursionFullDto;
import t.lab.guide.dto.ExcursionShortDto;

public class ExcursionMapper {
    public static ExcursionShortDto toExcursionShortDto(t.lab.guide.entity.Excursion excursion) {
        return new ExcursionShortDto(
                excursion.getId(),
                excursion.getName(),
                excursion.getDuration(),
                excursion.getDistance(),
                excursion.getImageUrl()
        );
    }

    public static ExcursionFullDto toExcursionFullDto(t.lab.guide.entity.Excursion excursion) {
        return new ExcursionFullDto(
                excursion.getId(),
                excursion.getName(),
                excursion.getDescription(),
                excursion.getDuration(),
                excursion.getDistance(),
                excursion.getImageUrl(),
                excursion.getRoutePoints().stream()
                        .map(RoutePointMapper::toRoutePointDto)
                        .toList()
        );
    }
}
