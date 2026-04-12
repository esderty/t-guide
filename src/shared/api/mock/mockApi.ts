import { createDiscoveryFeed } from '@/entities/excursion/lib/discovery-feed'
import { createDiscoveryRoutes } from '@/entities/excursion/lib/discovery-routes'
import type { Excursion } from '@/entities/excursion/model/types'
import type {
  FrontendApi,
  ProfileOverviewDto,
  RequestPasswordResetRequestDto,
  RegisterRequestDto,
  RouteCatalogRequest,
  RouteDetailsRequest,
  SessionDto,
  SignInRequestDto,
  UpdateProfileRequestDto,
  UserProfileDto,
} from '@/shared/api/contracts'
import { appMapConfig } from '@/shared/config/map'
import { formatPointCategory } from '@/shared/lib/format'

interface MockUserRecord {
  id: string
  name: string
  email: string
  phone: string
  password: string
  language: UserProfileDto['language']
  role: UserProfileDto['role']
}

const responseDelayMs = 180
const usersStorageKey = 't-guide:auth:users'
const sessionStorageKey = 't-guide:auth:session'

const defaultUser: MockUserRecord = {
  id: 'user-1',
  name: 'Анна',
  email: 'anna@example.com',
  phone: '+79990000000',
  password: 'password123',
  language: 'ru',
  role: 'user',
}

export const mockApi: FrontendApi = {
  async createPersonalRoute(request) {
    await wait(responseDelayMs)
    return request.route
  },

  async getDiscoveryFeed(request) {
    await wait(responseDelayMs)

    const baseFeed = createDiscoveryFeed(request.center, request.locale)
    const normalizedSearch = request.search?.trim().toLocaleLowerCase() ?? ''
    const categoryFiltered =
      request.category === 'all'
        ? baseFeed.nearbyPoints
        : baseFeed.nearbyPoints.filter((point) => point.category === request.category)
    const radiusFiltered = categoryFiltered.filter(
      (point) => point.distanceMeters <= request.radiusMeters,
    )
    const searchFiltered = normalizedSearch
      ? radiusFiltered.filter((point) => {
          const haystack = [
            point.title,
            formatPointCategory(point.category),
            point.shortDescription,
            point.description,
            point.addressLabel,
          ]
            .filter(Boolean)
            .join(' ')
            .toLocaleLowerCase()

          return haystack.includes(normalizedSearch)
        })
      : radiusFiltered

    const excursions = createDiscoveryRoutes({
      activePointCategory: request.category,
      center: request.center,
      locale: request.locale,
      nearbyPoints: searchFiltered,
      radiusMeters: request.radiusMeters,
    })

    return {
      appliedCategory: request.category,
      appliedRadiusMeters: request.radiusMeters,
      center: request.center,
      excursions,
      nearbyPoints: searchFiltered,
    }
  },

  async getProfileOverview() {
    await wait(responseDelayMs)

    const session = loadSession()
    const profile = session.profile ?? toProfile(defaultUser)
    const routes = createDiscoveryRoutes({
      activePointCategory: 'all',
      center: appMapConfig.defaultCenter,
      locale: profile.language,
      nearbyPoints: createDiscoveryFeed(appMapConfig.defaultCenter, profile.language).nearbyPoints,
      radiusMeters: 3000,
    })

    return {
      profile,
      savedRoutes: [],
      personalRoutes: [],
      routeHistory: routes.slice(0, 6).map((route, index) => ({
        id: `history-${route.slug}-${index}`,
        route,
        progressPercent: index === 0 ? 64 : index === 1 ? 100 : 100,
        completedAt:
          index === 0 ? null : new Date(Date.now() - (index + 1) * 86400000).toISOString(),
        startedAt: new Date(Date.now() - (index + 2) * 86400000).toISOString(),
        status: index === 0 ? 'active' : 'completed',
      })),
    } satisfies ProfileOverviewDto
  },

  async getRouteBySlug(request) {
    const routes = await getRoutesCatalogFromRequest(request)
    const route = routes.find((item) => item.slug === request.slug)

    if (route) {
      return route
    }

    const fallbackFeed = createDiscoveryFeed(request.center, request.locale)
    const fallbackRoutes = createDiscoveryRoutes({
      activePointCategory: 'all',
      center: request.center,
      locale: request.locale,
      nearbyPoints: fallbackFeed.nearbyPoints,
      radiusMeters: 5000,
    })

    return fallbackRoutes.find((item) => item.slug === request.slug) ?? null
  },

  async getRoutesCatalog(request) {
    return getRoutesCatalogFromRequest(request)
  },

  async getSession() {
    await wait(80)
    return loadSession()
  },

  async requestPasswordReset(request: RequestPasswordResetRequestDto) {
    await wait(responseDelayMs)

    const users = loadUsers()
    const normalizedLogin = request.login.trim().toLowerCase()
    const normalizedPhone = normalizePhone(request.login)
    const user = users.find(
      (candidate) =>
        candidate.email.toLowerCase() === normalizedLogin ||
        normalizePhone(candidate.phone) === normalizedPhone,
    )

    if (!user) {
      throw new Error('Пользователь с таким email или телефоном не найден.')
    }
  },

  async register(request: RegisterRequestDto) {
    await wait(responseDelayMs)

    const users = loadUsers()
    const normalizedPhone = normalizePhone(request.phone)

    if (users.some((user) => user.email.toLowerCase() === request.email.toLowerCase())) {
      throw new Error('Пользователь с такой почтой уже существует.')
    }

    if (users.some((user) => normalizePhone(user.phone) === normalizedPhone)) {
      throw new Error('Пользователь с таким телефоном уже существует.')
    }

    const createdUser: MockUserRecord = {
      id: `user-${Date.now()}`,
      name: request.name.trim(),
      email: request.email.trim(),
      phone: request.phone.trim(),
      password: request.password,
      language: request.language,
      role: 'user',
    }

    writeUsers([createdUser, ...users])

    const nextSession: SessionDto = {
      isAuthenticated: true,
      profile: toProfile(createdUser),
    }

    writeSession(nextSession)
    return nextSession
  },

  async removeSavedRoute() {
    await wait(responseDelayMs)
  },

  async saveRoute(request) {
    await wait(responseDelayMs)
    return request.route
  },

  async shareRoute(request) {
    await wait(80)

    return {
      url: `${window.location.origin}/excursions/${request.slug}`,
    }
  },

  async signIn(request: SignInRequestDto) {
    await wait(responseDelayMs)

    const users = loadUsers()
    const normalizedLogin = request.login.trim().toLowerCase()
    const normalizedPhone = normalizePhone(request.login)
    const user = users.find(
      (candidate) =>
        candidate.email.toLowerCase() === normalizedLogin ||
        normalizePhone(candidate.phone) === normalizedPhone,
    )

    if (!user || user.password !== request.password) {
      throw new Error('Неверный логин или пароль.')
    }

    const nextSession: SessionDto = {
      isAuthenticated: true,
      profile: toProfile(user),
    }

    writeSession(nextSession)
    return nextSession
  },

  async signOut() {
    await wait(80)
    const nextSession = createGuestSession()
    writeSession(nextSession)
    return nextSession
  },

  async updateProfile(request: UpdateProfileRequestDto) {
    await wait(responseDelayMs)

    const session = loadSession()

    if (!session.profile) {
      throw new Error('Профиль недоступен.')
    }

    const users = loadUsers()
    const nextUsers = users.map((user) =>
      user.id === session.profile?.id
        ? {
            ...user,
            email: request.email.trim(),
            language: request.language,
            name: request.name.trim(),
          }
        : user,
    )
    const updatedUser = nextUsers.find((user) => user.id === session.profile?.id)

    if (!updatedUser) {
      throw new Error('Профиль недоступен.')
    }

    writeUsers(nextUsers)

    const nextProfile = toProfile(updatedUser)
    writeSession({
      isAuthenticated: true,
      profile: nextProfile,
    })

    return nextProfile
  },
}

async function getRoutesCatalogFromRequest(
  request: RouteCatalogRequest | RouteDetailsRequest,
): Promise<Excursion[]> {
  await wait(responseDelayMs)

  const baseFeed = createDiscoveryFeed(request.center, request.locale)
  const nearbyPoints =
    request.category === 'all'
      ? baseFeed.nearbyPoints
      : baseFeed.nearbyPoints.filter((point) => point.category === request.category)

  return createDiscoveryRoutes({
    activePointCategory: request.category,
    center: request.center,
    locale: request.locale,
    nearbyPoints: nearbyPoints.filter((point) => point.distanceMeters <= request.radiusMeters),
    radiusMeters: request.radiusMeters,
  })
}

function wait(ms: number) {
  return new Promise((resolve) => {
    window.setTimeout(resolve, ms)
  })
}

function loadUsers() {
  if (typeof window === 'undefined') {
    return [defaultUser]
  }

  try {
    const rawValue = window.localStorage.getItem(usersStorageKey)

    if (!rawValue) {
      writeUsers([defaultUser])
      return [defaultUser]
    }

    const parsed = JSON.parse(rawValue) as MockUserRecord[]

    if (!Array.isArray(parsed) || !parsed.length) {
      writeUsers([defaultUser])
      return [defaultUser]
    }

    return parsed
  } catch {
    writeUsers([defaultUser])
    return [defaultUser]
  }
}

function writeUsers(users: MockUserRecord[]) {
  if (typeof window === 'undefined') {
    return
  }

  window.localStorage.setItem(usersStorageKey, JSON.stringify(users))
}

function loadSession(): SessionDto {
  if (typeof window === 'undefined') {
    return createGuestSession()
  }

  try {
    const rawValue = window.localStorage.getItem(sessionStorageKey)

    if (!rawValue) {
      return createGuestSession()
    }

    const parsed = JSON.parse(rawValue) as SessionDto

    if (!parsed?.isAuthenticated || !parsed.profile) {
      return createGuestSession()
    }

    return parsed
  } catch {
    return createGuestSession()
  }
}

function writeSession(session: SessionDto) {
  if (typeof window === 'undefined') {
    return
  }

  window.localStorage.setItem(sessionStorageKey, JSON.stringify(session))
}

function createGuestSession(): SessionDto {
  return {
    isAuthenticated: false,
    profile: null,
  }
}

function toProfile(user: MockUserRecord): UserProfileDto {
  return {
    id: user.id,
    name: user.name,
    email: user.email,
    phone: user.phone,
    language: user.language,
    role: user.role,
  }
}

function normalizePhone(phone: string) {
  return phone.replace(/[^\d+]/g, '')
}
