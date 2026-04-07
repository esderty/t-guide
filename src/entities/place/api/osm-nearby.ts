import type {
  GeoPoint,
  NearbyPoint,
  PointCategory,
  SupportedLocale,
} from '@/entities/excursion/model/types'
import { resolveNearbyPointImageUrl } from '@/entities/place/lib/place-images'
import { getDistanceMetersBetween } from '@/features/route-map/lib/route-geometry'

interface FetchNearbyOsmPlacesParams {
  category: PointCategory | 'all'
  center: GeoPoint
  locale: SupportedLocale
  radiusMeters: number
}

interface OverpassElement {
  center?: {
    lat: number
    lon: number
  }
  id: number
  lat?: number
  lon?: number
  tags?: Record<string, string>
  type: 'node' | 'way' | 'relation'
}

interface OverpassResponse {
  elements?: OverpassElement[]
}

interface TagSelector {
  category: PointCategory
  key: string
  value: string
}

const overpassEndpoints = [
  'https://overpass-api.de/api/interpreter',
  'https://overpass.private.coffee/api/interpreter',
]

const defaultVisitMinutes: Record<PointCategory, number> = {
  museum: 35,
  entertainment: 30,
  landmark: 18,
  food: 24,
  park: 28,
}

const historicLandmarkValues = new Set([
  'monument',
  'memorial',
  'castle',
  'ruins',
  'archaeological_site',
  'fort',
  'city_gate',
  'tower',
  'manor',
  'wayside_cross',
  'wayside_shrine',
])

const selectorsByCategory: Record<PointCategory, TagSelector[]> = {
  museum: [
    { category: 'museum', key: 'tourism', value: 'museum' },
    { category: 'museum', key: 'tourism', value: 'gallery' },
    { category: 'museum', key: 'building:use', value: 'museum' },
  ],
  entertainment: [
    { category: 'entertainment', key: 'tourism', value: 'attraction' },
    { category: 'entertainment', key: 'amenity', value: 'cinema' },
    { category: 'entertainment', key: 'amenity', value: 'theatre' },
    { category: 'entertainment', key: 'amenity', value: 'arts_centre' },
    { category: 'entertainment', key: 'leisure', value: 'amusement_arcade' },
  ],
  landmark: [
    { category: 'landmark', key: 'historic', value: 'monument' },
    { category: 'landmark', key: 'historic', value: 'memorial' },
    { category: 'landmark', key: 'historic', value: 'castle' },
    { category: 'landmark', key: 'historic', value: 'ruins' },
    { category: 'landmark', key: 'historic', value: 'archaeological_site' },
    { category: 'landmark', key: 'historic', value: 'fort' },
    { category: 'landmark', key: 'historic', value: 'city_gate' },
    { category: 'landmark', key: 'historic', value: 'tower' },
    { category: 'landmark', key: 'historic', value: 'manor' },
    { category: 'landmark', key: 'historic', value: 'wayside_cross' },
    { category: 'landmark', key: 'historic', value: 'wayside_shrine' },
  ],
  food: [
    { category: 'food', key: 'amenity', value: 'restaurant' },
    { category: 'food', key: 'amenity', value: 'cafe' },
    { category: 'food', key: 'amenity', value: 'fast_food' },
    { category: 'food', key: 'amenity', value: 'bar' },
    { category: 'food', key: 'amenity', value: 'pub' },
    { category: 'food', key: 'shop', value: 'bakery' },
  ],
  park: [
    { category: 'park', key: 'leisure', value: 'park' },
    { category: 'park', key: 'leisure', value: 'garden' },
    { category: 'park', key: 'leisure', value: 'nature_reserve' },
    { category: 'park', key: 'boundary', value: 'national_park' },
    { category: 'park', key: 'boundary', value: 'protected_area' },
  ],
}

const localeText = {
  ru: {
    addressUnknown: 'Адрес не указан.',
    hoursUnknown: 'Часы не указаны',
    overpassError: 'Не удалось получить места рядом из OpenStreetMap.',
  },
  en: {
    addressUnknown: 'Address not specified.',
    hoursUnknown: 'Hours not specified',
    overpassError: 'Failed to load nearby places from OpenStreetMap.',
  },
} as const

const cache = new Map<string, NearbyPoint[]>()

export async function fetchNearbyOsmPlaces({
  category,
  center,
  locale,
  radiusMeters,
}: FetchNearbyOsmPlacesParams): Promise<NearbyPoint[]> {
  const cacheKey = createCacheKey(category, center, locale, radiusMeters)
  const cached = cache.get(cacheKey)

  if (cached) {
    return cached
  }

  const selectors =
    category === 'all'
      ? Object.values(selectorsByCategory).flat()
      : selectorsByCategory[category]

  const query = buildOverpassQuery(selectors, center, radiusMeters)
  const elements = await fetchOverpassElements(query, locale)
  const explicitCategory = category === 'all' ? null : category

  const points = elements
    .map((element) => toNearbyPoint(element, center, locale, explicitCategory))
    .filter((point): point is NearbyPoint => Boolean(point))
    .filter((point) => point.distanceMeters <= radiusMeters)
    .filter(uniqueByCompositeKey)
    .sort((left, right) => left.distanceMeters - right.distanceMeters)

  cache.set(cacheKey, points)
  return points
}

export function buildGoogleMapsUrl(destination: GeoPoint, userPosition?: GeoPoint | null) {
  const params = new URLSearchParams({
    api: '1',
    destination: `${destination.lat},${destination.lng}`,
    travelmode: 'walking',
  })

  if (userPosition) {
    params.set('origin', `${userPosition.lat},${userPosition.lng}`)
  }

  return `https://www.google.com/maps/dir/?${params.toString()}`
}

function buildOverpassQuery(
  selectors: TagSelector[],
  center: GeoPoint,
  radiusMeters: number,
) {
  const selectorLines = selectors.flatMap((selector) => [
    `node["${selector.key}"="${selector.value}"](around:${radiusMeters},${center.lat},${center.lng});`,
    `way["${selector.key}"="${selector.value}"](around:${radiusMeters},${center.lat},${center.lng});`,
    `relation["${selector.key}"="${selector.value}"](around:${radiusMeters},${center.lat},${center.lng});`,
  ])

  return ['[out:json][timeout:25];', '(', ...selectorLines, ');', 'out center;'].join('')
}

async function fetchOverpassElements(query: string, locale: SupportedLocale) {
  let lastError: Error | null = null

  for (const endpoint of overpassEndpoints) {
    const controller = new AbortController()
    const timeoutId = window.setTimeout(() => controller.abort(), 18000)

    try {
      const body = new URLSearchParams({ data: query })
      const response = await fetch(endpoint, {
        method: 'POST',
        headers: {
          Accept: 'application/json',
          'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
        },
        body,
        signal: controller.signal,
      })

      if (!response.ok) {
        throw new Error(`Overpass responded with ${response.status}`)
      }

      const payload = (await response.json()) as OverpassResponse

      if (!payload.elements) {
        throw new Error('Overpass returned no elements')
      }

      return payload.elements
    } catch (error) {
      lastError = normalizeError(error)
    } finally {
      window.clearTimeout(timeoutId)
    }
  }

  const localeKey = locale === 'ru' ? 'ru' : 'en'
  throw lastError ?? new Error(localeText[localeKey].overpassError)
}

function toNearbyPoint(
  element: OverpassElement,
  center: GeoPoint,
  locale: SupportedLocale,
  explicitCategory: PointCategory | null,
): NearbyPoint | null {
  const tags = element.tags ?? {}
  const coordinates = getElementCoordinates(element)

  if (!coordinates || isUnavailablePlace(tags)) {
    return null
  }

  const category = explicitCategory ?? resolveCategoryFromTags(tags)

  if (!category || !hasReliableIdentity(tags, category)) {
    return null
  }

  const title = resolveTitle(tags, locale)

  if (!title) {
    return null
  }

  const localeKey = locale === 'ru' ? 'ru' : 'en'
  const typeLabel = resolveTypeLabel(category, tags, locale)
  const addressLabel = resolveAddress(tags)
  const distanceMeters = getDistanceMetersBetween(center, coordinates)

  return {
    id: `${element.type}-${element.id}`,
    title,
    category,
    shortDescription: addressLabel ? `${typeLabel} · ${addressLabel}` : typeLabel,
    description: addressLabel
      ? `${typeLabel}. ${addressLabel}`
      : `${typeLabel}. ${localeText[localeKey].addressUnknown}`,
    coordinates,
    imageUrl: resolveNearbyPointImageUrl(tags, coordinates, category),
    wikipediaTitle: normalizeWikipediaTitle(tags.wikipedia),
    wikidataId: tags.wikidata,
    expectedVisitMinutes: defaultVisitMinutes[category],
    rating: 0,
    scheduleLabel: tags.opening_hours?.trim() || localeText[localeKey].hoursUnknown,
    distanceMeters,
    addressLabel,
    googleMapsUrl: buildGoogleMapsUrl(coordinates, center),
    source: 'osm',
  }
}

function getElementCoordinates(element: OverpassElement): GeoPoint | null {
  if (typeof element.lat === 'number' && typeof element.lon === 'number') {
    return { lat: element.lat, lng: element.lon }
  }

  if (element.center) {
    return {
      lat: element.center.lat,
      lng: element.center.lon,
    }
  }

  return null
}

function resolveCategoryFromTags(tags: Record<string, string>): PointCategory | null {
  if (
    tags.tourism === 'museum' ||
    tags.tourism === 'gallery' ||
    tags['building:use'] === 'museum'
  ) {
    return 'museum'
  }

  if (
    tags.amenity === 'restaurant' ||
    tags.amenity === 'cafe' ||
    tags.amenity === 'fast_food' ||
    tags.amenity === 'bar' ||
    tags.amenity === 'pub' ||
    tags.shop === 'bakery'
  ) {
    return 'food'
  }

  if (
    tags.leisure === 'park' ||
    tags.leisure === 'garden' ||
    tags.leisure === 'nature_reserve' ||
    tags.boundary === 'national_park' ||
    tags.boundary === 'protected_area'
  ) {
    return 'park'
  }

  if (isHistoricLandmark(tags)) {
    return 'landmark'
  }

  if (
    tags.tourism === 'attraction' ||
    tags.amenity === 'cinema' ||
    tags.amenity === 'theatre' ||
    tags.amenity === 'arts_centre' ||
    tags.leisure === 'amusement_arcade'
  ) {
    return 'entertainment'
  }

  return null
}

function isUnavailablePlace(tags: Record<string, string>) {
  return [
    tags.disused,
    tags.abandoned,
    tags.demolished,
    tags.construction,
    tags.proposed,
  ].some((value) => value === 'yes')
}

function hasReliableIdentity(tags: Record<string, string>, category: PointCategory) {
  const hasIdentity = Boolean(
    tags['name:ru'] ||
      tags.name ||
      tags.official_name ||
      tags.short_name ||
      tags.brand ||
      tags.operator ||
      tags.wikipedia ||
      tags.wikidata,
  )

  if (!hasIdentity) {
    return false
  }

  if (category === 'landmark') {
    return isHistoricLandmark(tags)
  }

  return true
}

function isHistoricLandmark(tags: Record<string, string>) {
  const historicValue = tags.historic?.trim().toLowerCase()
  return Boolean(historicValue && historicLandmarkValues.has(historicValue))
}

function resolveTitle(tags: Record<string, string>, locale: SupportedLocale) {
  const localizedName = locale === 'ru' ? tags['name:ru'] : tags[`name:${locale}`]

  return (
    localizedName ||
    tags.name ||
    tags.official_name ||
    tags.short_name ||
    tags.brand ||
    tags.operator ||
    null
  )
}

function resolveAddress(tags: Record<string, string>) {
  const parts = [
    tags['addr:street'],
    tags['addr:housenumber'],
    tags['addr:suburb'],
    tags['addr:city'],
  ].filter(Boolean)

  return parts.length ? parts.join(', ') : undefined
}

function resolveTypeLabel(
  category: PointCategory,
  tags: Record<string, string>,
  locale: SupportedLocale,
) {
  const isRu = locale === 'ru'

  switch (category) {
    case 'museum':
      return tags.tourism === 'gallery'
        ? isRu
          ? 'Галерея'
          : 'Gallery'
        : isRu
          ? 'Музей'
          : 'Museum'
    case 'food':
      if (tags.shop === 'bakery') {
        return isRu ? 'Пекарня' : 'Bakery'
      }
      if (tags.amenity === 'cafe') {
        return isRu ? 'Кафе' : 'Cafe'
      }
      if (tags.amenity === 'bar' || tags.amenity === 'pub') {
        return isRu ? 'Бар' : 'Bar'
      }
      return isRu ? 'Ресторан' : 'Restaurant'
    case 'park':
      if (tags.leisure === 'garden') {
        return isRu ? 'Сад' : 'Garden'
      }
      if (tags.leisure === 'nature_reserve' || tags.boundary === 'protected_area') {
        return isRu ? 'Охраняемая зона' : 'Protected area'
      }
      return isRu ? 'Парк' : 'Park'
    case 'entertainment':
      if (tags.amenity === 'cinema') {
        return isRu ? 'Кинотеатр' : 'Cinema'
      }
      if (tags.amenity === 'theatre') {
        return isRu ? 'Театр' : 'Theatre'
      }
      return isRu ? 'Развлечение' : 'Attraction'
    case 'landmark':
      switch (tags.historic) {
        case 'monument':
          return isRu ? 'Памятник' : 'Monument'
        case 'memorial':
          return isRu ? 'Мемориал' : 'Memorial'
        case 'castle':
          return isRu ? 'Замок' : 'Castle'
        case 'ruins':
          return isRu ? 'Руины' : 'Ruins'
        case 'archaeological_site':
          return isRu ? 'Археологический объект' : 'Archaeological site'
        case 'fort':
          return isRu ? 'Крепость' : 'Fort'
        case 'city_gate':
          return isRu ? 'Исторические ворота' : 'Historic gate'
        case 'tower':
          return isRu ? 'Историческая башня' : 'Historic tower'
        case 'manor':
          return isRu ? 'Усадьба' : 'Manor'
        case 'wayside_cross':
          return isRu ? 'Исторический крест' : 'Wayside cross'
        case 'wayside_shrine':
          return isRu ? 'Историческая часовня' : 'Wayside shrine'
        default:
          return isRu ? 'Историческое место' : 'Historic place'
      }
    default:
      return isRu ? 'Место' : 'Place'
  }
}

function normalizeWikipediaTitle(reference?: string) {
  if (!reference?.trim()) {
    return undefined
  }

  return reference
}

function createCacheKey(
  category: PointCategory | 'all',
  center: GeoPoint,
  locale: SupportedLocale,
  radiusMeters: number,
) {
  return `${category}:${locale}:${radiusMeters}:${center.lat.toFixed(4)}:${center.lng.toFixed(4)}`
}

function normalizeError(error: unknown) {
  return error instanceof Error ? error : new Error('Unknown nearby places error')
}

function uniqueByCompositeKey(point: NearbyPoint, index: number, source: NearbyPoint[]) {
  const key = `${point.category}:${normalizeName(point.title)}:${point.coordinates.lat.toFixed(
    5,
  )}:${point.coordinates.lng.toFixed(5)}`

  return (
    source.findIndex((candidate) => {
      const candidateKey = `${candidate.category}:${normalizeName(
        candidate.title,
      )}:${candidate.coordinates.lat.toFixed(5)}:${candidate.coordinates.lng.toFixed(5)}`

      return candidateKey === key
    }) === index
  )
}

function normalizeName(value: string) {
  return value.trim().toLocaleLowerCase()
}
