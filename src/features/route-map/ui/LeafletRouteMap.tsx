import { useEffect, useMemo, useRef, useState } from 'react'
import * as L from 'leaflet'

import {
  createFallbackRouteGeometry,
  formatMeters,
  getBoundsFromGeometry,
  getNearestStop,
  toLngLat,
} from '@/features/route-map/lib/route-geometry'
import {
  applyLeafletLocation,
  buildMarkerTitle,
  createLeafletMap,
  createRoutePolyline,
  createRouteStopIcon,
  createUserIcon,
} from '@/features/route-map/lib/leaflet-map'
import type { RouteMapProps } from '@/features/route-map/model/types'
import { useUserGeolocation } from '@/features/route-map/model/useUserGeolocation'

const routePadding: [number, number, number, number] = [44, 44, 44, 44]
const pointZoom = 16
const userZoom = 15.4
const defaultCenter = { lat: 55.751244, lng: 37.618423 }

export function LeafletRouteMap({
  stops,
  selectedStopId,
  routeColor,
  onSelect,
}: RouteMapProps) {
  const mapContainerRef = useRef<HTMLDivElement | null>(null)
  const mapRef = useRef<L.Map | null>(null)
  const overlayRef = useRef<L.LayerGroup | null>(null)
  const initialCenterRef = useRef(stops[0]?.coordinates ?? defaultCenter)
  const skipAutoFocusRef = useRef(true)
  const [loadError, setLoadError] = useState<string | null>(null)

  const routeGeometry = useMemo(() => createFallbackRouteGeometry(stops), [stops])
  const routeBounds = useMemo(() => getBoundsFromGeometry(routeGeometry), [routeGeometry])

  const {
    error: geolocationError,
    requestLocation,
    userPosition,
  } = useUserGeolocation()

  const selectedStop =
    stops.find((stop) => stop.id === selectedStopId) ?? stops[0] ?? null
  const previousStop =
    selectedStop
      ? stops.find((stop) => stop.order === selectedStop.order - 1)
      : undefined
  const nextStop =
    selectedStop
      ? stops.find((stop) => stop.order === selectedStop.order + 1)
      : undefined
  const firstStop = stops[0]
  const lastStop = stops.at(-1)
  const nearestStop = useMemo(
    () => (userPosition ? getNearestStop(stops, userPosition) : null),
    [stops, userPosition],
  )

  useEffect(() => {
    const container = mapContainerRef.current

    if (!container || mapRef.current) {
      return
    }

    try {
      const map = createLeafletMap(container, initialCenterRef.current, 14)
      const overlay = L.layerGroup().addTo(map)

      mapRef.current = map
      overlayRef.current = overlay
      queueMicrotask(() => setLoadError(null))
    } catch (error) {
      console.error(error)
      queueMicrotask(() => setLoadError('Не удалось открыть карту.'))
    }

    return () => {
      overlayRef.current?.clearLayers()
      overlayRef.current = null
      mapRef.current?.remove()
      mapRef.current = null
    }
  }, [])

  useEffect(() => {
    const map = mapRef.current

    if (!map) {
      return
    }

    skipAutoFocusRef.current = true
    applyLeafletLocation(map, {
      bounds: routeBounds,
      duration: 900,
      easing: 'ease-in-out',
      padding: routePadding,
    })
  }, [routeBounds, stops])

  useEffect(() => {
    const overlay = overlayRef.current

    if (!overlay) {
      return
    }

    overlay.clearLayers()
    createRoutePolyline(routeGeometry, routeColor).addTo(overlay)

    stops.forEach((stop) => {
      const isActive = stop.id === selectedStopId
      const isStart = stop.id === firstStop?.id
      const isFinish = stop.id === lastStop?.id

      L.marker([stop.coordinates.lat, stop.coordinates.lng], {
        icon: createRouteStopIcon(stop, isActive, isStart, isFinish),
        title: buildMarkerTitle(stop),
      })
        .on('click', () => onSelect(stop.id))
        .addTo(overlay)
    })

    if (userPosition) {
      L.marker([userPosition.lat, userPosition.lng], {
        icon: createUserIcon(),
        title: 'Ваше местоположение',
      }).addTo(overlay)
    }
  }, [firstStop, lastStop, onSelect, routeColor, routeGeometry, selectedStopId, stops, userPosition])

  useEffect(() => {
    const map = mapRef.current

    if (!map || !selectedStop) {
      return
    }

    if (skipAutoFocusRef.current) {
      skipAutoFocusRef.current = false
      return
    }

    applyLeafletLocation(map, {
      center: toLngLat(selectedStop.coordinates),
      zoom: pointZoom,
      duration: 700,
      easing: 'ease-in-out',
    })
  }, [selectedStop])

  function showWholeRoute() {
    const map = mapRef.current

    if (!map) {
      return
    }

    applyLeafletLocation(map, {
      bounds: routeBounds,
      duration: 800,
      easing: 'ease-in-out',
      padding: routePadding,
    })
  }

  function goToStop(stopId?: string) {
    if (!stopId) {
      return
    }

    onSelect(stopId)
  }

  function showUserLocation() {
    const map = mapRef.current

    if (!userPosition || !map) {
      requestLocation()
      return
    }

    applyLeafletLocation(map, {
      center: toLngLat(userPosition),
      zoom: userZoom,
      duration: 700,
      easing: 'ease-in-out',
    })
  }

  const navigationControls = (
    <div className="map-card__control-group map-card__control-group--center">
      <button
        aria-label="Предыдущая точка"
        className="map-card__control-button map-card__control-button--nav"
        disabled={!previousStop}
        onClick={() => goToStop(previousStop?.id)}
        type="button"
      >
        <span className="map-card__control-icon" aria-hidden="true">
          ←
        </span>
        <span>Предыдущая</span>
      </button>

      <button
        aria-label="Следующая точка"
        className="map-card__control-button map-card__control-button--nav"
        disabled={!nextStop}
        onClick={() => goToStop(nextStop?.id)}
        type="button"
      >
        <span>Следующая</span>
        <span className="map-card__control-icon" aria-hidden="true">
          →
        </span>
      </button>
    </div>
  )

  if (loadError) {
    return (
      <section className="map-card">
        <div className="map-card__header">
          <h2 className="map-card__title">Карта маршрута</h2>
          <span className="map-status map-status--error">{loadError}</span>
        </div>

        <div className="map-card__controls map-card__controls--compact">
          {navigationControls}
        </div>
      </section>
    )
  }

  return (
    <section className="map-card">
      <div className="map-card__header">
        <h2 className="map-card__title">Карта маршрута</h2>
      </div>

      <div className="map-card__canvas map-card__canvas--interactive">
        <div className="map-card__map-root" ref={mapContainerRef}></div>
      </div>

      <div className="map-card__controls">
        <button
          className="map-card__control-button map-card__control-button--route"
          onClick={showWholeRoute}
          type="button"
        >
          Весь маршрут
        </button>

        {navigationControls}

        <button
          className="map-card__control-button map-card__control-button--locate"
          onClick={showUserLocation}
          type="button"
        >
          <span className="map-card__control-icon" aria-hidden="true">
            ◉
          </span>
          <span>Найти себя</span>
        </button>
      </div>

      <div className="map-card__legend">
        <span className="chip chip--accent">
          Активная точка: {selectedStop?.title ?? 'Маршрут'}
        </span>
        <span className="chip">
          {nextStop
            ? `Следующая: ${nextStop.order}. ${nextStop.title}`
            : 'Это последняя точка маршрута'}
        </span>
        <span className="chip">
          {nearestStop && userPosition
            ? `Ближе всего: ${nearestStop.stop.title} • ${formatMeters(nearestStop.distanceMeters)}`
            : 'Определяем ближайшую точку'}
        </span>
      </div>

      {geolocationError ? <p className="map-card__note">{geolocationError}</p> : null}
    </section>
  )
}
