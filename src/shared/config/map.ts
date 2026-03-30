import type { MapProvider } from '@/features/route-map/model/types'

export const appMapConfig = {
  provider: (import.meta.env.VITE_MAP_PROVIDER as MapProvider | undefined) ?? 'osm',
  defaultCenter: {
    lat: 55.751244,
    lng: 37.618423,
  },
  defaultZoom: 14,
  discoveryRadiusMeters: 1200,
} as const
