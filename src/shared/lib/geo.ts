import type { RouteStop } from '@/entities/excursion/model/types'

interface ProjectedStop extends RouteStop {
  x: number
  y: number
}

const canvasPadding = 12

export function projectRouteStops(stops: RouteStop[]): ProjectedStop[] {
  const latitudes = stops.map((stop) => stop.coordinates.lat)
  const longitudes = stops.map((stop) => stop.coordinates.lng)

  const minLat = Math.min(...latitudes)
  const maxLat = Math.max(...latitudes)
  const minLng = Math.min(...longitudes)
  const maxLng = Math.max(...longitudes)

  return stops.map((stop) => {
    const lngProgress =
      maxLng === minLng
        ? 0.5
        : (stop.coordinates.lng - minLng) / (maxLng - minLng)
    const latProgress =
      maxLat === minLat
        ? 0.5
        : (stop.coordinates.lat - minLat) / (maxLat - minLat)

    return {
      ...stop,
      x: canvasPadding + lngProgress * (100 - canvasPadding * 2),
      y: 100 - (canvasPadding + latProgress * (100 - canvasPadding * 2)),
    }
  })
}
