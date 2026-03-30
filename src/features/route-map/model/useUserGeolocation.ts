import { useCallback, useEffect, useRef, useState } from 'react'

import type { GeoPoint } from '@/entities/excursion/model/types'

export type GeolocationStatus =
  | 'idle'
  | 'tracking'
  | 'blocked'
  | 'unsupported'
  | 'loading'

interface UseUserGeolocationResult {
  error: string | null
  requestLocation: () => void
  status: GeolocationStatus
  userPosition: GeoPoint | null
}

const geolocationOptions = {
  enableHighAccuracy: true,
  maximumAge: 15000,
  timeout: 12000,
}

export function useUserGeolocation(): UseUserGeolocationResult {
  const watchIdRef = useRef<number | null>(null)
  const [userPosition, setUserPosition] = useState<GeoPoint | null>(null)
  const [status, setStatus] = useState<GeolocationStatus>(() => {
    if (typeof window === 'undefined') {
      return 'idle'
    }

    return navigator.geolocation ? 'loading' : 'unsupported'
  })
  const [error, setError] = useState<string | null>(() => {
    if (typeof window === 'undefined' || navigator.geolocation) {
      return null
    }

    return 'Браузер не поддерживает геопозицию.'
  })

  const stopWatching = useCallback(() => {
    if (watchIdRef.current !== null && navigator.geolocation) {
      navigator.geolocation.clearWatch(watchIdRef.current)
      watchIdRef.current = null
    }
  }, [])

  const handlePosition = useCallback((position: GeolocationPosition) => {
    setUserPosition({
      lat: position.coords.latitude,
      lng: position.coords.longitude,
    })
    setStatus('tracking')
    setError(null)
  }, [])

  const handleGeolocationError = useCallback((geolocationError: GeolocationPositionError) => {
    setStatus('blocked')

    if (geolocationError.code === geolocationError.PERMISSION_DENIED) {
      setError('Доступ к геопозиции отключен. Можно продолжить с картой по умолчанию.')
      return
    }

    setError('Не удалось определить текущее местоположение.')
  }, [])

  const startWatching = useCallback(() => {
    if (typeof window === 'undefined' || !navigator.geolocation) {
      setStatus('unsupported')
      setError('Браузер не поддерживает геопозицию.')
      return
    }

    stopWatching()
    watchIdRef.current = navigator.geolocation.watchPosition(
      handlePosition,
      handleGeolocationError,
      geolocationOptions,
    )
  }, [handleGeolocationError, handlePosition, stopWatching])

  const requestLocation = useCallback(() => {
    setStatus('loading')
    setError(null)
    startWatching()
  }, [startWatching])

  useEffect(() => {
    if (typeof window === 'undefined' || !navigator.geolocation) {
      return undefined
    }

    stopWatching()
    watchIdRef.current = navigator.geolocation.watchPosition(
      handlePosition,
      handleGeolocationError,
      geolocationOptions,
    )

    return () => {
      stopWatching()
    }
  }, [handleGeolocationError, handlePosition, stopWatching])

  return {
    error,
    requestLocation,
    status,
    userPosition,
  }
}
