import type { RouteStop } from '@/entities/excursion/model/types'
import { RouteStopList } from '@/entities/place/ui/RouteStopList'
import { RouteMap } from '@/features/route-map/ui/RouteMap'

interface RouteOverviewProps {
  stops: RouteStop[]
  selectedStopId: string
  routeColor: string
  onSelectStop: (stopId: string) => void
}

export function RouteOverview({
  stops,
  selectedStopId,
  routeColor,
  onSelectStop,
}: RouteOverviewProps) {
  return (
    <section className="route-overview">
      <RouteMap
        onSelect={onSelectStop}
        routeColor={routeColor}
        selectedStopId={selectedStopId}
        stops={stops}
      />
      <RouteStopList
        onSelect={onSelectStop}
        selectedStopId={selectedStopId}
        stops={stops}
      />
    </section>
  )
}
