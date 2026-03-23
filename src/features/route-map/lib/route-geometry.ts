import type {
  GeoPoint,
  RouteStop,
} from '@/entities/excursion/model/types'

export type LngLat = [number, number]
export type LngLatBounds = [LngLat, LngLat]

export type YandexRouteGeometry =
  | {
      type: 'LineString'
      coordinates: LngLat[]
    }
  | {
      type: 'MultiLineString'
      coordinates: LngLat[][]
    }

export type MapLocationRequest =
  | {
      center: LngLat
      zoom: number
      duration?: number
      easing?: string
      padding?: [number, number, number, number]
    }
  | {
      bounds: LngLatBounds
      duration?: number
      easing?: string
      padding?: [number, number, number, number]
    }

interface NearestStopResult {
  stop: RouteStop
  distanceMeters: number
}

export interface WalkingRouteBuildResult {
  geometry: YandexRouteGeometry | null
  status: 'walking' | 'partial' | 'fallback'
}

const singlePointPadding = 0.006

export function toLngLat(point: GeoPoint): LngLat {
  return [point.lng, point.lat]
}

export function getBoundsFromStops(stops: RouteStop[]): LngLatBounds {
  return getBoundsFromLngLat(stops.map((stop) => toLngLat(stop.coordinates)))
}

export function getBoundsFromGeometry(
  geometry: YandexRouteGeometry,
): LngLatBounds {
  if (geometry.type === 'LineString') {
    return getBoundsFromLngLat(geometry.coordinates)
  }

  return getBoundsFromLngLat(geometry.coordinates.flat())
}

export function createFallbackRouteGeometry(
  stops: RouteStop[],
): YandexRouteGeometry {
  return {
    type: 'LineString',
    coordinates: stops.map((stop) => toLngLat(stop.coordinates)),
  }
}

export async function buildWalkingRouteGeometry(
  ymaps3: {
    route?: (options: {
      type: 'walking'
      points: LngLat[]
      bounds?: boolean
    }) => Promise<Array<{ toRoute?: () => { geometry?: unknown } }>>
  },
  stops: RouteStop[],
) : Promise<WalkingRouteBuildResult> {
  if (stops.length < 2 || !ymaps3.route) {
    return {
      geometry: null,
      status: 'fallback',
    }
  }

  try {
    const segmentGeometries = await Promise.all(
      stops.slice(0, -1).map(async (stop, index) => {
        const nextStop = stops[index + 1]

        try {
          const routes = await ymaps3.route?.({
            type: 'walking',
            points: [toLngLat(stop.coordinates), toLngLat(nextStop.coordinates)],
            bounds: true,
          })

          const geometry = routes?.[0]?.toRoute?.()?.geometry as
            | YandexRouteGeometry
            | undefined

          if (!isValidRouteGeometry(geometry)) {
            return {
              geometry: createSegmentFallbackGeometry(stop, nextStop),
              resolved: false,
            }
          }

          return {
            geometry,
            resolved: true,
          }
        } catch {
          return {
            geometry: createSegmentFallbackGeometry(stop, nextStop),
            resolved: false,
          }
        }
      }),
    )

    const resolvedSegments = segmentGeometries.filter((segment) => segment.resolved)

    if (!segmentGeometries.length) {
      return {
        geometry: null,
        status: 'fallback',
      }
    }

    const geometry = mergeSegmentGeometries(
      segmentGeometries.map((segment) => segment.geometry),
    )

    if (!geometry) {
      return {
        geometry: null,
        status: 'fallback',
      }
    }

    if (!resolvedSegments.length) {
      return {
        geometry,
        status: 'fallback',
      }
    }

    return {
      geometry,
      status:
        resolvedSegments.length === segmentGeometries.length
          ? 'walking'
          : 'partial',
    }
  } catch {
    return {
      geometry: null,
      status: 'fallback',
    }
  }
}

export function getNearestStop(
  stops: RouteStop[],
  userPosition: GeoPoint,
): NearestStopResult | null {
  let nearestStop: RouteStop | null = null
  let minDistance = Number.POSITIVE_INFINITY

  for (const stop of stops) {
    const distance = getDistanceMeters(stop.coordinates, userPosition)

    if (distance < minDistance) {
      minDistance = distance
      nearestStop = stop
    }
  }

  if (!nearestStop) {
    return null
  }

  return {
    stop: nearestStop,
    distanceMeters: minDistance,
  }
}

export function formatMeters(value: number): string {
  if (value < 1000) {
    return `${Math.round(value)} м`
  }

  return `${(value / 1000).toFixed(1).replace('.', ',')} км`
}

export function hexToRgba(hex: string, alpha: number): string {
  const normalized = hex.replace('#', '')
  const safeHex =
    normalized.length === 3
      ? normalized
          .split('')
          .map((character) => `${character}${character}`)
          .join('')
      : normalized

  const red = Number.parseInt(safeHex.slice(0, 2), 16)
  const green = Number.parseInt(safeHex.slice(2, 4), 16)
  const blue = Number.parseInt(safeHex.slice(4, 6), 16)

  return `rgba(${red}, ${green}, ${blue}, ${alpha})`
}

function getBoundsFromLngLat(points: LngLat[]): LngLatBounds {
  if (!points.length) {
    return [
      [50.090193, 53.185873],
      [50.110193, 53.205873],
    ]
  }

  if (points.length === 1) {
    const [lng, lat] = points[0]

    return [
      [lng - singlePointPadding, lat - singlePointPadding],
      [lng + singlePointPadding, lat + singlePointPadding],
    ]
  }

  const longitudes = points.map(([lng]) => lng)
  const latitudes = points.map(([, lat]) => lat)

  return [
    [Math.min(...longitudes), Math.min(...latitudes)],
    [Math.max(...longitudes), Math.max(...latitudes)],
  ]
}

function createSegmentFallbackGeometry(
  fromStop: RouteStop,
  toStop: RouteStop,
): YandexRouteGeometry {
  return {
    type: 'LineString',
    coordinates: [toLngLat(fromStop.coordinates), toLngLat(toStop.coordinates)],
  }
}

function isValidRouteGeometry(
  geometry: YandexRouteGeometry | undefined,
): geometry is YandexRouteGeometry {
  if (!geometry) {
    return false
  }

  if (geometry.type === 'LineString') {
    return geometry.coordinates.length > 1
  }

  return geometry.coordinates.some((segment) => segment.length > 1)
}

function mergeSegmentGeometries(
  geometries: YandexRouteGeometry[],
): YandexRouteGeometry | null {
  const segments = geometries.flatMap((geometry) =>
    geometry.type === 'LineString' ? [geometry.coordinates] : geometry.coordinates,
  )

  const validSegments = segments.filter((segment) => segment.length > 1)

  if (!validSegments.length) {
    return null
  }

  if (validSegments.length === 1) {
    return {
      type: 'LineString',
      coordinates: validSegments[0],
    }
  }

  return {
    type: 'MultiLineString',
    coordinates: validSegments,
  }
}

function getDistanceMeters(from: GeoPoint, to: GeoPoint): number {
  const earthRadius = 6371000
  const fromLat = degreesToRadians(from.lat)
  const toLat = degreesToRadians(to.lat)
  const deltaLat = degreesToRadians(to.lat - from.lat)
  const deltaLng = degreesToRadians(to.lng - from.lng)

  const haversine =
    Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
    Math.cos(fromLat) *
      Math.cos(toLat) *
      Math.sin(deltaLng / 2) *
      Math.sin(deltaLng / 2)

  return 2 * earthRadius * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine))
}

function degreesToRadians(value: number): number {
  return (value * Math.PI) / 180
}
