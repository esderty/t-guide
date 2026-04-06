export type SupportedLocale = 'ru' | 'en' | 'de' | 'fr' | 'es'
export type ExcursionDifficulty = 'easy' | 'medium' | 'hard'
export type ExcursionTheme = 'walk' | 'food' | 'nature' | 'fun' | 'mixed'
export type PointCategory =
  | 'museum'
  | 'food'
  | 'park'
  | 'entertainment'
  | 'landmark'

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
  category: PointCategory
  shortDescription: string
  description: string
  coordinates: GeoPoint
  imageUrl: string
  expectedVisitMinutes: number
  rating: number
  scheduleLabel: string
  audio: AudioStory
}

export interface NearbyPoint {
  id: string
  title: string
  category: PointCategory
  shortDescription: string
  description: string
  coordinates: GeoPoint
  imageUrl: string
  wikipediaTitle?: string
  wikidataId?: string
  expectedVisitMinutes: number
  rating: number
  scheduleLabel: string
  distanceMeters: number
  addressLabel?: string
  googleMapsUrl?: string
  source?: 'mock' | 'osm'
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
  audienceLabel: string
  stops: RouteStop[]
}