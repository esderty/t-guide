import { useEffect, useState } from 'react'

import type {
  Excursion,
  GeoPoint,
  PointCategory,
  SupportedLocale,
} from '@/entities/excursion/model/types'
import { appApi } from '@/shared/api/client'

interface UseRouteBySlugParams {
  activePointCategory: PointCategory | 'all'
  center: GeoPoint
  enabled?: boolean
  locale: SupportedLocale
  radiusMeters: number
  slug: string
}

export function useRouteBySlug({
  activePointCategory,
  center,
  enabled = true,
  locale,
  radiusMeters,
  slug,
}: UseRouteBySlugParams) {
  const [route, setRoute] = useState<Excursion | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!enabled || !slug) {
      setRoute(null)
      setIsLoading(false)
      setError(null)
      return undefined
    }

    let isActive = true

    async function loadRoute() {
      setIsLoading(true)
      setError(null)

      try {
        const response = await appApi.getRouteBySlug({
          category: activePointCategory,
          center,
          locale,
          radiusMeters,
          slug,
        })

        if (!isActive) {
          return
        }

        setRoute(response)
      } catch (loadError) {
        if (!isActive) {
          return
        }

        console.error(loadError)
        setRoute(null)
        setError('Не удалось открыть маршрут.')
      } finally {
        if (isActive) {
          setIsLoading(false)
        }
      }
    }

    void loadRoute()

    return () => {
      isActive = false
    }
  }, [activePointCategory, center, enabled, locale, radiusMeters, slug])

  return {
    error,
    isLoading,
    route,
  }
}
