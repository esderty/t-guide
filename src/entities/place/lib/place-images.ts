import type { GeoPoint, PointCategory } from '@/entities/excursion/model/types'

const staticMapBaseUrl = 'https://staticmap.openstreetmap.de/staticmap.php'

export function resolveNearbyPointImageUrl(
  tags: Record<string, string>,
  coordinates: GeoPoint,
  category: PointCategory,
) {
  const directImage = tags.image || tags['image:0']

  if (directImage && /^https?:\/\//i.test(directImage)) {
    return directImage
  }

  const commonsFile = tags.wikimedia_commons || tags['wikimedia_commons:en']

  if (commonsFile) {
    return buildCommonsImageUrl(commonsFile)
  }

  return buildStaticPlaceImageUrl(coordinates, category)
}

export function buildStaticPlaceImageUrl(
  coordinates: GeoPoint,
  category: PointCategory,
  zoom = 16,
) {
  const params = new URLSearchParams({
    center: `${coordinates.lat},${coordinates.lng}`,
    zoom: String(zoom),
    size: '1200x720',
    maptype: 'mapnik',
    markers: `${coordinates.lat},${coordinates.lng},${getStaticMarkerColor(category)}`,
  })

  return `${staticMapBaseUrl}?${params.toString()}`
}

export function isStaticPlaceImageUrl(value: string) {
  return value.startsWith(staticMapBaseUrl)
}

function buildCommonsImageUrl(fileName: string) {
  const normalizedFileName = fileName.startsWith('File:') ? fileName : `File:${fileName}`
  return `https://commons.wikimedia.org/wiki/Special:FilePath/${encodeURIComponent(normalizedFileName)}?width=1200`
}

function getStaticMarkerColor(category: PointCategory) {
  switch (category) {
    case 'museum':
      return 'blue'
    case 'food':
      return 'orange'
    case 'park':
      return 'green'
    case 'entertainment':
      return 'violet'
    case 'landmark':
      return 'red'
    default:
      return 'lightblue1'
  }
}
