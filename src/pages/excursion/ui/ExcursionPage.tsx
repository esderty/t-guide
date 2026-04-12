import { useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'

import { useRouteBySlug } from '@/entities/excursion/model/useRouteBySlug'
import type { Excursion } from '@/entities/excursion/model/types'
import { AudioGuidePanel } from '@/features/audio-guide/ui/AudioGuidePanel'
import { PlaceDetailsCard } from '@/features/place-details/ui/PlaceDetailsCard'
import {
  formatMeters,
  getDistanceMetersBetween,
  getNearestStop,
} from '@/features/route-map/lib/route-geometry'
import { useUserGeolocation } from '@/features/route-map/model/useUserGeolocation'
import { useUserRoutes } from '@/features/user-routes/model/useUserRoutes'
import { appRoutes } from '@/shared/config/routes'
import { getStoredDiscoveryContext } from '@/shared/lib/discovery-context'
import {
  formatDifficulty,
  formatDistance,
  formatDuration,
  formatStopCount,
  formatTheme,
} from '@/shared/lib/format'
import { RouteOverview } from '@/widgets/route-overview/ui/RouteOverview'
import './ExcursionPage.css'

export function ExcursionPage() {
  const {
    isRouteSaved,
    personalRoutes,
    savedRoutes,
    shareRoute,
    toggleSavedRoute,
  } = useUserRoutes()
  const { slug } = useParams<{ slug: string }>()
  const storedContext = useMemo(() => getStoredDiscoveryContext(), [])
  const [selectedStopId, setSelectedStopId] = useState<string>('')
  const { error: geolocationError, requestLocation, userPosition } = useUserGeolocation()

  const {
    error,
    isLoading,
    route,
  } = useRouteBySlug({
    activePointCategory: storedContext.activePointCategory,
    center: storedContext.center,
    enabled: Boolean(slug),
    locale: storedContext.locale,
    radiusMeters: storedContext.radiusMeters,
    slug: slug ?? '',
  })

  const locallyStoredRoute =
    [...personalRoutes, ...savedRoutes].find((storedRoute) => storedRoute.slug === slug) ?? null
  const excursion = (route as Excursion | null) ?? locallyStoredRoute

  const nearestStop = useMemo(
    () => (excursion && userPosition ? getNearestStop(excursion.stops, userPosition) : null),
    [excursion, userPosition],
  )

  const effectiveSelectedStopId = useMemo(() => {
    if (!excursion) {
      return ''
    }

    if (excursion.stops.some((stop) => stop.id === selectedStopId)) {
      return selectedStopId
    }

    return excursion.stops[0]?.id ?? ''
  }, [excursion, selectedStopId])

  const selectedStop =
    excursion?.stops.find((stop) => stop.id === effectiveSelectedStopId) ??
    excursion?.stops[0] ??
    null

  const nextStop =
    excursion && selectedStop
      ? excursion.stops.find((stop) => stop.order === selectedStop.order + 1)
      : undefined

  if (isLoading && !locallyStoredRoute) {
    return (
      <section className="status-card">
        <h1 className="status-card__title">Открываем маршрут</h1>
        <p className="status-card__text">Загружаем точки, карту и сценарий прогулки.</p>
      </section>
    )
  }

  if (error && !locallyStoredRoute) {
    return (
      <section className="status-card">
        <h1 className="status-card__title">Не удалось открыть маршрут</h1>
        <p className="status-card__text">{error}</p>
      </section>
    )
  }

  if (!excursion || !selectedStop) {
    return (
      <section className="not-found">
        <p className="eyebrow">Маршрут</p>
        <h1 className="not-found__title">Маршрут не найден</h1>
        <p className="not-found__description">Откройте другой маршрут из каталога.</p>
        <Link className="button button--secondary" to={appRoutes.excursions}>
          Вернуться к каталогу
        </Link>
      </section>
    )
  }

  const distanceToStart = userPosition
    ? getDistanceMetersBetween(userPosition, excursion.stops[0].coordinates)
    : null
  const isSaved = isRouteSaved(excursion.slug)

  return (
    <section className="excursion-page page-shell">
      <section className="excursion-page__hero section-surface">
        <Link className="inline-link" to={appRoutes.excursions}>
          К маршрутам
        </Link>

        <div className="excursion-page__hero-main">
          <div className="excursion-page__hero-copy">
            <span className="eyebrow">{formatTheme(excursion.theme)}</span>
            <h1 className="page-title">{excursion.title}</h1>
            <p className="page-subtitle">{excursion.tagline}</p>
          </div>
          <span className="excursion-page__difficulty">{formatDifficulty(excursion.difficulty)}</span>
        </div>

        <div className="excursion-page__hero-meta">
          <div className="excursion-page__hero-stat">
            <span className="excursion-page__hero-value">{formatDuration(excursion.durationMinutes)}</span>
            <span className="meta-label">Продолжительность</span>
          </div>
          <div className="excursion-page__hero-stat">
            <span className="excursion-page__hero-value">{formatDistance(excursion.distanceKm)}</span>
            <span className="meta-label">Длина маршрута</span>
          </div>
          <div className="excursion-page__hero-stat">
            <span className="excursion-page__hero-value">{formatStopCount(excursion.stops.length)}</span>
            <span className="meta-label">Точки маршрута</span>
          </div>
          <div className="excursion-page__hero-stat">
            <span className="excursion-page__hero-value">{excursion.audienceLabel}</span>
            <span className="meta-label">Формат прогулки</span>
          </div>
        </div>

        <div className="excursion-page__hero-chips">
          <span className="chip chip--accent">Старт: {excursion.startLabel}</span>
          <span className="chip">Финиш: {excursion.finishLabel}</span>
          {distanceToStart !== null ? <span className="chip">До старта {formatMeters(distanceToStart)}</span> : null}
          {nearestStop ? (
            <span className="chip">
              Ближе всего: {nearestStop.stop.title} - {formatMeters(nearestStop.distanceMeters)}
            </span>
          ) : null}
        </div>

        <div className="excursion-page__hero-actions">
          <button
            aria-pressed={isSaved}
            className={`button ${isSaved ? 'button--primary' : 'button--secondary'}`}
            onClick={() => toggleSavedRoute(excursion)}
            type="button"
          >
            {isSaved ? 'Сохранено' : 'Сохранить маршрут'}
          </button>
          <button
            className="button button--ghost"
            onClick={() => void shareRoute(excursion)}
            type="button"
          >
            Поделиться
          </button>
        </div>

        {geolocationError ? <p className="excursion-page__hero-note">{geolocationError}</p> : null}
      </section>

      <section className="excursion-page__layout">
        <div className="excursion-page__column">
          <RouteOverview
            onLocateUser={requestLocation}
            onSelectStop={setSelectedStopId}
            routeColor={excursion.routeColor}
            selectedStopId={selectedStop.id}
            stops={excursion.stops}
            userPosition={userPosition}
          />
        </div>

        <div className="excursion-page__column">
          <PlaceDetailsCard stop={selectedStop} />
          <AudioGuidePanel
            nextStop={nextStop}
            onNextStop={() => {
              if (nextStop) {
                setSelectedStopId(nextStop.id)
              }
            }}
            stop={selectedStop}
          />
        </div>
      </section>
    </section>
  )
}
