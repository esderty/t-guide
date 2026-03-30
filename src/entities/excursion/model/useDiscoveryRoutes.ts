import { useMemo } from 'react'

import { createDiscoveryRoutes } from '@/entities/excursion/lib/discovery-routes'
import type {
  GeoPoint,
  PointCategory,
  SupportedLocale,
} from '@/entities/excursion/model/types'
import { useNearbyPlaces } from '@/entities/place/model/useNearbyPlaces'

interface UseDiscoveryRoutesParams {
  activePointCategory: PointCategory | 'all'
  center: GeoPoint
  enabled?: boolean
  locale: SupportedLocale
  radiusMeters: number
}

export function useDiscoveryRoutes({
  activePointCategory,
  center,
  enabled = true,
  locale,
  radiusMeters,
}: UseDiscoveryRoutesParams) {
  const nearbyResult = useNearbyPlaces({
    category: activePointCategory,
    center,
    enabled,
    locale,
    radiusMeters,
  })

  const excursions = useMemo(
    () =>
      createDiscoveryRoutes({
        activePointCategory,
        center,
        locale,
        nearbyPoints: nearbyResult.places,
        radiusMeters,
      }),
    [activePointCategory, center, locale, nearbyResult.places, radiusMeters],
  )

  return {
    ...nearbyResult,
    excursions,
    nearbyPoints: nearbyResult.places,
  }
}
