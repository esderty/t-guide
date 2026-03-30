import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'

import type {
  ExcursionTheme,
  PointCategory,
  SupportedLocale,
} from '@/entities/excursion/model/types'
import { useDiscoveryRoutes } from '@/entities/excursion/model/useDiscoveryRoutes'
import { buildGoogleMapsUrl } from '@/entities/place/api/osm-nearby'
import { formatMeters } from '@/features/route-map/lib/route-geometry'
import type {
  DiscoveryCategoryOption,
  DiscoveryRadiusOption,
} from '@/features/route-map/ui/DiscoveryMap'
import { DiscoveryMap } from '@/features/route-map/ui/DiscoveryMap'
import { useUserGeolocation } from '@/features/route-map/model/useUserGeolocation'
import { appRoutes } from '@/shared/config/routes'
import {
  detectSupportedLocale,
  getStoredDiscoveryContext,
  saveDiscoveryContext,
} from '@/shared/lib/discovery-context'
import {
  formatDuration,
  formatPointCategory,
  formatTheme,
} from '@/shared/lib/format'
import { ExcursionCatalog } from '@/widgets/excursion-catalog/ui/ExcursionCatalog'

const nearbyCategoryOptions: DiscoveryCategoryOption[] = [
  { id: 'all', label: 'Все места' },
  { id: 'museum', label: 'Музеи' },
  { id: 'entertainment', label: 'Развлечения' },
  { id: 'landmark', label: 'История' },
  { id: 'food', label: 'Еда' },
  { id: 'park', label: 'Природа' },
]

const radiusOptions: DiscoveryRadiusOption[] = [
  { value: 1000, label: '1 км' },
  { value: 3000, label: '3 км' },
  { value: 5000, label: '5 км' },
]

const routeThemeOptions: Array<ExcursionTheme | 'all'> = [
  'all',
  'walk',
  'food',
  'nature',
  'fun',
  'mixed',
]
const durationOptions = [30, 45, 60, 90, 120]

export function HomePage() {
  const storedContext = useMemo(() => getStoredDiscoveryContext(), [])
  const detectedLocale = useMemo(() => {
    if (typeof window === 'undefined') {
      return storedContext.locale
    }

    return detectSupportedLocale(
      navigator.languages?.[0] ?? navigator.language ?? storedContext.browserLocale,
    )
  }, [storedContext.browserLocale, storedContext.locale])

  const [audioLocale] = useState<SupportedLocale>(storedContext.locale ?? detectedLocale)
  const [activePointCategory, setActivePointCategory] = useState<PointCategory | 'all'>(
    storedContext.activePointCategory ?? 'all',
  )
  const [radiusMeters, setRadiusMeters] = useState<number>(storedContext.radiusMeters ?? 1000)
  const [activeRouteTheme, setActiveRouteTheme] = useState<ExcursionTheme | 'all'>('all')
  const [maxRouteDuration, setMaxRouteDuration] = useState<number | null>(null)
  const [selectedPointId, setSelectedPointId] = useState<string>('')
  const {
    error: geolocationError,
    requestLocation,
    status: geolocationStatus,
    userPosition,
  } = useUserGeolocation()

  const currentCenter = userPosition ?? storedContext.center
  const canLoadNearbyPlaces =
    Boolean(userPosition) || geolocationStatus === 'blocked' || geolocationStatus === 'unsupported'

  const {
    error: nearbyPlacesError,
    excursions,
    isExtendedRadius,
    isLoading,
    nearbyPoints,
  } = useDiscoveryRoutes({
    activePointCategory,
    center: currentCenter,
    enabled: canLoadNearbyPlaces,
    locale: audioLocale,
    radiusMeters,
  })

  const visibleRoutes = useMemo(
    () =>
      excursions.filter((excursion) => {
        const matchesTheme = activeRouteTheme === 'all' || excursion.theme === activeRouteTheme
        const matchesDuration =
          maxRouteDuration === null || excursion.durationMinutes <= maxRouteDuration

        return matchesTheme && matchesDuration
      }),
    [activeRouteTheme, excursions, maxRouteDuration],
  )

  useEffect(() => {
    saveDiscoveryContext({
      activePointCategory,
      center: currentCenter,
      locale: audioLocale,
      browserLocale:
        typeof window === 'undefined'
          ? storedContext.browserLocale
          : navigator.languages?.[0] ?? navigator.language ?? storedContext.browserLocale,
      radiusMeters,
      updatedAt: new Date().toISOString(),
    })
  }, [
    activePointCategory,
    audioLocale,
    currentCenter,
    radiusMeters,
    storedContext.browserLocale,
  ])

  const effectiveSelectedPointId =
    nearbyPoints.some((point) => point.id === selectedPointId)
      ? selectedPointId
      : nearbyPoints[0]?.id ?? ''

  const selectedPoint =
    nearbyPoints.find((point) => point.id === effectiveSelectedPointId) ??
    nearbyPoints[0] ??
    null

  const selectedPointMapsUrl = selectedPoint
    ? buildGoogleMapsUrl(selectedPoint.coordinates, userPosition)
    : '#'

  function cycleSelectedPoint(direction: 1 | -1) {
    if (!nearbyPoints.length) {
      return
    }

    const currentIndex = nearbyPoints.findIndex((point) => point.id === effectiveSelectedPointId)
    const safeIndex = currentIndex >= 0 ? currentIndex : 0
    const nextIndex = (safeIndex + direction + nearbyPoints.length) % nearbyPoints.length

    setSelectedPointId(nearbyPoints[nextIndex].id)
  }

  return (
    <section className="page page--home">
      <DiscoveryMap
        activeCategory={activePointCategory}
        categoryOptions={nearbyCategoryOptions}
        geolocationError={geolocationError}
        isExtendedRadius={isExtendedRadius}
        isLoading={isLoading || !canLoadNearbyPlaces}
        loadError={nearbyPlacesError}
        nearbyPoints={nearbyPoints}
        onChangeRadius={setRadiusMeters}
        onLocateUser={requestLocation}
        onSelectCategory={setActivePointCategory}
        onSelectNextPoint={() => cycleSelectedPoint(1)}
        onSelectPoint={setSelectedPointId}
        onSelectPreviousPoint={() => cycleSelectedPoint(-1)}
        radiusMeters={radiusMeters}
        radiusOptions={radiusOptions}
        selectedPointId={effectiveSelectedPointId}
        userPosition={userPosition}
      />

      <section className="page-section">
        <div className="section-heading section-heading--stacked">
          <div>
            <h2 className="section-title">Что можно открыть прямо сейчас</h2>
          </div>
        </div>

        {selectedPoint ? (
          <article className="spotlight-card spotlight-card--discovery">
            <div>
              <p className="spotlight-card__eyebrow">{formatPointCategory(selectedPoint.category)}</p>
              <h3 className="spotlight-card__title">{selectedPoint.title}</h3>
              <p className="spotlight-card__description">{selectedPoint.description}</p>
            </div>

            <div className="spotlight-card__meta">
              <span className="chip chip--accent">
                {formatMeters(selectedPoint.distanceMeters)} от пользователя
              </span>
              <span className="chip">{selectedPoint.scheduleLabel}</span>
              {selectedPoint.addressLabel ? <span className="chip">{selectedPoint.addressLabel}</span> : null}
            </div>

            <div className="spotlight-card__actions">
              <a className="button button--secondary" href={selectedPointMapsUrl} rel="noreferrer" target="_blank">
                Открыть в Google Maps
              </a>
            </div>
          </article>
        ) : null}

        <div className="nearby-grid">
          {nearbyPoints.slice(0, 4).map((point) => (
            <button
              className={`nearby-card${point.id === effectiveSelectedPointId ? ' nearby-card--active' : ''}`}
              key={point.id}
              onClick={() => setSelectedPointId(point.id)}
              type="button"
            >
              <div className="nearby-card__title-row">
                <div>
                  <p className="eyebrow">{formatPointCategory(point.category)}</p>
                  <h3>{point.title}</h3>
                </div>
                <span className="chip">{formatMeters(point.distanceMeters)}</span>
              </div>
              <p>{point.shortDescription}</p>
              <div className="nearby-card__meta">
                <span className="chip">{point.scheduleLabel}</span>
                {point.addressLabel ? <span className="chip">{point.addressLabel}</span> : null}
              </div>
            </button>
          ))}
        </div>
      </section>

      <section className="page-section">
        <div className="section-heading section-heading--stacked">
          <div>
            <h2 className="section-title">Готовые маршруты вокруг пользователя</h2>
          </div>

          <div className="filter-stack">
            <div className="filter-row filter-row--wrap">
              {routeThemeOptions.map((theme) => (
                <button
                  className={`filter-pill${activeRouteTheme === theme ? ' filter-pill--active' : ''}`}
                  key={theme}
                  onClick={() => setActiveRouteTheme(theme)}
                  type="button"
                >
                  {theme === 'all' ? 'Все маршруты' : formatTheme(theme)}
                </button>
              ))}
            </div>

            <div className="filter-row filter-row--wrap">
              <button
                className={`filter-pill${maxRouteDuration === null ? ' filter-pill--active' : ''}`}
                onClick={() => setMaxRouteDuration(null)}
                type="button"
              >
                Любое время
              </button>
              {durationOptions.map((duration) => (
                <button
                  className={`filter-pill${maxRouteDuration === duration ? ' filter-pill--active' : ''}`}
                  key={duration}
                  onClick={() => setMaxRouteDuration(duration)}
                  type="button"
                >
                  До {formatDuration(duration)}
                </button>
              ))}
            </div>
          </div>
        </div>

        <ExcursionCatalog
          emptyDescription="Попробуйте сменить тему маршрута или ограничение по времени."
          emptyTitle="Подходящие маршруты пока не найдены"
          excursions={visibleRoutes.slice(0, 6)}
        />

        <div className="section-actions">
          <Link className="button button--secondary" to={appRoutes.excursions}>
            Все маршруты
          </Link>
        </div>
      </section>
    </section>
  )
}
