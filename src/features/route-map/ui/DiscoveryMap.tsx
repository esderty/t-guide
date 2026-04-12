import { useEffect, useMemo, useRef, useState } from 'react'
import * as L from 'leaflet'
import { Link } from 'react-router-dom'

import type {
  GeoPoint,
  NearbyPoint,
  PointCategory,
  RouteStop,
} from '@/entities/excursion/model/types'
import {
  buildOsmWalkingRouteGeometryFromPoints,
  createLineGeometryFromPoints,
  getBoundsFromGeometry,
  getBoundsFromPoints,
  toLngLat,
  type RouteGeometry,
} from '@/features/route-map/lib/route-geometry'
import {
  applyLeafletLocation,
  buildMarkerTitle,
  createDiscoveryRadiusCircle,
  createGuidePolyline,
  createLeafletMap,
  createPoiIcon,
  createSegmentedRoutePolyline,
  createUserIcon,
  getPointCategoryIcon,
} from '@/features/route-map/lib/leaflet-map'
import { appMapConfig } from '@/shared/config/map'
import { appRoutes } from '@/shared/config/routes'
import { buildGoogleMapsUrl } from '@/shared/lib/maps'
import './DiscoveryMap.css'

export interface DiscoveryCategoryOption {
  id: PointCategory | 'all'
  label: string
}

export interface DiscoveryRadiusOption {
  label: string
  value: number
}

interface DiscoveryMapProps {
  activeCategory: PointCategory | 'all'
  canSaveDraftRoute?: boolean
  categoryOptions: DiscoveryCategoryOption[]
  draftStops?: RouteStop[]
  draftRouteNotice?: string | null
  embedded?: boolean
  emptyMessage: string
  fixedRouteStops?: RouteStop[]
  geolocationError: string | null
  isLoading: boolean
  loadError: string | null
  nearbyPoints: NearbyPoint[]
  onAddPointToDraft?: (point: NearbyPoint) => void
  onBuildRoute: (pointId: string) => void
  onChangeRadius: (radiusMeters: number) => void
  onClearDraftRoute?: () => void
  onLocateUser: () => void
  onSaveDraftRoute?: () => void
  onSearchQueryChange: (value: string) => void
  onSelectCategory: (category: PointCategory | 'all') => void
  onSelectNextPoint: () => void
  onSelectPoint: (pointId: string) => void
  onSelectPreviousPoint: () => void
  radiusMeters: number
  radiusOptions: DiscoveryRadiusOption[]
  routeTargetId: string | null
  searchQuery: string
  selectedPointId: string
  userPosition: GeoPoint | null
}

const mapPadding: [number, number, number, number] = [56, 48, 48, 48]
const selectedPointZoom = 16
const locateZoom = 15.5

type SelectionSource = 'marker' | 'navigation' | 'route'

function preservePageScroll() {
  const currentScrollY = window.scrollY

  window.requestAnimationFrame(() => {
    if (window.scrollY !== currentScrollY) {
      window.scrollTo({
        top: currentScrollY,
        behavior: 'auto',
      })
    }
  })
}

export function DiscoveryMap({
  activeCategory,
  canSaveDraftRoute = true,
  categoryOptions,
  draftStops = [],
  draftRouteNotice = null,
  embedded = false,
  emptyMessage,
  fixedRouteStops = [],
  geolocationError,
  isLoading,
  loadError,
  nearbyPoints,
  onAddPointToDraft,
  onBuildRoute,
  onChangeRadius,
  onClearDraftRoute,
  onLocateUser,
  onSaveDraftRoute,
  onSearchQueryChange,
  onSelectCategory,
  onSelectNextPoint,
  onSelectPoint,
  onSelectPreviousPoint,
  radiusMeters,
  radiusOptions,
  routeTargetId,
  searchQuery,
  selectedPointId,
  userPosition,
}: DiscoveryMapProps) {
  const mapContainerRef = useRef<HTMLDivElement | null>(null)
  const mapRef = useRef<L.Map | null>(null)
  const overlayRef = useRef<L.LayerGroup | null>(null)
  const markerRefs = useRef(new Map<string, L.Marker>())
  const controlsRef = useRef<HTMLDivElement | null>(null)
  const initialCenterRef = useRef(userPosition ?? appMapConfig.defaultCenter)
  const skipSelectedFocusRef = useRef(true)
  const selectionSourceRef = useRef<SelectionSource | null>(null)
  const [mapLoadError, setMapLoadError] = useState<string | null>(null)
  const [openMenu, setOpenMenu] = useState<'category' | 'radius' | null>(null)
  const [guideRoute, setGuideRoute] = useState<{
    geometry: RouteGeometry | null
    signature: string
  }>({
    geometry: null,
    signature: '',
  })
  const [draftRoute, setDraftRoute] = useState<{
    geometry: RouteGeometry | null
    signature: string
  }>({
    geometry: null,
    signature: '',
  })

  const selectedPoint =
    nearbyPoints.find((point) => point.id === selectedPointId) ?? nearbyPoints[0] ?? null
  const guidedPoint =
    nearbyPoints.find((point) => point.id === routeTargetId) ?? null
  const pointsBounds = useMemo(() => {
    const points = [
      ...nearbyPoints.map((point) => point.coordinates),
      ...(userPosition ? [userPosition] : []),
    ]

    return getBoundsFromPoints(points)
  }, [nearbyPoints, userPosition])
  const activeCategoryOption =
    categoryOptions.find((option) => option.id === activeCategory) ?? categoryOptions[0]
  const activeRadiusLabel =
    radiusOptions.find((option) => option.value === radiusMeters)?.label ?? `${radiusMeters / 1000} км`
  const canNavigatePoints = nearbyPoints.length > 1
  const visibleDraftStops = draftStops.length > 0 ? draftStops : fixedRouteStops
  const visibleDraftPointIds = useMemo(
    () => new Set(visibleDraftStops.map(getSourcePointId)),
    [visibleDraftStops],
  )
  const draftSignature = useMemo(() => {
    const points = [
      ...(userPosition ? [userPosition] : []),
      ...visibleDraftStops.map((stop) => stop.coordinates),
    ]

    return points
      .map((point) => `${point.lat.toFixed(5)}:${point.lng.toFixed(5)}`)
      .join('|')
  }, [userPosition, visibleDraftStops])
  const guideSignature =
    userPosition && guidedPoint
      ? `${guidedPoint.id}:${userPosition.lat.toFixed(5)}:${userPosition.lng.toFixed(5)}`
      : ''
  const fallbackGuideGeometry = useMemo(() => {
    if (!userPosition || !guidedPoint) {
      return null
    }

    return createLineGeometryFromPoints([userPosition, guidedPoint.coordinates])
  }, [guidedPoint, userPosition])
  const guideGeometry =
    guideRoute.signature === guideSignature && guideRoute.geometry
      ? guideRoute.geometry
      : fallbackGuideGeometry
  const draftFallbackGeometry = useMemo(() => {
    const points = [
      ...(userPosition ? [userPosition] : []),
      ...visibleDraftStops.map((stop) => stop.coordinates),
    ]

    return points.length > 1 ? createLineGeometryFromPoints(points) : null
  }, [userPosition, visibleDraftStops])
  const draftGeometry =
    draftRoute.signature === draftSignature && draftRoute.geometry
      ? draftRoute.geometry
      : draftFallbackGeometry
  const guideBounds = useMemo(
    () => (guideGeometry ? getBoundsFromGeometry(guideGeometry) : null),
    [guideGeometry],
  )
  const draftBounds = useMemo(
    () => (draftGeometry ? getBoundsFromGeometry(draftGeometry) : null),
    [draftGeometry],
  )

  useEffect(() => {
    const container = mapContainerRef.current
    const markers = markerRefs.current

    if (!container || mapRef.current) {
      return
    }

    try {
      const map = createLeafletMap(
        container,
        initialCenterRef.current,
        appMapConfig.defaultZoom,
      )
      const overlay = L.layerGroup().addTo(map)

      mapRef.current = map
      overlayRef.current = overlay
      queueMicrotask(() => setMapLoadError(null))
    } catch (error) {
      console.error(error)
      queueMicrotask(() => setMapLoadError('Не удалось открыть карту.'))
    }

    return () => {
      overlayRef.current?.clearLayers()
      overlayRef.current = null
      markers.clear()
      mapRef.current?.remove()
      mapRef.current = null
    }
  }, [])

  useEffect(() => {
    function handlePointerDown(event: PointerEvent) {
      if (!controlsRef.current?.contains(event.target as Node)) {
        setOpenMenu(null)
      }
    }

    window.addEventListener('pointerdown', handlePointerDown)

    return () => {
      window.removeEventListener('pointerdown', handlePointerDown)
    }
  }, [])

  useEffect(() => {
    const controller = new AbortController()

    async function loadGuideRoute() {
      if (!userPosition || !guidedPoint) {
        queueMicrotask(() => {
          setGuideRoute({ geometry: null, signature: '' })
        })
        return
      }

      try {
        const result = await buildOsmWalkingRouteGeometryFromPoints(
          [userPosition, guidedPoint.coordinates],
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
  }, [guideSignature, guidedPoint, userPosition])

  useEffect(() => {
    const controller = new AbortController()

    async function loadDraftRoute() {
      const points = [
        ...(userPosition ? [userPosition] : []),
        ...visibleDraftStops.map((stop) => stop.coordinates),
      ]

      if (points.length < 2) {
        queueMicrotask(() => {
          setDraftRoute({ geometry: null, signature: '' })
        })
        return
      }

      try {
        const result = await buildOsmWalkingRouteGeometryFromPoints(
          points,
          controller.signal,
        )

        if (controller.signal.aborted) {
          return
        }

        setDraftRoute({
          geometry: result.geometry,
          signature: draftSignature,
        })
      } catch (error) {
        if (!controller.signal.aborted) {
          console.error(error)
        }
      }
    }

    void loadDraftRoute()

    return () => {
      controller.abort()
    }
  }, [draftSignature, userPosition, visibleDraftStops])

  useEffect(() => {
    const map = mapRef.current

    if (!map) {
      return
    }

    if (draftBounds && visibleDraftStops.length) {
      applyLeafletLocation(map, {
        bounds: draftBounds,
        padding: mapPadding,
        duration: 700,
        easing: 'ease-in-out',
      })
      return
    }

    if (guideBounds && routeTargetId) {
      applyLeafletLocation(map, {
        bounds: guideBounds,
        padding: mapPadding,
        duration: 700,
        easing: 'ease-in-out',
      })
      return
    }

    if (!nearbyPoints.length && userPosition) {
      applyLeafletLocation(map, {
        center: toLngLat(userPosition),
        zoom: locateZoom,
        duration: 600,
        easing: 'ease-in-out',
      })
      return
    }

    if (!nearbyPoints.length) {
      return
    }

    skipSelectedFocusRef.current = true
    applyLeafletLocation(map, {
      bounds: pointsBounds,
      padding: mapPadding,
      duration: 850,
      easing: 'ease-in-out',
    })
  }, [draftBounds, guideBounds, nearbyPoints.length, pointsBounds, routeTargetId, userPosition, visibleDraftStops.length])

  useEffect(() => {
    const overlay = overlayRef.current

    if (!overlay) {
      return
    }

    overlay.clearLayers()
    markerRefs.current.clear()

    if (userPosition) {
      createDiscoveryRadiusCircle(userPosition, radiusMeters).addTo(overlay)
    }

    if (guideGeometry && !draftGeometry) {
      createGuidePolyline(guideGeometry).addTo(overlay)
    }

    if (draftGeometry) {
      createSegmentedRoutePolyline(draftGeometry).addTo(overlay)
    }

    nearbyPoints.forEach((point) => {
      const googleMapsUrl = buildGoogleMapsUrl(point.coordinates, userPosition)
      const isInDraft = visibleDraftPointIds.has(point.id)
      const marker = L.marker([point.coordinates.lat, point.coordinates.lng], {
        icon: createPoiIcon(point, point.id === selectedPointId, isInDraft),
        title: buildMarkerTitle(point),
      })
        .bindPopup(
          buildPopupContent({
            googleMapsUrl,
            onBuildRoute: () => {
              preservePageScroll()
              selectionSourceRef.current = 'route'
              onSelectPoint(point.id)
              onBuildRoute(point.id)
            },
            onAddPointToDraft: onAddPointToDraft
              ? () => {
                  preservePageScroll()
                  selectionSourceRef.current = 'route'
                  onSelectPoint(point.id)
                  onBuildRoute(point.id)
                  onAddPointToDraft(point)
                }
              : undefined,
            canAddToDraft: Boolean(onAddPointToDraft && !isInDraft && draftStops.length < 6),
            isInDraft,
            point,
          }),
          {
            autoPan: true,
            keepInView: true,
          },
        )
        .on('click', () => {
          preservePageScroll()
          selectionSourceRef.current = 'marker'
          onSelectPoint(point.id)
          marker.openPopup()
        })

      marker.addTo(overlay)
      markerRefs.current.set(point.id, marker)
    })

    if (userPosition) {
      L.marker([userPosition.lat, userPosition.lng], {
        icon: createUserIcon(),
        title: 'Ваше местоположение',
      }).addTo(overlay)
    }
  }, [draftGeometry, draftStops.length, guideGeometry, nearbyPoints, onAddPointToDraft, onBuildRoute, onSelectPoint, radiusMeters, selectedPointId, userPosition, visibleDraftPointIds])

  useEffect(() => {
    nearbyPoints.forEach((point) => {
      const marker = markerRefs.current.get(point.id)

      if (!marker) {
        return
      }

      marker.setIcon(createPoiIcon(point, point.id === selectedPointId, visibleDraftPointIds.has(point.id)))
    })
  }, [nearbyPoints, selectedPointId, visibleDraftPointIds])

  useEffect(() => {
    const map = mapRef.current
    const marker = selectedPoint ? markerRefs.current.get(selectedPoint.id) : null

    if (!map || !selectedPoint || !marker) {
      return
    }

    if (skipSelectedFocusRef.current) {
      skipSelectedFocusRef.current = false
      return
    }

    const source = selectionSourceRef.current
    selectionSourceRef.current = null

    if (source === 'marker') {
      marker.openPopup()
      return
    }

    applyLeafletLocation(map, {
      center: toLngLat(selectedPoint.coordinates),
      zoom: selectedPointZoom,
      duration: 600,
      easing: 'ease-in-out',
    })

    const popupTimeout = window.setTimeout(() => {
      marker.openPopup()
    }, 240)

    return () => {
      window.clearTimeout(popupTimeout)
    }
  }, [selectedPoint])

  function focusOnUser() {
    const map = mapRef.current

    if (!userPosition || !map) {
      onLocateUser()
      return
    }

    applyLeafletLocation(map, {
      center: toLngLat(userPosition),
      zoom: locateZoom,
      duration: 700,
      easing: 'ease-in-out',
    })
  }

  function toggleMenu(menu: 'category' | 'radius') {
    setOpenMenu((current) => (current === menu ? null : menu))
  }

  function handleCategorySelect(category: PointCategory | 'all') {
    onSelectCategory(category)
    setOpenMenu(null)
  }

  function handleRadiusSelect(nextRadius: number) {
    onChangeRadius(nextRadius)
    setOpenMenu(null)
  }

  function handleSelectPreviousPoint() {
    selectionSourceRef.current = 'navigation'
    onSelectPreviousPoint()
  }

  function handleSelectNextPoint() {
    selectionSourceRef.current = 'navigation'
    onSelectNextPoint()
  }

  return (
    <section className={`discovery-map discovery-map--wide${embedded ? ' discovery-map--embedded' : ''}`}>
      <div className="discovery-map__toolbar" ref={controlsRef}>
        <div className="discovery-map__toolbar-side discovery-map__toolbar-side--start">
          <div className={`discovery-map__dropdown${openMenu === 'category' ? ' discovery-map__dropdown--open' : ''}`}>
            <button
              aria-expanded={openMenu === 'category'}
              className="discovery-map__dropdown-trigger"
              onClick={() => toggleMenu('category')}
              type="button"
            >
              <span aria-hidden="true" className="discovery-map__dropdown-icon">{getPointCategoryIcon(activeCategoryOption.id)}</span>
              <span className="discovery-map__dropdown-value">{activeCategoryOption.label}</span>
              <span aria-hidden="true" className="discovery-map__dropdown-chevron">▾</span>
            </button>

            <div
              aria-hidden={openMenu !== 'category'}
              className={`discovery-map__dropdown-menu${openMenu === 'category' ? ' discovery-map__dropdown-menu--open' : ''}`}
              role="menu"
            >
              {categoryOptions.map((category) => (
                <button
                  className={`discovery-map__dropdown-option${activeCategory === category.id ? ' discovery-map__dropdown-option--active' : ''}`}
                  key={category.id}
                  onClick={() => handleCategorySelect(category.id)}
                  type="button"
                >
                  <span aria-hidden="true" className="discovery-map__dropdown-option-icon">{getPointCategoryIcon(category.id)}</span>
                  <span>{category.label}</span>
                </button>
              ))}
            </div>
          </div>
        </div>

        <div className="discovery-map__toolbar-title-wrap">
          <label className="discovery-map__search" htmlFor="nearby-search">
            <span aria-hidden="true" className="discovery-map__search-icon">⌕</span>
            <input
              autoComplete="off"
              className="discovery-map__search-input"
              id="nearby-search"
              onChange={(event) => onSearchQueryChange(event.target.value)}
              placeholder="Поиск мест в радиусе"
              type="search"
              value={searchQuery}
            />
          </label>
        </div>

        <div className="discovery-map__toolbar-side discovery-map__toolbar-side--end">
          <div className={`discovery-map__dropdown${openMenu === 'radius' ? ' discovery-map__dropdown--open' : ''}`}>
            <button
              aria-expanded={openMenu === 'radius'}
              className="discovery-map__dropdown-trigger"
              onClick={() => toggleMenu('radius')}
              type="button"
            >
              <span className="discovery-map__dropdown-value">{activeRadiusLabel}</span>
              <span aria-hidden="true" className="discovery-map__dropdown-chevron">▾</span>
            </button>

            <div
              aria-hidden={openMenu !== 'radius'}
              className={`discovery-map__dropdown-menu discovery-map__dropdown-menu--right${openMenu === 'radius' ? ' discovery-map__dropdown-menu--open' : ''}`}
              role="menu"
            >
              {radiusOptions.map((option) => (
                <button
                  className={`discovery-map__dropdown-option${radiusMeters === option.value ? ' discovery-map__dropdown-option--active' : ''}`}
                  key={option.value}
                  onClick={() => handleRadiusSelect(option.value)}
                  type="button"
                >
                  {option.label}
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>

      <div className="discovery-map__canvas discovery-map__canvas--wide">
        <div className="discovery-map__root" ref={mapContainerRef}></div>
        {isLoading ? (
          <div className="discovery-map__overlay-note">Ищем места рядом...</div>
        ) : null}
        {mapLoadError || loadError ? (
          <div className="discovery-map__overlay-note discovery-map__overlay-note--error">
            {mapLoadError ?? loadError}
          </div>
        ) : null}
        {!isLoading && !mapLoadError && !loadError && nearbyPoints.length === 0 ? (
          <div className="discovery-map__overlay-note">{emptyMessage}</div>
        ) : null}
        {draftRouteNotice ? (
          <div className="discovery-map__overlay-note discovery-map__overlay-note--success">
            {draftRouteNotice}
          </div>
        ) : null}
        {visibleDraftStops.length > 0 ? (
          <div className="discovery-map__draft-actions">
            <button
              className="discovery-map__draft-button"
              onClick={onClearDraftRoute}
              type="button"
            >
              Сбросить
            </button>
            {draftStops.length > 1 ? (
              canSaveDraftRoute ? (
                <button
                  className="discovery-map__draft-button discovery-map__draft-button--primary"
                  onClick={onSaveDraftRoute}
                  type="button"
                >
                  Сохранить
                </button>
              ) : (
                <Link
                  className="discovery-map__draft-button discovery-map__draft-button--primary discovery-map__draft-button--link"
                  to={appRoutes.signIn}
                >
                  Войти
                </Link>
              )
            ) : null}
          </div>
        ) : null}
      </div>

      <div className="discovery-map__navigation">
        <div className="discovery-map__navigation-side discovery-map__navigation-side--start">
          <button
            className="discovery-map__arrow-button"
            disabled={!canNavigatePoints}
            onClick={handleSelectPreviousPoint}
            type="button"
          >
            <span aria-hidden="true">←</span>
          </button>
        </div>

        <div className="discovery-map__navigation-center">
          <button className="button button--primary discovery-map__locate-button" onClick={focusOnUser} type="button">
            Найти себя
          </button>
        </div>

        <div className="discovery-map__navigation-side discovery-map__navigation-side--end">
          <button
            className="discovery-map__arrow-button"
            disabled={!canNavigatePoints}
            onClick={handleSelectNextPoint}
            type="button"
          >
            <span aria-hidden="true">→</span>
          </button>
        </div>
      </div>

      {geolocationError ? <p className="map-card__note">{geolocationError}</p> : null}
    </section>
  )
}

function buildPopupContent({
  point,
  googleMapsUrl,
  canAddToDraft,
  isInDraft,
  onAddPointToDraft,
  onBuildRoute,
}: {
  canAddToDraft: boolean
  isInDraft: boolean
  onAddPointToDraft?: () => void
  point: NearbyPoint
  googleMapsUrl: string
  onBuildRoute: () => void
}) {
  const container = document.createElement('div')
  container.className = 'map-popup'

  const title = document.createElement('strong')
  title.className = 'map-popup__title'
  title.textContent = point.title
  container.appendChild(title)

  if (point.addressLabel) {
    const meta = document.createElement('p')
    meta.className = 'map-popup__meta'
    meta.textContent = point.addressLabel
    container.appendChild(meta)
  }

  const actions = document.createElement('div')
  actions.className = 'map-popup__actions'

  const openLink = document.createElement('a')
  openLink.className = 'map-popup__link'
  openLink.href = googleMapsUrl
  openLink.rel = 'noreferrer'
  openLink.target = '_blank'
  openLink.textContent = 'Открыть в Google Maps'
  actions.appendChild(openLink)

  const routeButton = document.createElement('button')
  routeButton.className = 'map-popup__button'
  routeButton.type = 'button'
  routeButton.textContent = 'Построить маршрут'
  routeButton.addEventListener('click', (event) => {
    event.preventDefault()
    event.stopPropagation()
    onBuildRoute()
  })
  actions.appendChild(routeButton)

  const addButton = document.createElement('button')
  addButton.className = `map-popup__button map-popup__button--accent${isInDraft ? ' map-popup__button--active' : ''}`
  addButton.disabled = !canAddToDraft
  addButton.type = 'button'
  addButton.textContent = isInDraft ? 'В маршруте' : 'Добавить'
  addButton.addEventListener('click', (event) => {
    event.preventDefault()
    event.stopPropagation()
    onAddPointToDraft?.()
  })
  actions.appendChild(addButton)

  container.appendChild(actions)
  return container
}

function getSourcePointId(stop: RouteStop) {
  return stop.id.replace(/-draft-stop.*$/, '')
}




