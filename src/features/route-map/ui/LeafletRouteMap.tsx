import { useEffect, useMemo, useRef, useState } from 'react'
import * as L from 'leaflet'

import {
  buildOsmWalkingRouteGeometry,
  buildOsmWalkingRouteGeometryFromPoints,
  createFallbackRouteGeometry,
  createLineGeometryFromPoints,
  formatMeters,
  getBoundsFromGeometry,
  getBoundsFromPoints,
  getDistanceMetersBetween,
  getNearestStop,
  toLngLat,
  type YandexRouteGeometry,
} from '@/features/route-map/lib/route-geometry'
import {
  applyLeafletLocation,
  buildMarkerTitle,
  createGuidePolyline,
  createLeafletMap,
  createRoutePolyline,
  createRouteStopIcon,
  createUserIcon,
} from '@/features/route-map/lib/leaflet-map'
import type { RouteMapProps } from '@/features/route-map/model/types'

const routePadding: [number, number, number, number] = [44, 44, 44, 44]
const pointZoom = 16
const userZoom = 15.4
const defaultCenter = { lat: 55.751244, lng: 37.618423 }

export function LeafletRouteMap({
  onLocateUser,
  onSelect,
  routeColor,
  selectedStopId,
  stops,
  userPosition,
}: RouteMapProps) {
  const mapContainerRef = useRef<HTMLDivElement | null>(null)
  const mapRef = useRef<L.Map | null>(null)
  const overlayRef = useRef<L.LayerGroup | null>(null)
  const initialCenterRef = useRef(stops[0]?.coordinates ?? defaultCenter)
  const skipSelectionFocusRef = useRef(true)
  const [loadError, setLoadError] = useState<string | null>(null)
  const stopsSignature = useMemo(() => stops.map((stop) => stop.id).join('|'), [stops])
  const fallbackRouteGeometry = useMemo(() => createFallbackRouteGeometry(stops), [stops])
  const [resolvedRoute, setResolvedRoute] = useState<{
    geometry: YandexRouteGeometry | null
    signature: string
  }>({
    geometry: null,
    signature: '',
  })
  const routeGeometry =
    resolvedRoute.signature === stopsSignature && resolvedRoute.geometry
      ? resolvedRoute.geometry
      : fallbackRouteGeometry
  const wholeRouteBounds = useMemo(
    () =>
      getBoundsFromPoints([
        ...stops.map((stop) => stop.coordinates),
        ...(userPosition ? [userPosition] : []),
      ]),
    [stops, userPosition],
  )

  const selectedStop =
    stops.find((stop) => stop.id === selectedStopId) ?? stops[0] ?? null
  const previousStop = selectedStop
    ? stops.find((stop) => stop.order === selectedStop.order - 1)
    : undefined
  const nextStop = selectedStop
    ? stops.find((stop) => stop.order === selectedStop.order + 1)
    : undefined
  const firstStop = stops[0]
  const lastStop = stops.at(-1)
  const nearestStop = useMemo(
    () => (userPosition ? getNearestStop(stops, userPosition) : null),
    [stops, userPosition],
  )
  const [guideRoute, setGuideRoute] = useState<{
    geometry: YandexRouteGeometry | null
    signature: string
  }>({
    geometry: null,
    signature: '',
  })
  const guideSignature =
    selectedStop && userPosition
      ? `${selectedStop.id}:${userPosition.lat.toFixed(5)}:${userPosition.lng.toFixed(5)}`
      : ''
  const fallbackGuideGeometry = useMemo(() => {
    if (!selectedStop || !userPosition) {
      return null
    }

    return createLineGeometryFromPoints([userPosition, selectedStop.coordinates])
  }, [selectedStop, userPosition])
  const guideGeometry =
    guideRoute.signature === guideSignature && guideRoute.geometry
      ? guideRoute.geometry
      : fallbackGuideGeometry
  const guideBounds = useMemo(
    () => (guideGeometry ? getBoundsFromGeometry(guideGeometry) : null),
    [guideGeometry],
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
    const controller = new AbortController()

    async function loadWalkingRoute() {
      try {
        const result = await buildOsmWalkingRouteGeometry(stops, controller.signal)

        if (controller.signal.aborted || !result.geometry) {
          return
        }

        setResolvedRoute({
          geometry: result.geometry,
          signature: stopsSignature,
        })
      } catch (error) {
        if (!controller.signal.aborted) {
          console.error(error)
        }
      }
    }

    void loadWalkingRoute()

    return () => {
      controller.abort()
    }
  }, [stops, stopsSignature])

  useEffect(() => {
    const controller = new AbortController()

    async function loadGuideRoute() {
      if (!selectedStop || !userPosition) {
        queueMicrotask(() => {
          setGuideRoute({ geometry: null, signature: '' })
        })
        return
      }

      try {
        const result = await buildOsmWalkingRouteGeometryFromPoints(
          [userPosition, selectedStop.coordinates],
          controller.signal,
        )

        if (controller.signal.aborted) {
          return
        }

        setGuideRoute({
          geometry: result.geometry,
          signature: guideSignature,
        })
      } catch (error) {
        if (!controller.signal.aborted) {
          console.error(error)
        }
      }
    }

    void loadGuideRoute()

    return () => {
      controller.abort()
    }
  }, [guideSignature, selectedStop, userPosition])

  useEffect(() => {
    const map = mapRef.current

    if (!map) {
      return
    }

    skipSelectionFocusRef.current = true

    if (guideBounds && userPosition) {
      applyLeafletLocation(map, {
        bounds: guideBounds,
        duration: 850,
        easing: 'ease-in-out',
        padding: routePadding,
      })
      return
    }

    applyLeafletLocation(map, {
      bounds: wholeRouteBounds,
      duration: 850,
      easing: 'ease-in-out',
      padding: routePadding,
    })
  }, [guideBounds, stopsSignature, userPosition, wholeRouteBounds])

  useEffect(() => {
    const overlay = overlayRef.current

    if (!overlay) {
      return
    }

    overlay.clearLayers()
    createRoutePolyline(routeGeometry, routeColor).addTo(overlay)

    if (guideGeometry) {
      createGuidePolyline(guideGeometry).addTo(overlay)
    }

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
  }, [firstStop, guideGeometry, lastStop, onSelect, routeColor, routeGeometry, selectedStopId, stops, userPosition])

  useEffect(() => {
    const map = mapRef.current

    if (!map || !selectedStop) {
      return
    }

    if (skipSelectionFocusRef.current) {
      skipSelectionFocusRef.current = false
      return
    }

    if (guideBounds && userPosition) {
      applyLeafletLocation(map, {
        bounds: guideBounds,
        duration: 700,
        easing: 'ease-in-out',
        padding: routePadding,
      })
      return
    }

    applyLeafletLocation(map, {
      center: toLngLat(selectedStop.coordinates),
      zoom: pointZoom,
      duration: 700,
      easing: 'ease-in-out',
    })
  }, [guideBounds, selectedStop, userPosition])

  function showWholeRoute() {
    const map = mapRef.current

    if (!map) {
      return
    }

    applyLeafletLocation(map, {
      bounds: wholeRouteBounds,
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
      onLocateUser?.()
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
        className="map-card__control-button map-card__control-button--arrow"
        disabled={!previousStop}
        onClick={() => goToStop(previousStop?.id)}
        type="button"
      >
        <span aria-hidden="true" className="map-card__control-icon map-card__control-icon--solo">
          ←
        </span>
      </button>

      <button
        aria-label="Следующая точка"
        className="map-card__control-button map-card__control-button--arrow"
        disabled={!nextStop}
        onClick={() => goToStop(nextStop?.id)}
        type="button"
      >
        <span aria-hidden="true" className="map-card__control-icon map-card__control-icon--solo">
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

        <div className="map-card__controls map-card__controls--compact">{navigationControls}</div>
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
          <span aria-hidden="true" className="map-card__control-icon">
            ◎
          </span>
          <span>Найти себя</span>
        </button>
      </div>

      <div className="map-card__legend">
        <span className="chip chip--accent">
          Активная точка: {selectedStop?.title ?? 'Маршрут'}
        </span>
        <span className="chip">
          {nextStop ? `Следующая: ${nextStop.order}. ${nextStop.title}` : 'Это последняя точка маршрута'}
        </span>
        {nearestStop && userPosition ? (
          <span className="chip">
            Ближе всего: {nearestStop.stop.title} • {formatMeters(nearestStop.distanceMeters)}
          </span>
        ) : null}
        {selectedStop && userPosition ? (
          <span className="chip">
            До текущей точки: {formatMeters(getDistanceMetersBetween(userPosition, selectedStop.coordinates))}
          </span>
        ) : null}
      </div>
    </section>
  )
}




