import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { Link } from 'react-router-dom'

import type {
  ExcursionTheme,
  NearbyPoint,
  PointCategory,
  RouteStop,
  SupportedLocale,
} from '@/entities/excursion/model/types'
import { useDiscoveryRoutes } from '@/entities/excursion/model/useDiscoveryRoutes'
import { formatMeters } from '@/features/route-map/lib/route-geometry'
import { useUserGeolocation } from '@/features/route-map/model/useUserGeolocation'
import type {
  DiscoveryCategoryOption,
  DiscoveryRadiusOption,
} from '@/features/route-map/ui/DiscoveryMap'
import { DiscoveryMap } from '@/features/route-map/ui/DiscoveryMap'
import { useAuth } from '@/app/providers/useAuth'
import { useUserRoutes } from '@/features/user-routes/model/useUserRoutes'
import { appRoutes } from '@/shared/config/routes'
import {
  detectSupportedLocale,
  getStoredDiscoveryContext,
  saveDiscoveryContext,
} from '@/shared/lib/discovery-context'
import {
  formatDuration,
  formatLocaleLabel,
  formatPointCategory,
  formatTheme,
} from '@/shared/lib/format'
import { buildGoogleMapsUrl } from '@/shared/lib/maps'
import { SmartPlaceImage } from '@/shared/ui/SmartPlaceImage'
import { ExcursionCatalog } from '@/widgets/excursion-catalog/ui/ExcursionCatalog'
import './HomePage.css'

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
  const { session } = useAuth()
  const {
    addPointToDraft,
    clearDraftRoute,
    draftStops,
    isPointInDraft,
    removeDraftStop,
    saveDraftRoute,
  } = useUserRoutes()
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
  const [savedDraftPreviewStops, setSavedDraftPreviewStops] = useState<RouteStop[]>([])
  const [draftRouteNotice, setDraftRouteNoticeValue] = useState<string | null>(null)
  const [draftRouteNoticeKey, setDraftRouteNoticeKey] = useState(0)
  const [draftRouteNoticeTone, setDraftRouteNoticeTone] = useState<'success' | 'warning'>(
    'success',
  )
  const nearbyListRef = useRef<HTMLDivElement | null>(null)
  const shouldScrollNearbyListRef = useRef(false)
  const isAuthenticated = Boolean(session?.isAuthenticated && session.profile)

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
    error: discoveryError,
    excursions,
    isLoading,
    nearbyPoints,
  } = useDiscoveryRoutes({
    activePointCategory,
    center: currentCenter,
    enabled: canLoadNearbyPlaces,
    locale: audioLocale,
    radiusMeters,
    search: searchQuery,
  })

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

  const filteredNearbyPoints = nearbyPoints

  const effectiveSelectedPointId =
    filteredNearbyPoints.find((point) => point.id === selectedPointId)?.id ??
    filteredNearbyPoints[0]?.id ??
    ''

  const selectedPoint =
    filteredNearbyPoints.find((point) => point.id === effectiveSelectedPointId) ??
    filteredNearbyPoints[0] ??
    null

  const effectiveRouteTargetId =
    routeTargetId && filteredNearbyPoints.some((point) => point.id === routeTargetId)
      ? routeTargetId
      : null

  const visibleNearbyPoints = useMemo(
    () => getVisibleNearbyPoints(filteredNearbyPoints),
    [filteredNearbyPoints],
  )

  useEffect(() => {
    if (!shouldScrollNearbyListRef.current) {
      return
    }

    const list = nearbyListRef.current

    if (!list || !effectiveSelectedPointId) {
      return
    }

    const selectedCard = list.querySelector<HTMLElement>(
      `[data-point-id="${effectiveSelectedPointId}"]`,
    )

    if (selectedCard) {
      scrollElementIntoHorizontalViewIfNeeded(list, selectedCard)
    }

    shouldScrollNearbyListRef.current = false
  }, [effectiveSelectedPointId])

  useEffect(() => {
    if (!draftRouteNotice) {
      return
    }

    const timeoutId = window.setTimeout(() => {
      setDraftRouteNoticeValue(null)
    }, 3200)

    return () => {
      window.clearTimeout(timeoutId)
    }
  }, [draftRouteNotice, draftRouteNoticeKey])

  const setDraftRouteNotice = useCallback((message: string | null) => {
    if (!message) {
      setDraftRouteNoticeValue(null)
      return
    }

    setDraftRouteNoticeTone(
      message.toLowerCase().includes('уже') ? 'warning' : 'success',
    )
    setDraftRouteNoticeKey((current) => current + 1)
    setDraftRouteNoticeValue(message)
  }, [])

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

  const selectedPointMapsUrl = selectedPoint
    ? buildGoogleMapsUrl(selectedPoint.coordinates, userPosition)
    : '#'
  const selectedPointInDraft = selectedPoint ? isPointInDraft(selectedPoint.id) : false
  const canAddSelectedPoint = Boolean(selectedPoint && !selectedPointInDraft && draftStops.length < 6)

  const cycleSelectedPoint = useCallback(
    (direction: 1 | -1) => {
      if (!filteredNearbyPoints.length) {
        return
      }

      const currentIndex = filteredNearbyPoints.findIndex(
        (point) => point.id === effectiveSelectedPointId,
      )
      const safeIndex = currentIndex >= 0 ? currentIndex : 0
      const nextIndex =
        (safeIndex + direction + filteredNearbyPoints.length) % filteredNearbyPoints.length

      shouldScrollNearbyListRef.current = true
      setSelectedPointId(filteredNearbyPoints[nextIndex].id)
    },
    [effectiveSelectedPointId, filteredNearbyPoints],
  )

  const handleBuildRoute = useCallback(
    (pointId: string) => {
      setSelectedPointId(pointId)
      setRouteTargetId(pointId)

      if (!userPosition) {
        requestLocation()
      }
    },
    [requestLocation, userPosition],
  )

  const handleAddPointToRoute = useCallback(
    (point: NearbyPoint) => {
      addPointToDraft(point)
      setDraftRouteNotice(null)
      setSavedDraftPreviewStops([])
      setSelectedPointId(point.id)
      setRouteTargetId(point.id)

      if (!userPosition) {
        requestLocation()
      }
    },
    [addPointToDraft, requestLocation, setDraftRouteNotice, userPosition],
  )

  const handleClearDraftRoute = useCallback(() => {
    clearDraftRoute()
    setDraftRouteNotice(null)
    setSavedDraftPreviewStops([])
    setRouteTargetId(null)
  }, [clearDraftRoute, setDraftRouteNotice])

  const handleSaveDraftRoute = useCallback(() => {
    const result = saveDraftRoute()

    if (result.status === 'duplicate') {
      setDraftRouteNotice('Такой маршрут уже сохранен.')
      return
    }

    if (result.status !== 'saved' || !result.route) {
      return
    }

    setDraftRouteNotice('Маршрут сохранен в профиле.')
    setSavedDraftPreviewStops(result.route.stops)
    clearDraftRoute()
    setRouteTargetId(null)
  }, [clearDraftRoute, saveDraftRoute, setDraftRouteNotice])

  const handleNearbyCardClick = useCallback((pointId: string) => {
    shouldScrollNearbyListRef.current = true
    setSelectedPointId(pointId)
  }, [])

  const handleMapPointSelect = useCallback((pointId: string) => {
    shouldScrollNearbyListRef.current = true
    setSelectedPointId(pointId)
  }, [])

  return (
    <section className="home-page page-shell">
      <section className="home-page__hero section-surface">
        <div className="home-page__intro">
          <span className="eyebrow">Рядом с вами</span>
          <h1 className="page-title">Маршруты и интересные места рядом</h1>
          <p className="page-subtitle">
            Откройте ближайшие точки интереса, соберите маршрут под свой темп и переходите к
            готовым экскурсиям без лишних шагов.
          </p>

          <div className="home-page__meta">
            <span className="chip chip--accent">Язык: {formatLocaleLabel(audioLocale)}</span>
            <span className="chip">Радиус: {radiusMeters / 1000} км</span>
            <span className="chip">{nearbyPoints.length} точек рядом</span>
          </div>

          <div className="home-page__cta">
            <Link className="button button--primary" to={appRoutes.excursions}>
              Смотреть готовые маршруты
            </Link>
            <Link className="button button--secondary" to={appRoutes.profile}>
              Профиль
            </Link>
          </div>
        </div>

        <div className="home-page__discovery">
          <DiscoveryMap
            activeCategory={activePointCategory}
            canSaveDraftRoute={isAuthenticated}
            categoryOptions={nearbyCategoryOptions}
            draftStops={draftStops}
            draftRouteNotice={draftRouteNotice}
            draftRouteNoticeKey={draftRouteNoticeKey}
            draftRouteNoticeTone={draftRouteNoticeTone}
            embedded
            emptyMessage={searchQuery.trim() ? 'Ничего не найдено' : 'В этом радиусе нет доступных точек.'}
            fixedRouteStops={savedDraftPreviewStops}
            geolocationError={geolocationError}
            isLoading={isLoading || !canLoadNearbyPlaces}
            loadError={discoveryError}
            nearbyPoints={filteredNearbyPoints}
            onAddPointToDraft={handleAddPointToRoute}
            onBuildRoute={handleBuildRoute}
            onChangeRadius={setRadiusMeters}
            onClearDraftRoute={handleClearDraftRoute}
            onLocateUser={requestLocation}
            onSaveDraftRoute={handleSaveDraftRoute}
            onSearchQueryChange={setSearchQuery}
            onSelectCategory={setActivePointCategory}
            onSelectNextPoint={() => cycleSelectedPoint(1)}
            onSelectPoint={handleMapPointSelect}
            onSelectPreviousPoint={() => cycleSelectedPoint(-1)}
            radiusMeters={radiusMeters}
            radiusOptions={radiusOptions}
            routeTargetId={effectiveRouteTargetId}
            searchQuery={searchQuery}
            selectedPointId={effectiveSelectedPointId}
            userPosition={userPosition}
          />
        </div>
      </section>

      <section className="home-page__nearby section-surface">
        <div className="section-heading">
          <div>
            <h2 className="section-title">Активная точка и ближайшие места</h2>
          </div>
        </div>

        <div className="home-page__nearby-grid">
          {selectedPoint ? (
            <article className="home-page__spotlight">
              <div className="home-page__spotlight-media">
                <SmartPlaceImage
                  alt={selectedPoint.title}
                  category={selectedPoint.category}
                  coordinates={selectedPoint.coordinates}
                  loading="lazy"
                  referrerPolicy="no-referrer"
                  src={selectedPoint.imageUrl}
                  title={selectedPoint.title}
                />
              </div>

              <div className="home-page__spotlight-body">
                <div className="home-page__spotlight-head">
                  <div>
                    <span className="eyebrow">{formatPointCategory(selectedPoint.category)}</span>
                    <h3 className="home-page__spotlight-title">{selectedPoint.title}</h3>
                  </div>
                  <span className="chip chip--accent">{formatMeters(selectedPoint.distanceMeters)}</span>
                </div>

                <p className="home-page__spotlight-copy">{selectedPoint.description}</p>

                <div className="home-page__spotlight-meta">
                  <span className="chip">{selectedPoint.scheduleLabel}</span>
                  {selectedPoint.addressLabel ? <span className="chip">{selectedPoint.addressLabel}</span> : null}
                </div>

                <div className="home-page__spotlight-actions">
                  <a className="button button--secondary" href={selectedPointMapsUrl} rel="noreferrer" target="_blank">
                    Открыть в Google Maps
                  </a>
                  <button className="button button--primary" onClick={() => handleBuildRoute(selectedPoint.id)} type="button">
                    Построить маршрут
                  </button>
                  <button
                    className="button button--ghost"
                    disabled={!canAddSelectedPoint}
                    onClick={() => handleAddPointToRoute(selectedPoint)}
                    type="button"
                  >
                    {selectedPointInDraft ? 'Уже в маршруте' : 'Добавить в маршрут'}
                  </button>
                </div>
              </div>
            </article>
          ) : (
            <section className="status-card">
              <h3 className="status-card__title">Нет выбранной точки</h3>
              <p className="status-card__text">Разрешите геопозицию или выберите другую категорию.</p>
            </section>
          )}

          <div className="home-page__nearby-list" ref={nearbyListRef} role="list">
            {visibleNearbyPoints.map((point) => (
              <button
                className={[
                  'home-page__nearby-card',
                  point.id === effectiveSelectedPointId ? 'home-page__nearby-card--active' : '',
                  isPointInDraft(point.id) ? 'home-page__nearby-card--draft' : '',
                ].filter(Boolean).join(' ')}
                data-point-id={point.id}
                key={point.id}
                onClick={() => handleNearbyCardClick(point.id)}
                type="button"
              >
                <div className="home-page__nearby-card-media">
                  <SmartPlaceImage
                    alt={point.title}
                    category={point.category}
                    className="home-page__nearby-card-image"
                    coordinates={point.coordinates}
                    loading="lazy"
                    referrerPolicy="no-referrer"
                    src={point.imageUrl}
                    title={point.title}
                  />
                </div>

                <div className="home-page__nearby-card-body">
                  <div className="home-page__nearby-card-head">
                    <span className="eyebrow">{formatPointCategory(point.category)}</span>
                    <span className="home-page__nearby-card-distance">{formatMeters(point.distanceMeters)}</span>
                  </div>
                  <h3 className="home-page__nearby-card-title">{point.title}</h3>
                  <p className="home-page__nearby-card-copy">{point.shortDescription}</p>
                  {isPointInDraft(point.id) ? (
                    <span className="home-page__nearby-card-check">В маршруте ✓</span>
                  ) : null}
                </div>
              </button>
            ))}
          </div>
        </div>

        <div className="home-page__builder">
          <div className="home-page__builder-head">
            <div>
              <h3 className="home-page__builder-title">Мой маршрут</h3>
              <p className="home-page__builder-copy">
                {draftStops.length
                  ? `${draftStops.length} из 6 точек выбрано`
                  : 'Выберите минимум две точки рядом'}
              </p>
            </div>
            <span className="chip chip--accent">{draftStops.length}/6</span>
          </div>

          {draftStops.length ? (
            <div className="home-page__builder-stops">
              {draftStops.map((stop) => (
                <button
                  className="home-page__builder-stop"
                  key={stop.id}
                  onClick={() => removeDraftStop(stop.id)}
                  type="button"
                >
                  <span>{stop.order}. {stop.title}</span>
                  <span aria-hidden="true">×</span>
                </button>
              ))}
            </div>
          ) : null}

          <div className="home-page__builder-actions">
            {isAuthenticated ? (
              <button
                className="button button--primary"
                disabled={draftStops.length < 2}
                onClick={handleSaveDraftRoute}
                type="button"
              >
                Сохранить маршрут
              </button>
            ) : (
              <Link className="button button--primary" to={appRoutes.signIn}>
                Войти чтобы сохранить
              </Link>
            )}
            <button
              className="button button--secondary"
              disabled={!draftStops.length}
              onClick={handleClearDraftRoute}
              type="button"
            >
              Очистить
            </button>
          </div>

          {draftRouteNotice ? (
            <div
              className={`home-page__builder-notice home-page__builder-notice--${draftRouteNoticeTone}`}
              key={draftRouteNoticeKey}
              role="status"
            >
              {draftRouteNotice}
            </div>
          ) : null}
        </div>
      </section>

      <section className="home-page__routes section-surface">
        <div className="section-heading section-heading--stacked">
          <div>
            <h2 className="section-title">Готовые маршруты рядом</h2>
            <p className="section-copy">
              Выберите готовую прогулку по теме и времени и сразу перейдите к прохождению маршрута.
            </p>
          </div>

          <div className="home-page__filters">
            <div className="filter-bar">
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

            <div className="filter-bar">
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
          emptyDescription="Попробуйте снять фильтр по времени или переключить тип маршрута."
          emptyTitle="Маршруты по текущим условиям не найдены"
          excursions={visibleRoutes.slice(0, 6)}
        />

        <div className="home-page__routes-actions">
          <Link className="button button--secondary" to={appRoutes.excursions}>
            Все маршруты
          </Link>
        </div>
      </section>
    </section>
  )
}

function getVisibleNearbyPoints(points: NearbyPoint[]) {
  return points
}

function scrollElementIntoHorizontalViewIfNeeded(
  container: HTMLElement,
  target: HTMLElement,
) {
  const containerRect = container.getBoundingClientRect()
  const targetRect = target.getBoundingClientRect()
  const isFullyVisible =
    targetRect.left >= containerRect.left && targetRect.right <= containerRect.right

  if (isFullyVisible) {
    return
  }

  const currentScrollLeft = container.scrollLeft
  const relativeLeft = targetRect.left - containerRect.left + currentScrollLeft
  const nextScrollLeft =
    relativeLeft - container.clientWidth / 2 + targetRect.width / 2
  const maxScrollLeft = Math.max(0, container.scrollWidth - container.clientWidth)

  container.scrollTo({
    behavior: 'smooth',
    left: Math.min(Math.max(0, nextScrollLeft), maxScrollLeft),
  })
}
