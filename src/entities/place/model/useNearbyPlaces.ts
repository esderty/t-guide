import { useEffect, useState } from 'react'

import type {
  GeoPoint,
  NearbyPoint,
  PointCategory,
  SupportedLocale,
} from '@/entities/excursion/model/types'
import { fetchNearbyOsmPlaces } from '@/entities/place/api/osm-nearby'
import { buildFallbackNearbyPlaces } from '@/entities/place/lib/fallback-nearby'

interface UseNearbyPlacesParams {
  category: PointCategory | 'all'
  center: GeoPoint
  enabled?: boolean
  locale: SupportedLocale
  radiusMeters: number
}

interface UseNearbyPlacesResult {
  error: string | null
  isExtendedRadius: boolean
  isLoading: boolean
  places: NearbyPoint[]
}

export function useNearbyPlaces({
  category,
  center,
  enabled = true,
  locale,
  radiusMeters,
}: UseNearbyPlacesParams): UseNearbyPlacesResult {
  const [places, setPlaces] = useState<NearbyPoint[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!enabled) {
      setPlaces([])
      setIsLoading(false)
      setError(null)
      return undefined
    }

    let isCancelled = false

    async function loadPlaces() {
      setPlaces([])
      setIsLoading(true)
      setError(null)

      try {
        const fetchedPlaces = await fetchNearbyOsmPlaces({
          category,
          center,
          locale,
          radiusMeters,
        })

        if (isCancelled) {
          return
        }

        setPlaces(fetchedPlaces)
      } catch (loadError) {
        if (isCancelled) {
          return
        }

        console.error(loadError)
        const fallbackPlaces = buildFallbackNearbyPlaces(center, locale).filter((point) => {
          const matchesCategory = category === 'all' || point.category === category
          const matchesRadius = point.distanceMeters <= radiusMeters

          return matchesCategory && matchesRadius
        })

        setPlaces(fallbackPlaces)
        setError(null)
      } finally {
        if (!isCancelled) {
          setIsLoading(false)
        }
      }
    }

    loadPlaces()

    return () => {
      isCancelled = true
    }
  }, [category, center, enabled, locale, radiusMeters])

  return {
    error,
    isExtendedRadius: false,
    isLoading,
    places,
  }
}
