import type { RouteMapProps } from '@/features/route-map/model/types'
import { RouteMapPreview } from '@/features/route-map/ui/RouteMapPreview'
import { YandexRouteMap } from '@/features/route-map/ui/YandexRouteMap'
import { appMapConfig } from '@/shared/config/map'

export function RouteMap(props: RouteMapProps) {
  switch (appMapConfig.provider) {
    case 'yandex':
      return appMapConfig.apiKey ? (
        <YandexRouteMap {...props} />
      ) : (
        <RouteMapPreview {...props} />
      )
    case 'preview':
    default:
      return <RouteMapPreview {...props} />
  }
}
