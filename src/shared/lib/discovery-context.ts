import type {
  GeoPoint,
  PointCategory,
  SupportedLocale,
} from '@/entities/excursion/model/types'
import { appMapConfig } from '@/shared/config/map'

export interface DiscoveryContext {
  activePointCategory: PointCategory | 'all'
  center: GeoPoint
  locale: SupportedLocale
  browserLocale: string
  radiusMeters: number
  updatedAt: string
}

const discoveryContextKey = 't-guide:discovery-context'

export function detectSupportedLocale(localeCandidate?: string): SupportedLocale {
  const normalizedLocale = (localeCandidate ?? '').toLowerCase()

  if (normalizedLocale.startsWith('ru')) {
    return 'ru'
  }

  if (normalizedLocale.startsWith('de')) {
    return 'de'
  }

  if (normalizedLocale.startsWith('fr')) {
    return 'fr'
  }

  if (normalizedLocale.startsWith('es')) {
    return 'es'
  }

  return 'en'
}

export function getDefaultDiscoveryContext(): DiscoveryContext {
  const browserLocale =
    typeof window === 'undefined'
      ? 'ru-RU'
      : navigator.languages?.[0] ?? navigator.language ?? 'ru-RU'

  return {
    activePointCategory: 'all',
    center: appMapConfig.defaultCenter,
    locale: detectSupportedLocale(browserLocale),
    browserLocale,
    radiusMeters: 1000,
    updatedAt: new Date().toISOString(),
  }
}

export function getStoredDiscoveryContext(): DiscoveryContext {
  if (typeof window === 'undefined') {
    return getDefaultDiscoveryContext()
  }

  const defaultContext = getDefaultDiscoveryContext()
  const serializedValue = sessionStorage.getItem(discoveryContextKey)

  if (!serializedValue) {
    return defaultContext
  }

  try {
    const parsedValue = JSON.parse(serializedValue) as Partial<DiscoveryContext>

    return {
      activePointCategory: parsedValue.activePointCategory ?? defaultContext.activePointCategory,
      center:
        parsedValue.center?.lat !== undefined && parsedValue.center?.lng !== undefined
          ? parsedValue.center
          : defaultContext.center,
      locale: parsedValue.locale ?? defaultContext.locale,
      browserLocale: parsedValue.browserLocale ?? defaultContext.browserLocale,
      radiusMeters:
        typeof parsedValue.radiusMeters === 'number'
          ? parsedValue.radiusMeters
          : defaultContext.radiusMeters,
      updatedAt: parsedValue.updatedAt ?? defaultContext.updatedAt,
    }
  } catch {
    return defaultContext
  }
}

export function saveDiscoveryContext(context: DiscoveryContext) {
  if (typeof window === 'undefined') {
    return
  }

  sessionStorage.setItem(discoveryContextKey, JSON.stringify(context))
}
