import { useEffect, useMemo, useRef, useState } from 'react'

import {
  buildWalkingRouteGeometry,
  createFallbackRouteGeometry,
  formatMeters,
  getBoundsFromGeometry,
  getBoundsFromStops,
  getNearestStop,
  hexToRgba,
  toLngLat,
  type MapLocationRequest,
  type YandexRouteGeometry,
} from '@/features/route-map/lib/route-geometry'
import {
  loadYandexMaps,
  resetYandexMapsLoader,
  type YandexMapsModules,
} from '@/features/route-map/lib/yandex-loader'
import type { RouteMapProps } from '@/features/route-map/model/types'
import { useUserGeolocation } from '@/features/route-map/model/useUserGeolocation'
import { RouteMapPreview } from '@/features/route-map/ui/RouteMapPreview'

const routePadding: [number, number, number, number] = [44, 44, 44, 44]
const pointZoom = 16
const userZoom = 15.4

export function YandexRouteMap({
  stops,
  selectedStopId,
  routeColor,
  onSelect,
}: RouteMapProps) {
  const [retryKey, setRetryKey] = useState(0)
  const [modules, setModules] = useState<YandexMapsModules | null>(null)
  const [loadError, setLoadError] = useState<string | null>(null)
  const [routeGeometry, setRouteGeometry] = useState<YandexRouteGeometry>(
    () => createFallbackRouteGeometry(stops),
  )
  const [routeMode, setRouteMode] = useState<
    'loading' | 'walking' | 'partial' | 'fallback'
  >('loading')
  const [location, setLocation] = useState<MapLocationRequest>(() => ({
    bounds: getBoundsFromStops(stops),
    padding: routePadding,
  }))
  const skipAutoFocusRef = useRef(true)

  const {
    error: geolocationError,
    requestLocation,
    status: geolocationStatus,
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
  const routeStroke = useMemo(
    () => [
      {
        color: hexToRgba(routeColor, 0.2),
        width: 12,
      },
      {
        color: routeColor,
        width: 6,
      },
      {
        color: 'rgba(255, 255, 255, 0.55)',
        width: 2,
      },
    ],
    [routeColor],
  )
  const routeKey = useMemo(
    () =>
      stops
        .map(
          (stop) =>
            `${stop.id}:${stop.coordinates.lat}:${stop.coordinates.lng}:${stop.order}`,
        )
        .join('|'),
    [stops],
  )
  const routeBounds = useMemo(
    () => getBoundsFromGeometry(routeGeometry),
    [routeGeometry],
  )
  const mapBehaviors = useMemo(
    () => ['drag', 'dblClick', 'pinchZoom', 'scrollZoom'],
    [],
  )

  useEffect(() => {
    let isMounted = true
    setLoadError(null)

    void loadYandexMaps()
      .then((nextModules) => {
        if (!isMounted) {
          return
        }

        setModules(nextModules)
      })
      .catch((error) => {
        if (!isMounted) {
          return
        }

        console.error(error)
        setModules(null)
        setLoadError('Не удалось загрузить Яндекс Карты.')
      })

    return () => {
      isMounted = false
    }
  }, [retryKey])

  useEffect(() => {
    let isMounted = true
    const fallbackGeometry = createFallbackRouteGeometry(stops)

    setRouteGeometry(fallbackGeometry)
    setRouteMode('loading')

    async function resolveRoute() {
      if (!modules) {
        return
      }

      const walkingRoute = await buildWalkingRouteGeometry(
        modules.ymaps3,
        stops,
      )

      if (!isMounted) {
        return
      }

      if (walkingRoute.geometry) {
        setRouteGeometry(walkingRoute.geometry)
        setRouteMode(walkingRoute.status)
        return
      }

      setRouteMode('fallback')
    }

    void resolveRoute()

    return () => {
      isMounted = false
    }
  }, [modules, routeKey, stops])

  useEffect(() => {
    skipAutoFocusRef.current = true
    setLocation({
      bounds: routeBounds,
      duration: 900,
      easing: 'ease-in-out',
      padding: routePadding,
    })
  }, [routeBounds, routeKey])

  useEffect(() => {
    if (!selectedStop) {
      return
    }

    if (skipAutoFocusRef.current) {
      skipAutoFocusRef.current = false
      return
    }

    setLocation({
      center: toLngLat(selectedStop.coordinates),
      zoom: pointZoom,
      duration: 700,
      easing: 'ease-in-out',
    })
  }, [selectedStop])

  function showWholeRoute() {
    setLocation({
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
    if (!userPosition) {
      requestLocation()
      return
    }

    setLocation({
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

        <div className="map-card__fallback-actions">
          <button
            className="button button--secondary"
            onClick={() => {
              resetYandexMapsLoader()
              setRetryKey((current) => current + 1)
            }}
            type="button"
          >
            Перезагрузить карту
          </button>
        </div>

        <RouteMapPreview
          embedded
          onSelect={onSelect}
          routeColor={routeColor}
          selectedStopId={selectedStopId}
          showLegend={false}
          stops={stops}
        />

        <div className="map-card__controls map-card__controls--compact">
          {navigationControls}
        </div>
      </section>
    )
  }

  if (!modules) {
    return (
      <section className="map-card">
        <div className="map-card__header">
          <div>
            <h2 className="map-card__title">Карта маршрута</h2>
            <p className="section-description">
              Подключаем Яндекс Карты и строим пешеходный маршрут по Самаре.
            </p>
          </div>
          <span className="map-status map-status--loading">Загрузка карты</span>
        </div>

        <div className="map-card__canvas map-card__canvas--loading">
          <div className="map-card__loader">
            <span className="map-card__loader-ring" aria-hidden="true"></span>
            <span>Готовим интерактивную карту маршрута</span>
          </div>
        </div>
      </section>
    )
  }

  const {
    YMap,
    YMapDefaultFeaturesLayer,
    YMapDefaultSchemeLayer,
    YMapFeature,
    YMapMarker,
  } = modules.components

  return (
    <section className="map-card">
      <div className="map-card__header">
        <div>
          <h2 className="map-card__title">Карта маршрута</h2>
          <p className="section-description">
            Переключайтесь между точками, открывайте весь маршрут и находите себя
            на карте во время прогулки.
          </p>
        </div>

        <div className="map-card__status-group">
          <span
            className={`map-status${routeMode === 'walking' ? ' map-status--accent' : ''}`}
          >
            {routeMode === 'loading'
              ? 'Строим пешеходный маршрут'
              : routeMode === 'walking'
                ? 'Пешеходный маршрут Яндекса'
                : routeMode === 'partial'
                  ? 'Маршрут частично по дорогам'
                : 'Маршрут по точкам'}
          </span>
          <span
            className={`map-status${userPosition ? ' map-status--success' : ''}`}
          >
            {userPosition
              ? 'Геопозиция активна'
              : geolocationStatus === 'blocked'
                ? 'Геопозиция отключена'
                : 'Определяем положение'}
          </span>
        </div>
      </div>

      <div className="map-card__canvas map-card__canvas--interactive">
        <YMap
          behaviors={modules.reactify.useDefault(mapBehaviors, [])}
          className="map-card__map-root"
          location={modules.reactify.useDefault(location, [location])}
          mode="vector"
        >
          <YMapDefaultSchemeLayer />
          <YMapDefaultFeaturesLayer />

          <YMapFeature
            geometry={modules.reactify.useDefault(routeGeometry, [routeGeometry])}
            style={modules.reactify.useDefault(
              {
                stroke: routeStroke,
              },
              [routeStroke],
            )}
          />

          {stops.map((stop) => {
            const isActive = stop.id === selectedStopId
            const isStart = stop.id === firstStop?.id
            const isFinish = stop.id === lastStop?.id

            return (
              <YMapMarker
                coordinates={modules.reactify.useDefault(
                  toLngLat(stop.coordinates),
                  [stop.coordinates.lat, stop.coordinates.lng],
                )}
                key={stop.id}
              >
                <button
                  aria-label={`Точка маршрута ${stop.order}: ${stop.title}`}
                  className={`map-marker${isActive ? ' map-marker--active' : ''}${isStart ? ' map-marker--start' : ''}${isFinish ? ' map-marker--finish' : ''}`}
                  onClick={() => onSelect(stop.id)}
                  title={stop.title}
                  type="button"
                >
                  {(isStart || isFinish) && (
                    <span className="map-marker__hint">
                      {isStart ? 'Старт' : 'Финиш'}
                    </span>
                  )}
                  <span className="map-marker__core">{stop.order}</span>
                </button>
              </YMapMarker>
            )
          })}

          {userPosition ? (
            <YMapMarker
              coordinates={modules.reactify.useDefault(toLngLat(userPosition), [
                userPosition.lat,
                userPosition.lng,
              ])}
            >
              <div className="user-marker" title="Ваше местоположение">
                <span className="user-marker__pulse" aria-hidden="true"></span>
                <span className="user-marker__dot"></span>
              </div>
            </YMapMarker>
          ) : null}
        </YMap>
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

      {geolocationError ? (
        <p className="map-card__note">{geolocationError}</p>
      ) : null}
    </section>
  )
}
