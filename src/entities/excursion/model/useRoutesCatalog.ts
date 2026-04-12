import { useEffect, useState } from 'react'

import type {
  Excursion,
  GeoPoint,
  PointCategory,
  SupportedLocale,
} from '@/entities/excursion/model/types'
import { appApi } from '@/shared/api/client'

interface UseRoutesCatalogParams {
  activePointCategory: PointCategory | 'all'
  center: GeoPoint
  enabled?: boolean
  locale: SupportedLocale
  radiusMeters: number
}

export function useRoutesCatalog({
  activePointCategory,
  center,
  enabled = true,
  locale,
  radiusMeters,
}: UseRoutesCatalogParams) {
  const [routes, setRoutes] = useState<Excursion[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!enabled) {
      setRoutes([])
      setIsLoading(false)
      setError(null)
      return undefined
    }

    let isActive = true

    async function loadRoutes() {
      setIsLoading(true)
      setError(null)

      try {
        const response = await appApi.getRoutesCatalog({
          category: activePointCategory,
          center,
          locale,
          radiusMeters,
        })

        if (!isActive) {
          return
        }

        setRoutes(response)
      } catch (loadError) {
        if (!isActive) {
          return
        }

        console.error(loadError)
        setRoutes([])
        setError('Не удалось загрузить маршруты.')
      } finally {
        if (isActive) {
          setIsLoading(false)
        }
      }
    }

    void loadRoutes()

    return () => {
      isActive = false
    }
  }, [activePointCategory, center, enabled, locale, radiusMeters])

  return {
    error,
    isLoading,
    routes,
  }
}
