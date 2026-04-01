package t.lab.guide.mapper;

import t.lab.guide.dto.RoutePointDto;
import t.lab.guide.entity.RoutePoint;

public class RoutePointMapper {
    public static RoutePointDto toRoutePointDto(RoutePoint routePoint) {
        return new RoutePointDto(
                routePoint.getId(),
                routePoint.getName(),
                routePoint.getDescription(),
                routePoint.getLatitude(),
                routePoint.getLongitude(),
                routePoint.getAudioUrl(),
                routePoint.getImageUrl()
        );
    }
}
