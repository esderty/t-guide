import { useEffect, useState } from 'react'

import { getExcursions } from '@/entities/excursion/api/excursions.repository'
import type { Excursion } from '@/entities/excursion/model/types'

export function useExcursions() {
  const [excursions, setExcursions] = useState<Excursion[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let isActive = true

    async function loadExcursions() {
      try {
        setLoading(true)
        setError(null)

        const nextExcursions = await getExcursions()

        if (!isActive) {
          return
        }

        setExcursions(nextExcursions)
      } catch {
        if (isActive) {
          setError('Не удалось загрузить маршруты.')
        }
      } finally {
        if (isActive) {
          setLoading(false)
        }
      }
    }

    void loadExcursions()

    return () => {
      isActive = false
    }
  }, [])

  return {
    excursions,
    loading,
    error,
  }
}
