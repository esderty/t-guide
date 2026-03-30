import type {
  GeoPoint,
  NearbyPoint,
  PointCategory,
  SupportedLocale,
} from '@/entities/excursion/model/types'
import { buildStaticPlaceImageUrl } from '@/entities/place/lib/place-images'

interface FallbackTemplate {
  bearingDegrees: number
  category: PointCategory
  distanceMeters: number
  expectedVisitMinutes: number
  id: string
  scheduleLabel: Partial<Record<SupportedLocale, string>>
  shortDescription: Partial<Record<SupportedLocale, string>>
  title: Partial<Record<SupportedLocale, string>>
}

const fallbackTemplates: FallbackTemplate[] = [
  {
    id: 'museum-1',
    category: 'museum',
    distanceMeters: 420,
    bearingDegrees: 18,
    expectedVisitMinutes: 28,
    title: { ru: 'Музей района', en: 'District Museum' },
    shortDescription: {
      ru: 'Камерная точка с историей квартала.',
      en: 'A compact place with local district history.',
    },
    scheduleLabel: { ru: 'Открыто до 20:00', en: 'Open until 8 PM' },
  },
  {
    id: 'museum-2',
    category: 'museum',
    distanceMeters: 980,
    bearingDegrees: 314,
    expectedVisitMinutes: 24,
    title: { ru: 'Городская галерея', en: 'City Gallery' },
    shortDescription: {
      ru: 'Выставочное пространство рядом с маршрутом.',
      en: 'An exhibition space close to the route.',
    },
    scheduleLabel: { ru: 'Открыто до 19:00', en: 'Open until 7 PM' },
  },
  {
    id: 'museum-3',
    category: 'museum',
    distanceMeters: 2350,
    bearingDegrees: 62,
    expectedVisitMinutes: 32,
    title: { ru: 'Музей современной сцены', en: 'Contemporary Museum' },
    shortDescription: {
      ru: 'Современная точка для длинной остановки.',
      en: 'A contemporary stop for a longer visit.',
    },
    scheduleLabel: { ru: 'Открыто до 21:00', en: 'Open until 9 PM' },
  },
  {
    id: 'museum-4',
    category: 'museum',
    distanceMeters: 6120,
    bearingDegrees: 118,
    expectedVisitMinutes: 36,
    title: { ru: 'Исторический музей', en: 'History Museum' },
    shortDescription: {
      ru: 'Большая музейная точка дальше по району.',
      en: 'A larger museum spot farther out in the area.',
    },
    scheduleLabel: { ru: 'Открыто до 18:30', en: 'Open until 6:30 PM' },
  },
  {
    id: 'entertainment-1',
    category: 'entertainment',
    distanceMeters: 360,
    bearingDegrees: 104,
    expectedVisitMinutes: 18,
    title: { ru: 'Кинотеатр квартала', en: 'Neighborhood Cinema' },
    shortDescription: {
      ru: 'Легкая развлекательная остановка рядом.',
      en: 'A light entertainment stop nearby.',
    },
    scheduleLabel: { ru: 'Сеансы до 22:00', en: 'Shows until 10 PM' },
  },
  {
    id: 'entertainment-2',
    category: 'entertainment',
    distanceMeters: 1120,
    bearingDegrees: 162,
    expectedVisitMinutes: 20,
    title: { ru: 'Арт-пространство', en: 'Art Space' },
    shortDescription: {
      ru: 'Точка для событий и локальных выставок.',
      en: 'A place for events and local exhibitions.',
    },
    scheduleLabel: { ru: 'Открыто до 21:30', en: 'Open until 9:30 PM' },
  },
  {
    id: 'entertainment-3',
    category: 'entertainment',
    distanceMeters: 2840,
    bearingDegrees: 248,
    expectedVisitMinutes: 26,
    title: { ru: 'Театральная сцена', en: 'Theatre Stage' },
    shortDescription: {
      ru: 'Точка для вечернего маршрута.',
      en: 'A stop that fits well into an evening route.',
    },
    scheduleLabel: { ru: 'Показы до 22:30', en: 'Shows until 10:30 PM' },
  },
  {
    id: 'entertainment-4',
    category: 'entertainment',
    distanceMeters: 7320,
    bearingDegrees: 296,
    expectedVisitMinutes: 22,
    title: { ru: 'Площадка впечатлений', en: 'Experience Hub' },
    shortDescription: {
      ru: 'Активная развлекательная точка подальше.',
      en: 'A more active entertainment spot a bit farther out.',
    },
    scheduleLabel: { ru: 'Открыто до 23:00', en: 'Open until 11 PM' },
  },
  {
    id: 'landmark-1',
    category: 'landmark',
    distanceMeters: 280,
    bearingDegrees: 330,
    expectedVisitMinutes: 14,
    title: { ru: 'Историческая площадь', en: 'Historic Square' },
    shortDescription: {
      ru: 'Быстрая историческая точка в центре радиуса.',
      en: 'A quick historic stop close to the center.',
    },
    scheduleLabel: { ru: 'Доступно весь день', en: 'Open all day' },
  },
  {
    id: 'landmark-2',
    category: 'landmark',
    distanceMeters: 840,
    bearingDegrees: 212,
    expectedVisitMinutes: 16,
    title: { ru: 'Памятное место', en: 'Memorial Spot' },
    shortDescription: {
      ru: 'Небольшая точка для короткой истории.',
      en: 'A small stop for a short historical story.',
    },
    scheduleLabel: { ru: 'Доступно весь день', en: 'Open all day' },
  },
  {
    id: 'landmark-3',
    category: 'landmark',
    distanceMeters: 3480,
    bearingDegrees: 82,
    expectedVisitMinutes: 20,
    title: { ru: 'Старая ратуша', en: 'Old Town Hall' },
    shortDescription: {
      ru: 'Классическая точка для исторического маршрута.',
      en: 'A classic stop for a history-focused route.',
    },
    scheduleLabel: { ru: 'Доступно днем', en: 'Open during the day' },
  },
  {
    id: 'landmark-4',
    category: 'landmark',
    distanceMeters: 9240,
    bearingDegrees: 146,
    expectedVisitMinutes: 24,
    title: { ru: 'Фортовая сцена', en: 'Fort Landmark' },
    shortDescription: {
      ru: 'Дальняя историческая точка для длинного круга.',
      en: 'A farther historic stop for a longer loop.',
    },
    scheduleLabel: { ru: 'Доступно весь день', en: 'Open all day' },
  },
  {
    id: 'food-1',
    category: 'food',
    distanceMeters: 310,
    bearingDegrees: 76,
    expectedVisitMinutes: 16,
    title: { ru: 'Кафе рядом', en: 'Nearby Cafe' },
    shortDescription: {
      ru: 'Быстрая гастроточка у пользователя.',
      en: 'A quick food stop very close to the user.',
    },
    scheduleLabel: { ru: 'Открыто до 22:00', en: 'Open until 10 PM' },
  },
  {
    id: 'food-2',
    category: 'food',
    distanceMeters: 930,
    bearingDegrees: 186,
    expectedVisitMinutes: 20,
    title: { ru: 'Локальный ресторан', en: 'Local Restaurant' },
    shortDescription: {
      ru: 'Полноценная гастрономическая остановка.',
      en: 'A full dining stop inside the short radius.',
    },
    scheduleLabel: { ru: 'Открыто до 23:00', en: 'Open until 11 PM' },
  },
  {
    id: 'food-3',
    category: 'food',
    distanceMeters: 2680,
    bearingDegrees: 24,
    expectedVisitMinutes: 24,
    title: { ru: 'Фуд-холл', en: 'Food Hall' },
    shortDescription: {
      ru: 'Большая точка с несколькими форматами еды.',
      en: 'A larger food stop with multiple options.',
    },
    scheduleLabel: { ru: 'Открыто до 22:30', en: 'Open until 10:30 PM' },
  },
  {
    id: 'food-4',
    category: 'food',
    distanceMeters: 5140,
    bearingDegrees: 284,
    expectedVisitMinutes: 18,
    title: { ru: 'Пекарня района', en: 'District Bakery' },
    shortDescription: {
      ru: 'Гастрономическая остановка на дальнем круге.',
      en: 'A food stop on the wider radius.',
    },
    scheduleLabel: { ru: 'Открыто до 20:00', en: 'Open until 8 PM' },
  },
  {
    id: 'park-1',
    category: 'park',
    distanceMeters: 470,
    bearingDegrees: 136,
    expectedVisitMinutes: 22,
    title: { ru: 'Зеленый парк', en: 'Green Park' },
    shortDescription: {
      ru: 'Ближайшая природная точка для спокойной паузы.',
      en: 'The closest nature stop for a calm pause.',
    },
    scheduleLabel: { ru: 'Круглосуточно', en: 'Open 24/7' },
  },
  {
    id: 'park-2',
    category: 'park',
    distanceMeters: 1180,
    bearingDegrees: 278,
    expectedVisitMinutes: 26,
    title: { ru: 'Сад у воды', en: 'Waterside Garden' },
    shortDescription: {
      ru: 'Тихая точка у воды на среднем радиусе.',
      en: 'A quiet waterside spot on the medium radius.',
    },
    scheduleLabel: { ru: 'Открыто до 21:00', en: 'Open until 9 PM' },
  },
  {
    id: 'park-3',
    category: 'park',
    distanceMeters: 4310,
    bearingDegrees: 346,
    expectedVisitMinutes: 30,
    title: { ru: 'Лесной парк', en: 'Forest Park' },
    shortDescription: {
      ru: 'Большая природная зона в широком радиусе.',
      en: 'A larger natural zone on the wider radius.',
    },
    scheduleLabel: { ru: 'Круглосуточно', en: 'Open 24/7' },
  },
  {
    id: 'park-4',
    category: 'park',
    distanceMeters: 9820,
    bearingDegrees: 52,
    expectedVisitMinutes: 34,
    title: { ru: 'Заповедная зона', en: 'Protected Area' },
    shortDescription: {
      ru: 'Самая дальняя природная точка на 10 км.',
      en: 'The farthest nature stop on the 10 km radius.',
    },
    scheduleLabel: { ru: 'Доступно весь день', en: 'Open all day' },
  },
]

export function buildFallbackNearbyPlaces(
  center: GeoPoint,
  locale: SupportedLocale,
): NearbyPoint[] {
  return fallbackTemplates
    .map((template) => {
      const coordinates = movePointByBearing(
        center,
        template.distanceMeters,
        template.bearingDegrees,
      )

      return {
        id: template.id,
        title: template.title[locale] ?? template.title.en ?? template.title.ru ?? '',
        category: template.category,
        shortDescription:
          template.shortDescription[locale] ??
          template.shortDescription.en ??
          template.shortDescription.ru ??
          '',
        description:
          template.shortDescription[locale] ??
          template.shortDescription.en ??
          template.shortDescription.ru ??
          '',
        coordinates,
        imageUrl: buildStaticPlaceImageUrl(coordinates, template.category),
        expectedVisitMinutes: template.expectedVisitMinutes,
        rating: 4.7,
        scheduleLabel:
          template.scheduleLabel[locale] ??
          template.scheduleLabel.en ??
          template.scheduleLabel.ru ??
          '',
        distanceMeters: template.distanceMeters,
        source: 'mock' as const,
      }
    })
    .sort((left, right) => left.distanceMeters - right.distanceMeters)
}

function movePointByBearing(
  center: GeoPoint,
  distanceMeters: number,
  bearingDegrees: number,
): GeoPoint {
  const earthRadius = 6371000
  const bearing = degreesToRadians(bearingDegrees)
  const angularDistance = distanceMeters / earthRadius
  const lat1 = degreesToRadians(center.lat)
  const lng1 = degreesToRadians(center.lng)

  const lat2 = Math.asin(
    Math.sin(lat1) * Math.cos(angularDistance) +
      Math.cos(lat1) * Math.sin(angularDistance) * Math.cos(bearing),
  )

  const lng2 =
    lng1 +
    Math.atan2(
      Math.sin(bearing) * Math.sin(angularDistance) * Math.cos(lat1),
      Math.cos(angularDistance) - Math.sin(lat1) * Math.sin(lat2),
    )

  return {
    lat: radiansToDegrees(lat2),
    lng: radiansToDegrees(lng2),
  }
}

function degreesToRadians(value: number) {
  return (value * Math.PI) / 180
}

function radiansToDegrees(value: number) {
  return (value * 180) / Math.PI
}
