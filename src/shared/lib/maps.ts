import type { GeoPoint } from '@/entities/excursion/model/types'

export function buildGoogleMapsUrl(destination: GeoPoint, userPosition?: GeoPoint | null) {
  const destinationPart = `${destination.lat},${destination.lng}`

  if (!userPosition) {
    return `https://www.google.com/maps/search/?api=1&query=${destinationPart}`
  }

  return [
    'https://www.google.com/maps/dir/?api=1',
    `origin=${userPosition.lat},${userPosition.lng}`,
    `destination=${destinationPart}`,
    'travelmode=walking',
  ].join('&')
}
