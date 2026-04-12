import { useEffect, useState } from 'react'

import { appApi } from '@/shared/api/client'
import type { ProfileOverviewDto } from '@/shared/api/contracts'

export function useProfileOverview(enabled: boolean) {
  const [overview, setOverview] = useState<ProfileOverviewDto | null>(null)
  const [isLoading, setIsLoading] = useState(enabled)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!enabled) {
      return
    }

    let isMounted = true

    async function loadProfileOverview() {
      setIsLoading(true)
      setError(null)

      try {
        const nextOverview = await appApi.getProfileOverview()

        if (isMounted) {
          setOverview(nextOverview)
        }
      } catch (nextError) {
        if (isMounted) {
          setError(nextError instanceof Error ? nextError.message : 'Не удалось открыть профиль.')
        }
      } finally {
        if (isMounted) {
          setIsLoading(false)
        }
      }
    }

    void loadProfileOverview()

    return () => {
      isMounted = false
    }
  }, [enabled])

  return enabled
    ? {
        error,
        isLoading,
        overview,
      }
    : {
        error: null,
        isLoading: false,
        overview: null,
      }
}
