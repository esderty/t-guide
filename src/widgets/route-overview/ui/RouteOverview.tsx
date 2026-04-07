import type { GeoPoint, RouteStop } from '@/entities/excursion/model/types'
import { RouteStopList } from '@/entities/place/ui/RouteStopList'
import { RouteMap } from '@/features/route-map/ui/RouteMap'

interface RouteOverviewProps {
  stops: RouteStop[]
  selectedStopId: string
  routeColor: string
  onLocateUser?: () => void
  onSelectStop: (stopId: string) => void
  userPosition?: GeoPoint | null
}

export function RouteOverview({
  stops,
  selectedStopId,
  routeColor,
  onLocateUser,
  onSelectStop,
  userPosition,
}: RouteOverviewProps) {
  return (
    <section className="route-overview">
      <RouteMap
        onLocateUser={onLocateUser}
        onSelect={onSelectStop}
        routeColor={routeColor}
        selectedStopId={selectedStopId}
        stops={stops}
        userPosition={userPosition}
      />
      <RouteStopList
        onSelect={onSelectStop}
        selectedStopId={selectedStopId}
        stops={stops}
      />
    </section>
  )
}
