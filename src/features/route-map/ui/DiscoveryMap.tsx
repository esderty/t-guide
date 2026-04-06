import { useEffect, useMemo, useRef, useState } from 'react'
import * as L from 'leaflet'

import type {
  GeoPoint,
  NearbyPoint,
  PointCategory,
} from '@/entities/excursion/model/types'
import { buildGoogleMapsUrl } from '@/entities/place/api/osm-nearby'
import {
  getBoundsFromPoints,
  toLngLat,
} from '@/features/route-map/lib/route-geometry'
import {
  applyLeafletLocation,
  buildMarkerTitle,
  createDiscoveryRadiusCircle,
  createLeafletMap,
  createPoiIcon,
  createUserIcon,
  getPointCategoryIcon,
} from '@/features/route-map/lib/leaflet-map'
import { appMapConfig } from '@/shared/config/map'

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
  categoryOptions: DiscoveryCategoryOption[]
  embedded?: boolean
  emptyMessage: string
  geolocationError: string | null
  isLoading: boolean
  loadError: string | null
  nearbyPoints: NearbyPoint[]
  onChangeRadius: (radiusMeters: number) => void
  onLocateUser: () => void
  onSearchQueryChange: (value: string) => void
  onSelectCategory: (category: PointCategory | 'all') => void
  onSelectNextPoint: () => void
  onSelectPoint: (pointId: string) => void
  onSelectPreviousPoint: () => void
  radiusMeters: number
  radiusOptions: DiscoveryRadiusOption[]
  searchQuery: string
  selectedPointId: string
  userPosition: GeoPoint | null
}

const mapPadding: [number, number, number, number] = [56, 48, 48, 48]
const selectedPointZoom = 16
const locateZoom = 15.5

export function DiscoveryMap({
  activeCategory,
  categoryOptions,
  embedded = false,
  emptyMessage,
  geolocationError,
  isLoading,
  loadError,
  nearbyPoints,
  onChangeRadius,
  onLocateUser,
  onSearchQueryChange,
  onSelectCategory,
  onSelectNextPoint,
  onSelectPoint,
  onSelectPreviousPoint,
  radiusMeters,
  radiusOptions,
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
  const [mapLoadError, setMapLoadError] = useState<string | null>(null)
  const [openMenu, setOpenMenu] = useState<'category' | 'radius' | null>(null)

  const selectedPoint =
    nearbyPoints.find((point) => point.id === selectedPointId) ?? nearbyPoints[0] ?? null
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
    const map = mapRef.current

    if (!map) {
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
  }, [nearbyPoints.length, pointsBounds, userPosition])

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

    nearbyPoints.forEach((point) => {
      const googleMapsUrl = buildGoogleMapsUrl(point.coordinates, userPosition)
      const marker = L.marker([point.coordinates.lat, point.coordinates.lng], {
        icon: createPoiIcon(point, false),
        title: buildMarkerTitle(point),
      })
        .bindPopup(buildPopupMarkup(point, googleMapsUrl), {
          autoPan: true,
          keepInView: true,
        })
        .on('click', () => {
          marker.openPopup()
          onSelectPoint(point.id)
        })

      marker.addTo(overlay)
      markerRefs.current.set(point.id, marker)
    })

    if (userPosition) {
      L.marker([userPosition.lat, userPosition.lng], {
        icon: createUserIcon(),
        title: 'Текущее местоположение пользователя',
      }).addTo(overlay)
    }
  }, [nearbyPoints, onSelectPoint, radiusMeters, userPosition])

  useEffect(() => {
    nearbyPoints.forEach((point) => {
      const marker = markerRefs.current.get(point.id)

      if (!marker) {
        return
      }

      marker.setIcon(createPoiIcon(point, point.id === selectedPointId))
    })
  }, [nearbyPoints, selectedPointId])

  useEffect(() => {
    const map = mapRef.current
    const markers = markerRefs.current

    if (!map || !selectedPoint) {
      return
    }

    if (skipSelectedFocusRef.current) {
      skipSelectedFocusRef.current = false
      const initialTimeout = window.setTimeout(() => {
        markers.get(selectedPoint.id)?.openPopup()
      }, 40)

      return () => {
        window.clearTimeout(initialTimeout)
      }
    }

    applyLeafletLocation(map, {
      center: toLngLat(selectedPoint.coordinates),
      zoom: selectedPointZoom,
      duration: 600,
      easing: 'ease-in-out',
    })

    const popupTimeout = window.setTimeout(() => {
      markers.get(selectedPoint.id)?.openPopup()
    }, 260)

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
      </div>

      <div className="discovery-map__navigation">
        <div className="discovery-map__navigation-side discovery-map__navigation-side--start">
          <button
            className="discovery-map__arrow-button"
            disabled={!canNavigatePoints}
            onClick={onSelectPreviousPoint}
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
            onClick={onSelectNextPoint}
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

function buildPopupMarkup(point: NearbyPoint, googleMapsUrl: string) {
  const popupAddress = point.addressLabel
    ? `<p class="map-popup__meta">${escapeHtml(point.addressLabel)}</p>`
    : ''

  return `
    <div class="map-popup">
      <strong class="map-popup__title">${escapeHtml(point.title)}</strong>
      ${popupAddress}
      <a class="map-popup__link" href="${googleMapsUrl}" target="_blank" rel="noreferrer">Открыть в Google Maps</a>
    </div>
  `
}

function escapeHtml(value: string) {
  return value
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#39;')
}
