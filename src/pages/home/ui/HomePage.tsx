import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'

import type {
  ExcursionTheme,
  NearbyPoint,
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
import { SmartPlaceImage } from '@/shared/ui/SmartPlaceImage'
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
const nearbyPreviewCount = 4
const categorySearchTerms: Record<PointCategory, string[]> = {
  museum: ['музей', 'музеи', 'галерея', 'museum', 'gallery'],
  entertainment: ['развлечения', 'развлечение', 'кино', 'театр', 'cinema', 'theatre'],
  landmark: ['история', 'памятник', 'мемориал', 'истор', 'monument', 'memorial'],
  food: ['еда', 'кафе', 'ресторан', 'пекарня', 'food', 'cafe', 'restaurant', 'bakery'],
  park: ['природа', 'парк', 'сад', 'garden', 'park', 'nature'],
}

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
  const [routeTargetId, setRouteTargetId] = useState<string | null>(null)
  const [searchQuery, setSearchQuery] = useState('')
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

  const filteredNearbyPoints = useMemo(() => {
    const normalizedQuery = normalizeSearch(searchQuery)

    if (!normalizedQuery) {
      return nearbyPoints
    }

    return nearbyPoints.filter((point) => matchesNearbyPoint(point, normalizedQuery))
  }, [nearbyPoints, searchQuery])


  const effectiveRouteTargetId =
    routeTargetId && filteredNearbyPoints.some((point) => point.id === routeTargetId)
      ? routeTargetId
      : null

  const effectiveSelectedPointId =
    filteredNearbyPoints.some((point) => point.id === selectedPointId)
      ? selectedPointId
      : filteredNearbyPoints[0]?.id ?? ''

  const selectedPoint =
    filteredNearbyPoints.find((point) => point.id === effectiveSelectedPointId) ??
    filteredNearbyPoints[0] ??
    null

  const selectedPointMapsUrl = selectedPoint
    ? buildGoogleMapsUrl(selectedPoint.coordinates, userPosition)
    : '#'

  const visibleNearbyPoints = useMemo(
    () => getVisibleNearbyPoints(filteredNearbyPoints, effectiveSelectedPointId, nearbyPreviewCount),
    [effectiveSelectedPointId, filteredNearbyPoints],
  )

  const cycleSelectedPoint = useCallback((direction: 1 | -1) => {
    if (!filteredNearbyPoints.length) {
      return
    }

    const currentIndex = filteredNearbyPoints.findIndex(
      (point) => point.id === effectiveSelectedPointId,
    )
    const safeIndex = currentIndex >= 0 ? currentIndex : 0
    const nextIndex =
      (safeIndex + direction + filteredNearbyPoints.length) % filteredNearbyPoints.length

    setSelectedPointId(filteredNearbyPoints[nextIndex].id)
  }, [effectiveSelectedPointId, filteredNearbyPoints])

  const handleBuildRoute = useCallback((pointId: string) => {
    setSelectedPointId(pointId)
    setRouteTargetId(pointId)

    if (!userPosition) {
      requestLocation()
    }
  }, [requestLocation, userPosition])

  return (
    <section className="page page--home">
      <section className="page-section page-section--discovery-shell">
        <DiscoveryMap
          activeCategory={activePointCategory}
          categoryOptions={nearbyCategoryOptions}
          embedded
          emptyMessage={searchQuery.trim() ? 'Ничего не найдено' : 'В этом радиусе пока нет подходящих мест.'}
          geolocationError={geolocationError}
          isLoading={isLoading || !canLoadNearbyPlaces}
          loadError={nearbyPlacesError}
          nearbyPoints={filteredNearbyPoints}
          onBuildRoute={handleBuildRoute}
          onChangeRadius={setRadiusMeters}
          onLocateUser={requestLocation}
          onSearchQueryChange={setSearchQuery}
          onSelectCategory={setActivePointCategory}
          onSelectNextPoint={() => cycleSelectedPoint(1)}
          onSelectPoint={setSelectedPointId}
          onSelectPreviousPoint={() => cycleSelectedPoint(-1)}
          radiusMeters={radiusMeters}
          radiusOptions={radiusOptions}
          routeTargetId={effectiveRouteTargetId}
          searchQuery={searchQuery}
          selectedPointId={effectiveSelectedPointId}
          userPosition={userPosition}
        />

        <div className="discovery-home__places">
          <div className="discovery-home__places-track" role="list">
            {visibleNearbyPoints.map((point) => (
              <button
                className={`discovery-home__place-card${point.id === effectiveSelectedPointId ? ' discovery-home__place-card--active' : ''}`}
                key={point.id}
                onClick={() => setSelectedPointId(point.id)}
                type="button"
              >
                <div className="discovery-home__place-card-media">
                  <SmartPlaceImage
                    alt={point.title}
                    category={point.category}
                    className="discovery-home__place-card-image"
                    coordinates={point.coordinates}
                    loading="lazy"
                    referrerPolicy="no-referrer"
                    src={point.imageUrl}
                    title={point.title}
                    wikidataId={point.wikidataId}
                    wikipediaTitle={point.wikipediaTitle}
                  />
                  <div className="discovery-home__place-card-overlay">
                    <span className="eyebrow">{formatPointCategory(point.category)}</span>
                    <span className="chip">{formatMeters(point.distanceMeters)}</span>
                  </div>
                </div>

                <div className="discovery-home__place-card-body">
                  <h3 className="discovery-home__place-card-title">{point.title}</h3>
                  <p className="discovery-home__place-card-copy">{point.shortDescription}</p>
                  <div className="discovery-home__place-card-meta">
                    <span className="chip chip--soft">{point.scheduleLabel}</span>
                    {point.addressLabel ? <span className="chip chip--soft">{point.addressLabel}</span> : null}
                  </div>
                </div>
              </button>
            ))}
          </div>
        </div>

        {selectedPoint ? (
          <article className="discovery-home__spotlight">
            <div className="discovery-home__spotlight-media">
              <SmartPlaceImage
                alt={selectedPoint.title}
                category={selectedPoint.category}
                coordinates={selectedPoint.coordinates}
                loading="lazy"
                referrerPolicy="no-referrer"
                src={selectedPoint.imageUrl}
                title={selectedPoint.title}
                wikidataId={selectedPoint.wikidataId}
                wikipediaTitle={selectedPoint.wikipediaTitle}
              />
            </div>

            <div className="discovery-home__spotlight-body">
              <div className="discovery-home__spotlight-title-row">
                <div>
                  <p className="spotlight-card__eyebrow">{formatPointCategory(selectedPoint.category)}</p>
                  <h3 className="spotlight-card__title">{selectedPoint.title}</h3>
                </div>
                <span className="chip chip--accent">{formatMeters(selectedPoint.distanceMeters)}</span>
              </div>

              <p className="spotlight-card__description">{selectedPoint.description}</p>

              <div className="spotlight-card__meta">
                <span className="chip">{selectedPoint.scheduleLabel}</span>
                {selectedPoint.addressLabel ? <span className="chip">{selectedPoint.addressLabel}</span> : null}
              </div>

              <div className="spotlight-card__actions">
                <a className="button button--secondary" href={selectedPointMapsUrl} rel="noreferrer" target="_blank">
                  Открыть в Google Maps
                </a>
                <button className="button button--primary" onClick={() => handleBuildRoute(selectedPoint.id)} type="button">
                  Построить маршрут
                </button>
              </div>
            </div>
          </article>
        ) : null}
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

function getVisibleNearbyPoints(
  points: NearbyPoint[],
  selectedPointId: string,
  maxCount: number,
) {
  if (!points.length) {
    return []
  }

  const selectedIndex = points.findIndex((point) => point.id === selectedPointId)
  const startIndex = selectedIndex >= 0 ? selectedIndex : 0

  return Array.from({ length: Math.min(maxCount, points.length) }, (_, index) =>
    points[(startIndex + index) % points.length],
  )
}

function matchesNearbyPoint(point: NearbyPoint, normalizedQuery: string) {
  const haystack = [
    point.title,
    point.shortDescription,
    point.description,
    point.addressLabel,
    formatPointCategory(point.category),
    ...categorySearchTerms[point.category],
  ]
    .filter(Boolean)
    .join(' ')
    .toLocaleLowerCase()

  return haystack.includes(normalizedQuery)
}

function normalizeSearch(value: string) {
  return value.trim().toLocaleLowerCase()
}



