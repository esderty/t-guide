import {
  useCallback,
  useMemo,
  useState,
  type ReactNode,
} from 'react'

import { useAuth } from '@/app/providers/useAuth'
import type {
  AudioStory,
  Excursion,
  GeoPoint,
  NearbyPoint,
  RouteStop,
} from '@/entities/excursion/model/types'
import { getDistanceMetersBetween } from '@/features/route-map/lib/route-geometry'
import { UserRoutesContext } from '@/features/user-routes/model/user-routes-context'
import { appApi } from '@/shared/api/client'

const maxDraftStops = 6
const storageKeyPrefix = 't-guide:user-routes'

interface StoredUserRoutes {
  personalRoutes: Excursion[]
  savedRoutes: Excursion[]
}

interface UserRoutesProviderProps {
  children: ReactNode
}

export function UserRoutesProvider({ children }: UserRoutesProviderProps) {
  const { session } = useAuth()
  const [savedRoutes, setSavedRoutes] = useState<Excursion[]>(() =>
    loadStoredRoutes(session?.profile?.id ?? 'guest').savedRoutes,
  )
  const [personalRoutes, setPersonalRoutes] = useState<Excursion[]>(() =>
    loadStoredRoutes(session?.profile?.id ?? 'guest').personalRoutes,
  )
  const [draftStops, setDraftStops] = useState<RouteStop[]>([])
  const isAuthenticated = Boolean(session?.isAuthenticated && session.profile)
  const storageScope = session?.profile?.id ?? 'guest'

  const persistRoutes = useCallback(
    (nextState: StoredUserRoutes) => {
      setSavedRoutes(nextState.savedRoutes)
      setPersonalRoutes(nextState.personalRoutes)
      writeStoredRoutes(storageScope, nextState)
    },
    [storageScope],
  )

  const isRouteSaved = useCallback(
    (slug: string) => savedRoutes.some((route) => route.slug === slug),
    [savedRoutes],
  )

  const isPointInDraft = useCallback(
    (pointId: string) => draftStops.some((stop) => getSourcePointId(stop.id) === pointId),
    [draftStops],
  )

  const toggleSavedRoute = useCallback(
    (route: Excursion) => {
      if (!isAuthenticated) {
        return
      }

      const alreadySaved = savedRoutes.some((savedRoute) => savedRoute.slug === route.slug)
      const nextSavedRoutes = alreadySaved
        ? savedRoutes.filter((savedRoute) => savedRoute.slug !== route.slug)
        : [route, ...savedRoutes]

      persistRoutes({
        personalRoutes,
        savedRoutes: dedupeRoutes(nextSavedRoutes),
      })

      if (alreadySaved) {
        void appApi.removeSavedRoute({ slug: route.slug })
      } else {
        void appApi.saveRoute({ route })
      }
    },
    [isAuthenticated, persistRoutes, personalRoutes, savedRoutes],
  )

  const removeSavedRoute = useCallback(
    (slug: string) => {
      if (!isAuthenticated) {
        return
      }

      persistRoutes({
        personalRoutes,
        savedRoutes: savedRoutes.filter((route) => route.slug !== slug),
      })
      void appApi.removeSavedRoute({ slug })
    },
    [isAuthenticated, persistRoutes, personalRoutes, savedRoutes],
  )

  const removePersonalRoute = useCallback(
    (slug: string) => {
      if (!isAuthenticated) {
        return
      }

      persistRoutes({
        personalRoutes: personalRoutes.filter((route) => route.slug !== slug),
        savedRoutes,
      })
    },
    [isAuthenticated, persistRoutes, personalRoutes, savedRoutes],
  )

  const addPointToDraft = useCallback((point: NearbyPoint) => {
    setDraftStops((currentStops) => {
      if (
        currentStops.length >= maxDraftStops ||
        currentStops.some((stop) => getSourcePointId(stop.id) === point.id)
      ) {
        return currentStops
      }

      return [
        ...currentStops,
        createRouteStopFromPoint(point, currentStops.length + 1),
      ]
    })
  }, [])

  const removeDraftStop = useCallback((stopId: string) => {
    setDraftStops((currentStops) =>
      currentStops
        .filter((stop) => stop.id !== stopId)
        .map((stop, index) => ({
          ...stop,
          order: index + 1,
        })),
    )
  }, [])

  const clearDraftRoute = useCallback(() => {
    setDraftStops([])
  }, [])

  const saveDraftRoute = useCallback(() => {
    if (!isAuthenticated) {
      return {
        route: null,
        status: 'unauthorized' as const,
      }
    }

    if (draftStops.length < 2) {
      return {
        route: null,
        status: 'invalid' as const,
      }
    }

    const draftSignature = getRouteSignature(draftStops)
    const duplicateRoute = personalRoutes.find(
      (route) => getRouteSignature(route.stops) === draftSignature,
    )

    if (duplicateRoute) {
      return {
        route: duplicateRoute,
        status: 'duplicate' as const,
      }
    }

    const route = createPersonalRoute(draftStops)
    const nextPersonalRoutes = dedupeRoutes([route, ...personalRoutes])

    persistRoutes({
      personalRoutes: nextPersonalRoutes,
      savedRoutes,
    })
    void appApi.createPersonalRoute({ route })

    return {
      route,
      status: 'saved' as const,
    }
  }, [draftStops, isAuthenticated, persistRoutes, personalRoutes, savedRoutes])

  const shareRoute = useCallback(async (route: Excursion) => {
    const fallbackRouteUrl = `${window.location.origin}/excursions/${route.slug}`
    const routeUrl = await appApi
      .shareRoute({ slug: route.slug })
      .then((response) => response.url)
      .catch(() => fallbackRouteUrl)
    const shareData = {
      text: route.tagline,
      title: route.title,
      url: routeUrl,
    }

    if (navigator.share) {
      await navigator.share(shareData)
      return
    }

    if (navigator.clipboard?.writeText) {
      await navigator.clipboard.writeText(routeUrl)
    }
  }, [])

  const value = useMemo(
    () => ({
      addPointToDraft,
      clearDraftRoute,
      draftStops,
      isPointInDraft,
      isRouteSaved,
      personalRoutes,
      removeDraftStop,
      removePersonalRoute,
      removeSavedRoute,
      saveDraftRoute,
      savedRoutes,
      shareRoute,
      toggleSavedRoute,
    }),
    [
      addPointToDraft,
      clearDraftRoute,
      draftStops,
      isPointInDraft,
      isRouteSaved,
      personalRoutes,
      removeDraftStop,
      removePersonalRoute,
      removeSavedRoute,
      saveDraftRoute,
      savedRoutes,
      shareRoute,
      toggleSavedRoute,
    ],
  )

  return (
    <UserRoutesContext.Provider value={value}>
      {children}
    </UserRoutesContext.Provider>
  )
}

function loadStoredRoutes(storageScope: string): StoredUserRoutes {
  if (typeof window === 'undefined') {
    return {
      personalRoutes: [],
      savedRoutes: [],
    }
  }

  try {
    const rawValue = window.localStorage.getItem(getStorageKey(storageScope))

    if (!rawValue) {
      return {
        personalRoutes: [],
        savedRoutes: [],
      }
    }

    const parsed = JSON.parse(rawValue) as Partial<StoredUserRoutes>

    return {
      personalRoutes: Array.isArray(parsed.personalRoutes) ? parsed.personalRoutes : [],
      savedRoutes: Array.isArray(parsed.savedRoutes) ? parsed.savedRoutes : [],
    }
  } catch {
    return {
      personalRoutes: [],
      savedRoutes: [],
    }
  }
}

function writeStoredRoutes(storageScope: string, state: StoredUserRoutes) {
  try {
    window.localStorage.setItem(getStorageKey(storageScope), JSON.stringify(state))
  } catch {
    return
  }
}

function getStorageKey(storageScope: string) {
  return `${storageKeyPrefix}:${storageScope}`
}

function dedupeRoutes(routes: Excursion[]) {
  const seenSlugs = new Set<string>()

  return routes.filter((route) => {
    if (seenSlugs.has(route.slug)) {
      return false
    }

    seenSlugs.add(route.slug)
    return true
  })
}

function createRouteStopFromPoint(point: NearbyPoint, order: number): RouteStop {
  return {
    audio: createDraftAudio(point),
    category: point.category,
    coordinates: point.coordinates,
    description: point.description,
    expectedVisitMinutes: point.expectedVisitMinutes,
    id: `${point.id}-draft-stop`,
    imageUrl: point.imageUrl,
    order,
    rating: point.rating,
    scheduleLabel: point.scheduleLabel,
    shortDescription: point.shortDescription,
    title: point.title,
  }
}

function createDraftAudio(point: NearbyPoint): AudioStory {
  return {
    durationSeconds: 90,
    id: `${point.id}-draft-audio`,
    language: 'ru',
    transcriptPreview: `Короткий рассказ о точке «${point.title}» будет доступен во время прогулки.`,
    url: null,
  }
}

function createPersonalRoute(stops: RouteStop[]): Excursion {
  const now = Date.now()
  const distanceKm = getRouteDistanceKm(stops.map((stop) => stop.coordinates))
  const visitMinutes = stops.reduce((total, stop) => total + stop.expectedVisitMinutes, 0)
  const transitMinutes = Math.max(8, Math.round(distanceKm * 12))

  return {
    audienceLabel: 'Личный маршрут',
    coverImageUrl: stops[0]?.imageUrl ?? '',
    createdAt: new Date(now).toISOString(),
    description: 'Маршрут собран из выбранных мест рядом.',
    difficulty: stops.length > 4 ? 'hard' : stops.length > 2 ? 'medium' : 'easy',
    distanceKm,
    district: 'Личная подборка',
    durationMinutes: visitMinutes + transitMinutes,
    finishLabel: stops.at(-1)?.title ?? 'Финиш',
    id: now,
    routeColor: '#1f8a70',
    slug: `personal-${now}`,
    startLabel: stops[0]?.title ?? 'Старт',
    stops: stops.map((stop, index) => ({
      ...stop,
      id: `${stop.id}-${now}`,
      order: index + 1,
    })),
    tagline: 'Собственная прогулка по выбранным точкам',
    theme: 'mixed',
    title: `Личный маршрут на ${stops.length} точки`,
  }
}

function getRouteDistanceKm(points: GeoPoint[]) {
  if (points.length < 2) {
    return 0
  }

  let distanceMeters = 0

  for (let index = 0; index < points.length - 1; index += 1) {
    distanceMeters += getDistanceMetersBetween(points[index], points[index + 1])
  }

  return Number(Math.max(0.2, distanceMeters / 1000).toFixed(1))
}

function getRouteSignature(stops: RouteStop[]) {
  return stops
    .map((stop) => getSourcePointId(stop.id))
    .join('|')
}

function getSourcePointId(stopId: string) {
  return stopId.replace(/-draft-stop(?:-\d+)?$/, '')
}
