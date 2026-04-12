import type { GeoPoint, RouteStop } from '@/entities/excursion/model/types'

export interface RouteMapProps {
  stops: RouteStop[]
  selectedStopId: string
  routeColor: string
  onLocateUser?: () => void
  onSelect: (stopId: string) => void
  userPosition?: GeoPoint | null
}
