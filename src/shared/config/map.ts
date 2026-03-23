import type { MapProvider } from '@/features/route-map/model/types'

export const appMapConfig = {
  provider:
    (import.meta.env.VITE_MAP_PROVIDER as MapProvider | undefined) ?? 'preview',
  apiKey: import.meta.env.VITE_YANDEX_MAPS_API_KEY ?? '',
  defaultCenter: {
    lat: 53.195873,
    lng: 50.100193,
  },
  defaultZoom: 13,
}
