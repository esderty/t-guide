import type {
  Excursion,
  ExcursionDifficulty,
  ExcursionTheme,
} from '@/entities/excursion/model/types'

export interface ExcursionFilters {
  query: string
  theme: ExcursionTheme | 'all'
  district: string | 'all'
  difficulty: ExcursionDifficulty | 'all'
  maxDistance: number | null
  maxDuration: number | null
  sortBy: 'newest' | 'duration-asc' | 'distance-asc' | 'stops-desc'
}

export const defaultExcursionFilters: ExcursionFilters = {
  query: '',
  theme: 'all',
  district: 'all',
  difficulty: 'all',
  maxDistance: null,
  maxDuration: null,
  sortBy: 'newest',
}

export function getLatestExcursions(
  excursions: Excursion[],
  limit: number,
): Excursion[] {
  return [...excursions]
    .sort(
      (left, right) =>
        new Date(right.createdAt).getTime() - new Date(left.createdAt).getTime(),
    )
    .slice(0, limit)
}

export function getExcursionStats(excursions: Excursion[]) {
  const totalDistance = excursions.reduce(
    (distance, excursion) => distance + excursion.distanceKm,
    0,
  )

  const shortestExcursion = [...excursions].sort(
    (left, right) => left.durationMinutes - right.durationMinutes,
  )[0]

  const longestExcursion = [...excursions].sort(
    (left, right) => right.durationMinutes - left.durationMinutes,
  )[0]

  return {
    totalCount: excursions.length,
    totalDistance,
    shortestExcursion,
    longestExcursion,
  }
}

export function filterExcursions(
  excursions: Excursion[],
  filters: ExcursionFilters,
): Excursion[] {
  const normalizedQuery = normalizeSearchToken(filters.query)

  const filtered = excursions.filter((excursion) => {
    const matchesQuery =
      !normalizedQuery ||
      excursion.title.toLowerCase().includes(normalizedQuery) ||
      excursion.tagline.toLowerCase().includes(normalizedQuery) ||
      excursion.stops.some(
        (stop) =>
          stop.title.toLowerCase().includes(normalizedQuery) ||
          stop.shortDescription.toLowerCase().includes(normalizedQuery),
      )

    const matchesTheme =
      filters.theme === 'all' || excursion.theme === filters.theme

    const matchesDistrict =
      filters.district === 'all' || excursion.district === filters.district

    const matchesDifficulty =
      filters.difficulty === 'all' || excursion.difficulty === filters.difficulty

    const matchesDistance =
      filters.maxDistance === null || excursion.distanceKm <= filters.maxDistance

    const matchesDuration =
      filters.maxDuration === null ||
      excursion.durationMinutes <= filters.maxDuration

    return (
      matchesQuery &&
      matchesTheme &&
      matchesDistrict &&
      matchesDifficulty &&
      matchesDistance &&
      matchesDuration
    )
  })

  return filtered.sort((left, right) => {
    switch (filters.sortBy) {
      case 'duration-asc':
        return left.durationMinutes - right.durationMinutes
      case 'distance-asc':
        return left.distanceKm - right.distanceKm
      case 'stops-desc':
        return right.stops.length - left.stops.length
      case 'newest':
      default:
        return (
          new Date(right.createdAt).getTime() - new Date(left.createdAt).getTime()
        )
    }
  })
}

export function paginateItems<T>(
  items: T[],
  currentPage: number,
  pageSize: number,
): T[] {
  const startIndex = (currentPage - 1) * pageSize
  return items.slice(startIndex, startIndex + pageSize)
}

export function hasActiveExcursionFilters(filters: ExcursionFilters): boolean {
  return (
    filters.query.trim().length > 0 ||
    filters.theme !== defaultExcursionFilters.theme ||
    filters.district !== defaultExcursionFilters.district ||
    filters.difficulty !== defaultExcursionFilters.difficulty ||
    filters.maxDistance !== defaultExcursionFilters.maxDistance ||
    filters.maxDuration !== defaultExcursionFilters.maxDuration ||
    filters.sortBy !== defaultExcursionFilters.sortBy
  )
}

export function getExcursionQuerySuggestion(
  excursions: Excursion[],
  query: string,
): string | null {
  const normalizedQuery = normalizeSearchToken(query)

  if (normalizedQuery.length < 2) {
    return null
  }

  const suggestion = collectSuggestionTokens(excursions).find((token) => {
    const normalizedToken = normalizeSearchToken(token)
    return (
      normalizedToken.startsWith(normalizedQuery) &&
      normalizedToken !== normalizedQuery
    )
  })

  return suggestion ?? null
}

function collectSuggestionTokens(excursions: Excursion[]): string[] {
  const tokens = new Set<string>()

  excursions.forEach((excursion) => {
    tokenizeText(excursion.title).forEach((token) => tokens.add(token))
    tokenizeText(excursion.tagline).forEach((token) => tokens.add(token))
    excursion.stops.forEach((stop) => {
      tokenizeText(stop.title).forEach((token) => tokens.add(token))
    })
  })

  return [...tokens].sort((left, right) => left.localeCompare(right, 'ru'))
}

function tokenizeText(text: string): string[] {
  return text
    .split(/[\s,.:;!?()"-]+/)
    .map((token) => token.trim())
    .filter((token) => token.length >= 3)
}

function normalizeSearchToken(value: string): string {
  return value.trim().toLocaleLowerCase('ru')
}
