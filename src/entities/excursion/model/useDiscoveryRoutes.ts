import { useEffect, useState } from 'react'

import type {
  Excursion,
  GeoPoint,
  NearbyPoint,
  PointCategory,
  SupportedLocale,
} from '@/entities/excursion/model/types'
import { appApi } from '@/shared/api/client'

interface UseDiscoveryRoutesParams {
  activePointCategory: PointCategory | 'all'
  center: GeoPoint
  enabled?: boolean
  locale: SupportedLocale
  radiusMeters: number
  search?: string
}

export function useDiscoveryRoutes({
  activePointCategory,
  center,
  enabled = true,
  locale,
  radiusMeters,
  search,
}: UseDiscoveryRoutesParams) {
  const [nearbyPoints, setNearbyPoints] = useState<NearbyPoint[]>([])
  const [excursions, setExcursions] = useState<Excursion[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!enabled) {
      setNearbyPoints([])
      setExcursions([])
      setIsLoading(false)
      setError(null)
      return undefined
    }

    let isActive = true

    async function loadDiscoveryFeed() {
      setIsLoading(true)
      setError(null)

      try {
        const response = await appApi.getDiscoveryFeed({
          category: activePointCategory,
          center,
          locale,
          radiusMeters,
          search,
        })

        if (!isActive) {
          return
        }

        setNearbyPoints(response.nearbyPoints)
        setExcursions(response.excursions)
      } catch (loadError) {
        if (!isActive) {
          return
        }

        console.error(loadError)
        setNearbyPoints([])
        setExcursions([])
        setError('Не удалось загрузить данные для экрана.')
      } finally {
        if (isActive) {
          setIsLoading(false)
        }
      }
    }

    void loadDiscoveryFeed()

    return () => {
      isActive = false
    }
  }, [activePointCategory, center, enabled, locale, radiusMeters, search])

  return {
    error,
    excursions,
    isLoading,
    nearbyPoints,
  }
}
