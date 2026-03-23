export type SupportedLocale = 'ru'
export type ExcursionDifficulty = 'easy' | 'medium' | 'hard'
export type ExcursionTheme =
  | 'history'
  | 'architecture'
  | 'waterfront'
  | 'culture'
  | 'panoramas'
  | 'legends'
  | 'family'
  | 'modernism'

export interface GeoPoint {
  lat: number
  lng: number
}

export interface AudioStory {
  id: string
  url: string | null
  durationSeconds: number
  language: SupportedLocale
  transcriptPreview: string
}

export interface RouteStop {
  id: string
  order: number
  title: string
  shortDescription: string
  description: string
  coordinates: GeoPoint
  imageUrl: string
  expectedVisitMinutes: number
  audio: AudioStory
}

export interface Excursion {
  id: number
  slug: string
  createdAt: string
  title: string
  tagline: string
  description: string
  theme: ExcursionTheme
  district: string
  durationMinutes: number
  distanceKm: number
  startLabel: string
  finishLabel: string
  coverImageUrl: string
  routeColor: string
  difficulty: ExcursionDifficulty
  stops: RouteStop[]
}
