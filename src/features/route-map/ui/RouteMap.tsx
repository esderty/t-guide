import type { RouteMapProps } from '@/features/route-map/model/types'
import { RouteMapPreview } from '@/features/route-map/ui/RouteMapPreview'
import { LeafletRouteMap } from '@/features/route-map/ui/LeafletRouteMap'
import { appMapConfig } from '@/shared/config/map'

export function RouteMap(props: RouteMapProps) {
  switch (appMapConfig.provider) {
    case 'preview':
      return <RouteMapPreview {...props} />
    case 'osm':
    default:
      return <LeafletRouteMap {...props} />
  }
}
