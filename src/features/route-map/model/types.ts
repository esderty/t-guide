import type { GeoPoint, RouteStop } from '@/entities/excursion/model/types'

export type MapProvider = 'preview' | 'osm'

export interface RouteMapProps {
  stops: RouteStop[]
  selectedStopId: string
  routeColor: string
  onSelect: (stopId: string) => void
  userPosition?: GeoPoint | null
}
