import { request } from '@/shared/api/http'
import type {
  DiscoveryFeedDto,
  DiscoveryFeedRequest,
  FrontendApi,
  ProfileOverviewDto,
  RequestPasswordResetRequestDto,
  RegisterRequestDto,
  RemoveSavedRouteRequestDto,
  RouteCatalogRequest,
  RouteDetailsRequest,
  SaveRouteRequestDto,
  SessionDto,
  ShareRouteDto,
  ShareRouteRequestDto,
  SignInRequestDto,
  UpdateProfileRequestDto,
  UserProfileDto,
  CreatePersonalRouteRequestDto,
} from '@/shared/api/contracts'
import { mockApi } from '@/shared/api/mock/mockApi'

const useMockApi = import.meta.env.VITE_USE_MOCK_API !== 'false' || !import.meta.env.VITE_API_URL

const httpApi: FrontendApi = {
  createPersonalRoute(payload: CreatePersonalRouteRequestDto) {
    return request('/profile/routes/personal', {
      body: JSON.stringify(payload),
      method: 'POST',
    })
  },
  getDiscoveryFeed(payload: DiscoveryFeedRequest) {
    return request<DiscoveryFeedDto>('/discovery/feed', {
      body: JSON.stringify(payload),
      method: 'POST',
    })
  },
  getProfileOverview() {
    return request<ProfileOverviewDto>('/profile/overview')
  },
  getRouteBySlug({ slug, ...payload }: RouteDetailsRequest) {
    return request(`/routes/${slug}`, {
      body: JSON.stringify(payload),
      method: 'POST',
    })
  },
  getRoutesCatalog(payload: RouteCatalogRequest) {
    return request('/routes/catalog', {
      body: JSON.stringify(payload),
      method: 'POST',
    })
  },
  getSession() {
    return request<SessionDto>('/auth/session')
  },
  requestPasswordReset(payload: RequestPasswordResetRequestDto) {
    return request<void>('/auth/reset-password', {
      body: JSON.stringify(payload),
      method: 'POST',
    })
  },
  register(payload: RegisterRequestDto) {
    return request<SessionDto>('/auth/register', {
      body: JSON.stringify(payload),
      method: 'POST',
    })
  },
  removeSavedRoute(payload: RemoveSavedRouteRequestDto) {
    return request<void>(`/profile/routes/saved/${payload.slug}`, {
      method: 'DELETE',
    })
  },
  saveRoute(payload: SaveRouteRequestDto) {
    return request('/profile/routes/saved', {
      body: JSON.stringify(payload),
      method: 'POST',
    })
  },
  shareRoute(payload: ShareRouteRequestDto) {
    return request<ShareRouteDto>(`/routes/${payload.slug}/share`, {
      method: 'POST',
    })
  },
  signIn(payload: SignInRequestDto) {
    return request<SessionDto>('/auth/sign-in', {
      body: JSON.stringify(payload),
      method: 'POST',
    })
  },
  signOut() {
    return request<SessionDto>('/auth/sign-out', {
      method: 'POST',
    })
  },
  updateProfile(payload: UpdateProfileRequestDto) {
    return request<UserProfileDto>('/profile', {
      body: JSON.stringify(payload),
      method: 'PATCH',
    })
  },
}

export const appApi: FrontendApi = useMockApi ? mockApi : httpApi
