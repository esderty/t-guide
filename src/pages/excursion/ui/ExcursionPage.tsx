import { useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'

import { useDiscoveryRoutes } from '@/entities/excursion/model/useDiscoveryRoutes'
import type { Excursion } from '@/entities/excursion/model/types'
import { AudioGuidePanel } from '@/features/audio-guide/ui/AudioGuidePanel'
import { PlaceDetailsCard } from '@/features/place-details/ui/PlaceDetailsCard'
import {
  formatMeters,
  getDistanceMetersBetween,
  getNearestStop,
} from '@/features/route-map/lib/route-geometry'
import { useUserGeolocation } from '@/features/route-map/model/useUserGeolocation'
import { appRoutes } from '@/shared/config/routes'
import { getStoredDiscoveryContext } from '@/shared/lib/discovery-context'
import {
  formatDifficulty,
  formatDistance,
  formatStopCount,
  formatTheme,
} from '@/shared/lib/format'
import { RouteOverview } from '@/widgets/route-overview/ui/RouteOverview'

export function ExcursionPage() {
  const { slug } = useParams<{ slug: string }>()
  const storedContext = useMemo(() => getStoredDiscoveryContext(), [])
  const [selectedStopId, setSelectedStopId] = useState<string>('')
  const { error: geolocationError, requestLocation, userPosition } = useUserGeolocation()
  const { excursions, isLoading } = useDiscoveryRoutes({
    activePointCategory: storedContext.activePointCategory,
    center: storedContext.center,
    locale: storedContext.locale,
    radiusMeters: storedContext.radiusMeters,
  })

  const excursion = useMemo<Excursion | null>(
    () => excursions.find((candidate) => candidate.slug === slug) ?? null,
    [excursions, slug],
  )

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

  if (isLoading) {
    return (
      <section className="status-card">
        <h1 className="status-card__title">Открываем маршрут</h1>
        <p className="status-card__text">Собираем точки прогулки и готовим карту.</p>
      </section>
    )
  }

  if (!excursion || !selectedStop) {
    return (
      <section className="not-found">
        <h1 className="not-found__title">Маршрут пока недоступен</h1>
        <p className="not-found__description">Попробуйте открыть другой маршрут из текущего списка.</p>
        <Link className="button button--secondary" to={appRoutes.excursions}>
          Вернуться к маршрутам
        </Link>
      </section>
    )
  }

  const distanceToStart = userPosition
    ? getDistanceMetersBetween(userPosition, excursion.stops[0].coordinates)
    : null

  return (
    <section className="page">
      <article className="route-hero route-hero--discovery">
        <Link className="inline-link" to={appRoutes.excursions}>
          К маршрутам
        </Link>

        <div className="route-hero__title-row">
          <div>
            <p className="eyebrow">{formatTheme(excursion.theme)}</p>
            <h1 className="route-title">{excursion.title}</h1>
            <p className="page-description route-hero__lead">{excursion.tagline}</p>
          </div>
          <span className="card__tag">{formatDifficulty(excursion.difficulty)}</span>
        </div>

        <div className="route-hero__meta route-hero__meta--compact">
          <div className="meta-pill meta-pill--compact">
            <span className="meta-pill__value">{formatDistance(excursion.distanceKm)}</span>
            <span className="meta-pill__label">Маршрут</span>
          </div>
          <div className="meta-pill meta-pill--compact">
            <span className="meta-pill__value">{formatStopCount(excursion.stops.length)}</span>
            <span className="meta-pill__label">Точек</span>
          </div>
          <div className="meta-pill meta-pill--compact">
            <span className="meta-pill__value">{excursion.audienceLabel}</span>
            <span className="meta-pill__label">Формат прогулки</span>
          </div>
        </div>

        <div className="route-hero__path">
          <span className="chip chip--accent">Старт: {excursion.startLabel}</span>
          <span className="chip">Финиш: {excursion.finishLabel}</span>
          {distanceToStart !== null ? (
            <span className="chip">До старта {formatMeters(distanceToStart)}</span>
          ) : null}
          {nearestStop ? (
            <span className="chip">
              Ближе всего: {nearestStop.stop.title} • {formatMeters(nearestStop.distanceMeters)}
            </span>
          ) : null}
        </div>
      </article>

      {geolocationError ? <p className="map-card__note">{geolocationError}</p> : null}

      <div className="route-layout">
        <div className="route-layout__column">
          <RouteOverview
            onLocateUser={requestLocation}
            onSelectStop={setSelectedStopId}
            routeColor={excursion.routeColor}
            selectedStopId={selectedStop.id}
            stops={excursion.stops}
            userPosition={userPosition}
          />
        </div>

        <div className="route-layout__column">
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
      </div>
    </section>
  )
}
