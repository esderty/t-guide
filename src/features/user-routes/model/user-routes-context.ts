import { createContext } from 'react'

import type {
  Excursion,
  NearbyPoint,
  RouteStop,
} from '@/entities/excursion/model/types'

export interface SaveDraftRouteResult {
  route: Excursion | null
  status: 'saved' | 'duplicate' | 'invalid' | 'unauthorized'
}

export interface UserRoutesContextValue {
  draftStops: RouteStop[]
  personalRoutes: Excursion[]
  savedRoutes: Excursion[]
  addPointToDraft: (point: NearbyPoint) => void
  clearDraftRoute: () => void
  isPointInDraft: (pointId: string) => boolean
  isRouteSaved: (slug: string) => boolean
  removeDraftStop: (stopId: string) => void
  removePersonalRoute: (slug: string) => void
  removeSavedRoute: (slug: string) => void
  saveDraftRoute: () => SaveDraftRouteResult
  shareRoute: (route: Excursion) => Promise<void>
  toggleSavedRoute: (route: Excursion) => void
}

export const UserRoutesContext = createContext<UserRoutesContextValue | null>(null)
