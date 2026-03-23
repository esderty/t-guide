import type {
  ExcursionDifficulty,
  ExcursionTheme,
  GeoPoint,
} from '@/entities/excursion/model/types'

export function formatDuration(totalMinutes: number): string {
  if (totalMinutes < 60) {
    return `${totalMinutes} мин`
  }

  const hours = Math.floor(totalMinutes / 60)
  const minutes = totalMinutes % 60

  if (!minutes) {
    return `${hours} ч`
  }

  return `${hours} ч ${minutes} мин`
}

export function formatDistance(distanceKm: number): string {
  return `${distanceKm.toFixed(1).replace('.', ',')} км`
}

export function formatStopCount(count: number): string {
  const lastDigit = count % 10
  const lastTwoDigits = count % 100

  if (lastDigit === 1 && lastTwoDigits !== 11) {
    return `${count} точка`
  }

  if (
    lastDigit >= 2 &&
    lastDigit <= 4 &&
    !(lastTwoDigits >= 12 && lastTwoDigits <= 14)
  ) {
    return `${count} точки`
  }

  return `${count} точек`
}

export function formatDifficulty(difficulty: ExcursionDifficulty): string {
  switch (difficulty) {
    case 'easy':
      return 'Легко'
    case 'medium':
      return 'Средне'
    case 'hard':
      return 'Сложно'
    default:
      return 'Маршрут'
  }
}

export function formatTheme(theme: ExcursionTheme): string {
  switch (theme) {
    case 'history':
      return 'История'
    case 'architecture':
      return 'Архитектура'
    case 'waterfront':
      return 'Набережные'
    case 'culture':
      return 'Культура'
    case 'panoramas':
      return 'Панорамы'
    case 'legends':
      return 'Легенды'
    case 'family':
      return 'Семейные'
    case 'modernism':
      return 'Модерн'
    default:
      return 'Маршрут'
  }
}

export function formatCoordinates(point: GeoPoint): string {
  return `${point.lat.toFixed(4)}, ${point.lng.toFixed(4)}`
}
