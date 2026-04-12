import type {
  Excursion,
  GeoPoint,
  NearbyPoint,
  PointCategory,
  SupportedLocale,
} from '@/entities/excursion/model/types'

export type UserRole = 'guest' | 'user' | 'admin'

export interface UserProfileDto {
  id: string
  name: string
  email: string
  phone?: string
  language: SupportedLocale
  role: UserRole
}

export interface SessionDto {
  isAuthenticated: boolean
  profile: UserProfileDto | null
}

export interface SignInRequestDto {
  login: string
  password: string
}

export interface RequestPasswordResetRequestDto {
  login: string
}

export interface RegisterRequestDto {
  name: string
  phone: string
  email: string
  password: string
  language: SupportedLocale
}

export interface UpdateProfileRequestDto {
  name: string
  email: string
  language: SupportedLocale
}

export interface SaveRouteRequestDto {
  route: Excursion
}

export interface RemoveSavedRouteRequestDto {
  slug: string
}

export interface CreatePersonalRouteRequestDto {
  route: Excursion
}

export interface ShareRouteRequestDto {
  slug: string
}

export interface ShareRouteDto {
  url: string
}

export interface RouteHistoryItemDto {
  id: string
  route: Excursion
  progressPercent: number
  completedAt: string | null
  startedAt: string
  status: 'active' | 'completed'
}

export interface ProfileOverviewDto {
  profile: UserProfileDto
  savedRoutes: Excursion[]
  personalRoutes: Excursion[]
  routeHistory: RouteHistoryItemDto[]
}

export interface DiscoveryFeedRequest {
  center: GeoPoint
  locale: SupportedLocale
  radiusMeters: number
  category: PointCategory | 'all'
  search?: string
}

export interface DiscoveryFeedDto {
  appliedCategory: PointCategory | 'all'
  appliedRadiusMeters: number
  center: GeoPoint
  excursions: Excursion[]
  nearbyPoints: NearbyPoint[]
}

export interface RouteCatalogRequest {
  center: GeoPoint
  locale: SupportedLocale
  radiusMeters: number
  category: PointCategory | 'all'
}

export interface RouteDetailsRequest {
  center: GeoPoint
  locale: SupportedLocale
  radiusMeters: number
  category: PointCategory | 'all'
  slug: string
}

export interface FrontendApi {
  createPersonalRoute(request: CreatePersonalRouteRequestDto): Promise<Excursion>
  getDiscoveryFeed(request: DiscoveryFeedRequest): Promise<DiscoveryFeedDto>
  getProfileOverview(): Promise<ProfileOverviewDto>
  getRouteBySlug(request: RouteDetailsRequest): Promise<Excursion | null>
  getRoutesCatalog(request: RouteCatalogRequest): Promise<Excursion[]>
  getSession(): Promise<SessionDto>
  requestPasswordReset(request: RequestPasswordResetRequestDto): Promise<void>
  register(request: RegisterRequestDto): Promise<SessionDto>
  removeSavedRoute(request: RemoveSavedRouteRequestDto): Promise<void>
  saveRoute(request: SaveRouteRequestDto): Promise<Excursion>
  shareRoute(request: ShareRouteRequestDto): Promise<ShareRouteDto>
  signIn(request: SignInRequestDto): Promise<SessionDto>
  signOut(): Promise<SessionDto>
  updateProfile(request: UpdateProfileRequestDto): Promise<UserProfileDto>
}
