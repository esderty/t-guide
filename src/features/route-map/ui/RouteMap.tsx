import type { RouteMapProps } from '@/features/route-map/model/types'
import { LeafletRouteMap } from '@/features/route-map/ui/LeafletRouteMap'

export function RouteMap(props: RouteMapProps) {
  return <LeafletRouteMap {...props} />
}
