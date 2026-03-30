import type {
  AudioStory,
  Excursion,
  ExcursionDifficulty,
  ExcursionTheme,
  GeoPoint,
  NearbyPoint,
  PointCategory,
  RouteStop,
  SupportedLocale,
} from '@/entities/excursion/model/types'
import { appMapConfig } from '@/shared/config/map'

interface DiscoveryFeed {
  excursions: Excursion[]
  nearbyPoints: NearbyPoint[]
}

interface PointTemplate {
  id: string
  northMeters: number
  eastMeters: number
  category: PointCategory
  rating: number
  expectedVisitMinutes: number
  imageUrl: string
  title: LocalizedText
  shortDescription: LocalizedText
  description: LocalizedText
  scheduleLabel: LocalizedText
}

interface RouteTemplate {
  id: number
  slug: string
  theme: ExcursionTheme
  routeColor: string
  difficulty: ExcursionDifficulty
  stopIds: string[]
  createdAt: string
  coverImageUrl: string
  title: LocalizedText
  tagline: LocalizedText
  description: LocalizedText
  audienceLabel: LocalizedText
}

type LocalizedText = Partial<Record<SupportedLocale, string>>

const pointTemplates: PointTemplate[] = [
  {
    id: 'city-museum',
    northMeters: 320,
    eastMeters: -140,
    category: 'museum',
    rating: 4.8,
    expectedVisitMinutes: 24,
    imageUrl: '/illustrations/landmark-card.svg',
    title: {
      ru: 'Городской музей',
      en: 'City Museum',
    },
    shortDescription: {
      ru: 'Короткая остановка с локальной историей и сильной первой точкой маршрута.',
      en: 'A compact stop with local history and a strong route opening.',
    },
    description: {
      ru: 'Эта точка хорошо подходит для старта аудиопрогулки: рядом обычно есть удобный подход, а вокруг можно быстро собрать контекст района.',
      en: 'This stop works well as a route opener: it is easy to approach and quickly gives the neighborhood some context.',
    },
    scheduleLabel: {
      ru: 'Открыто до 20:00',
      en: 'Open until 8 PM',
    },
  },
  {
    id: 'viewpoint-plaza',
    northMeters: 460,
    eastMeters: 180,
    category: 'landmark',
    rating: 4.7,
    expectedVisitMinutes: 18,
    imageUrl: '/illustrations/volga-route.svg',
    title: {
      ru: 'Смотровая площадь',
      en: 'Viewpoint Plaza',
    },
    shortDescription: {
      ru: 'Видовая точка для паузы, фото и ориентира по району.',
      en: 'A scenic point for a pause, photos, and neighborhood orientation.',
    },
    description: {
      ru: 'Отсюда удобно объяснять маршрут дальше: пользователь сразу видит направление прогулки и получает эмоциональную точку входа в историю места.',
      en: 'This spot helps frame the route ahead: the user immediately sees the direction of the walk and gets an emotional entry into the area.',
    },
    scheduleLabel: {
      ru: 'Доступно весь день',
      en: 'Open all day',
    },
  },
  {
    id: 'food-market',
    northMeters: 40,
    eastMeters: 320,
    category: 'food',
    rating: 4.9,
    expectedVisitMinutes: 20,
    imageUrl: '/illustrations/landmark-card.svg',
    title: {
      ru: 'Маркет местной кухни',
      en: 'Local Food Market',
    },
    shortDescription: {
      ru: 'Еда, быстрые остановки и удобная точка для гастромаршрута.',
      en: 'Food, quick stops, and a strong starting point for a flavor route.',
    },
    description: {
      ru: 'Здесь хорошо запускать короткие гастрономические прогулки: рядом обычно концентрируются вкусы района и понятный городской ритм.',
      en: 'This is a strong launch point for short food walks: neighborhood flavors and an easy urban rhythm usually meet here.',
    },
    scheduleLabel: {
      ru: 'Работает до 22:00',
      en: 'Open until 10 PM',
    },
  },
  {
    id: 'street-bistro',
    northMeters: -210,
    eastMeters: 260,
    category: 'food',
    rating: 4.6,
    expectedVisitMinutes: 16,
    imageUrl: '/illustrations/landmark-card.svg',
    title: {
      ru: 'Уличное бистро',
      en: 'Street Bistro',
    },
    shortDescription: {
      ru: 'Легкая остановка для кофе, десерта или быстрого перекуса по пути.',
      en: 'A light stop for coffee, dessert, or a quick bite on the way.',
    },
    description: {
      ru: 'Такие места делают маршрут живым: здесь удобно переключиться с навигации на атмосферу района и дать пользователю паузу.',
      en: 'Stops like this make the route feel alive: they turn navigation into neighborhood atmosphere and give the user a pause.',
    },
    scheduleLabel: {
      ru: 'Открыто до 23:00',
      en: 'Open until 11 PM',
    },
  },
  {
    id: 'central-park',
    northMeters: -360,
    eastMeters: -120,
    category: 'park',
    rating: 4.8,
    expectedVisitMinutes: 28,
    imageUrl: '/illustrations/volga-route.svg',
    title: {
      ru: 'Зеленый парк',
      en: 'Green Park',
    },
    shortDescription: {
      ru: 'Спокойный участок для прогулки, воздуха и мягкого темпа.',
      en: 'A calm segment for a slower pace, fresh air, and an easy walk.',
    },
    description: {
      ru: 'Парк добавляет маршруту дыхание и делает сценарий более гибким: здесь удобно задержаться или, наоборот, быстро пройти дальше.',
      en: 'The park gives the route breathing room and makes the scenario more flexible: it works both for a longer pause and a faster pass-through.',
    },
    scheduleLabel: {
      ru: 'Круглосуточно',
      en: 'Open 24/7',
    },
  },
  {
    id: 'botanical-yard',
    northMeters: -520,
    eastMeters: 210,
    category: 'park',
    rating: 4.7,
    expectedVisitMinutes: 22,
    imageUrl: '/illustrations/volga-route.svg',
    title: {
      ru: 'Ботанический двор',
      en: 'Botanical Courtyard',
    },
    shortDescription: {
      ru: 'Тихое место с зеленью и ощущением короткой перезагрузки.',
      en: 'A quieter green spot that feels like a short reset.',
    },
    description: {
      ru: 'Эта точка хорошо работает в природных и смешанных маршрутах, потому что меняет ритм и дает более камерный эпизод внутри города.',
      en: 'This stop works well in nature and mixed routes because it shifts the rhythm and adds a more intimate episode inside the city.',
    },
    scheduleLabel: {
      ru: 'Доступно до 21:00',
      en: 'Open until 9 PM',
    },
  },
  {
    id: 'art-club',
    northMeters: 120,
    eastMeters: -340,
    category: 'entertainment',
    rating: 4.5,
    expectedVisitMinutes: 26,
    imageUrl: '/illustrations/landmark-card.svg',
    title: {
      ru: 'Арт-клуб района',
      en: 'Neighborhood Art Club',
    },
    shortDescription: {
      ru: 'Точка для событий, выставок и более живого сценария прогулки.',
      en: 'A place for events, exhibitions, and a more energetic walk scenario.',
    },
    description: {
      ru: 'Такой stop хорошо раскрывает современную жизнь района: здесь удобно добавлять рассказы про локальные события, людей и вечерний ритм.',
      en: 'A stop like this reveals the modern life of the neighborhood: it is a natural place for stories about local events, people, and the evening rhythm.',
    },
    scheduleLabel: {
      ru: 'Сеансы до 21:30',
      en: 'Last sessions at 9:30 PM',
    },
  },
  {
    id: 'maker-gallery',
    northMeters: -80,
    eastMeters: -460,
    category: 'entertainment',
    rating: 4.6,
    expectedVisitMinutes: 19,
    imageUrl: '/illustrations/landmark-card.svg',
    title: {
      ru: 'Галерея идей',
      en: 'Idea Gallery',
    },
    shortDescription: {
      ru: 'Современная точка с выставочным настроением и хорошим визуальным темпом.',
      en: 'A contemporary stop with an exhibition mood and a strong visual pace.',
    },
    description: {
      ru: 'Эта точка хорошо связывает прогулочные и развлекательные маршруты: здесь удобно переключать пользователя с классической экскурсии на более живой городской опыт.',
      en: 'This stop bridges walking and entertainment routes well: it helps shift the user from a classic guided tour into a more vivid urban experience.',
    },
    scheduleLabel: {
      ru: 'Открыто до 19:00',
      en: 'Open until 7 PM',
    },
  },
  {
    id: 'dessert-lab',
    northMeters: 250,
    eastMeters: 430,
    category: 'food',
    rating: 4.7,
    expectedVisitMinutes: 14,
    imageUrl: '/illustrations/landmark-card.svg',
    title: {
      ru: 'Десертная студия',
      en: 'Dessert Studio',
    },
    shortDescription: {
      ru: 'Небольшая гастроточка, которую удобно встраивать в маршрут как награду в середине пути.',
      en: 'A small food stop that works well as a reward in the middle of the route.',
    },
    description: {
      ru: 'Маленькие гастрономические остановки добавляют маршруту запоминаемость и помогают держать более расслабленный ритм прогулки.',
      en: 'Small food stops add memorability to the route and help keep a more relaxed walking rhythm.',
    },
    scheduleLabel: {
      ru: 'Открыто до 20:30',
      en: 'Open until 8:30 PM',
    },
  },
  {
    id: 'history-square',
    northMeters: 510,
    eastMeters: -310,
    category: 'landmark',
    rating: 4.8,
    expectedVisitMinutes: 17,
    imageUrl: '/illustrations/volga-route.svg',
    title: {
      ru: 'Историческая площадь',
      en: 'Historic Square',
    },
    shortDescription: {
      ru: 'Узнаваемая городская сцена для короткой истории и смены темпа.',
      en: 'A recognizable urban stage for a short story and a rhythm shift.',
    },
    description: {
      ru: 'Площадь добавляет маршруту структуру: здесь удобно рассказывать о городе, менять направление и выводить пользователя к следующей сцене прогулки.',
      en: 'The square gives the route structure: it is a natural place to tell a city story, change direction, and lead the user into the next scene of the walk.',
    },
    scheduleLabel: {
      ru: 'Доступно весь день',
      en: 'Open all day',
    },
  },
]

const routeTemplates: RouteTemplate[] = [
  {
    id: 1,
    slug: 'first-steps-nearby',
    theme: 'walk',
    routeColor: '#0f766e',
    difficulty: 'easy',
    stopIds: ['viewpoint-plaza', 'city-museum', 'history-square', 'central-park'],
    createdAt: '2026-03-29T18:00:00.000Z',
    coverImageUrl: '/illustrations/volga-route.svg',
    title: {
      ru: 'Первые шаги рядом',
      en: 'First Steps Nearby',
    },
    tagline: {
      ru: 'Быстрый маршрут для первого знакомства с районом вокруг пользователя.',
      en: 'A compact route for getting oriented around the user.',
    },
    description: {
      ru: 'Этот маршрут собирает видовую точку, локальную историю и зеленую передышку в одном компактном сценарии.',
      en: 'This route combines a scenic point, local history, and a green pause in one compact scenario.',
    },
    audienceLabel: {
      ru: 'Подойдет для первого выхода и мягкого темпа',
      en: 'A good fit for a first outing and a soft pace',
    },
  },
  {
    id: 2,
    slug: 'local-flavor-loop',
    theme: 'food',
    routeColor: '#d97706',
    difficulty: 'easy',
    stopIds: ['food-market', 'street-bistro', 'dessert-lab', 'viewpoint-plaza'],
    createdAt: '2026-03-28T13:00:00.000Z',
    coverImageUrl: '/illustrations/landmark-card.svg',
    title: {
      ru: 'Вкусный маршрут по району',
      en: 'Local Flavor Loop',
    },
    tagline: {
      ru: 'Короткий сценарий с едой, паузами и финальной видовой точкой.',
      en: 'A short scenario with food stops, pauses, and a scenic finale.',
    },
    description: {
      ru: 'Маршрут ведет через локальные гастроточки и завершает прогулку местом, где удобно задержаться подольше.',
      en: 'The route moves through local food stops and ends at a place where it is easy to stay a little longer.',
    },
    audienceLabel: {
      ru: 'Для тех, кто хочет идти медленно и вкусно',
      en: 'For people who want a slower, tastier walk',
    },
  },
  {
    id: 3,
    slug: 'green-reset-route',
    theme: 'nature',
    routeColor: '#4f772d',
    difficulty: 'medium',
    stopIds: ['central-park', 'botanical-yard', 'viewpoint-plaza'],
    createdAt: '2026-03-27T11:30:00.000Z',
    coverImageUrl: '/illustrations/volga-route.svg',
    title: {
      ru: 'Зеленая передышка',
      en: 'Green Reset Route',
    },
    tagline: {
      ru: 'Маршрут для перезагрузки, спокойной прогулки и более тихих точек.',
      en: 'A route for resetting, slowing down, and finding quieter places.',
    },
    description: {
      ru: 'В этом маршруте больше воздуха, меньше суеты и удобные точки, чтобы дать пользователю отдых между историями.',
      en: 'This route has more breathing room, less rush, and better places to rest between stories.',
    },
    audienceLabel: {
      ru: 'Для прогулки без спешки и с длинными паузами',
      en: 'For a walk without rush and with longer pauses',
    },
  },
  {
    id: 4,
    slug: 'afterwork-fun-trail',
    theme: 'fun',
    routeColor: '#7c3aed',
    difficulty: 'medium',
    stopIds: ['maker-gallery', 'art-club', 'street-bistro', 'history-square'],
    createdAt: '2026-03-26T19:00:00.000Z',
    coverImageUrl: '/illustrations/landmark-card.svg',
    title: {
      ru: 'Маршрут впечатлений',
      en: 'Afterwork Fun Trail',
    },
    tagline: {
      ru: 'Живой городской сценарий с выставками, событиями и короткими остановками.',
      en: 'An energetic city scenario with exhibitions, events, and short breaks.',
    },
    description: {
      ru: 'Этот маршрут подходит для вечера или свободного окна в городе, когда хочется больше впечатлений и смены сцен.',
      en: 'This route works well for an evening or a free slot in the city when the user wants more scenes and more impressions.',
    },
    audienceLabel: {
      ru: 'Для активного темпа и более насыщенного вечера',
      en: 'For a more active pace and a denser evening plan',
    },
  },
  {
    id: 5,
    slug: 'neighborhood-mix',
    theme: 'mixed',
    routeColor: '#0f4c81',
    difficulty: 'hard',
    stopIds: ['city-museum', 'food-market', 'central-park', 'maker-gallery'],
    createdAt: '2026-03-25T09:00:00.000Z',
    coverImageUrl: '/illustrations/volga-route.svg',
    title: {
      ru: 'Смешанный маршрут рядом',
      en: 'Neighborhood Mix',
    },
    tagline: {
      ru: 'История, еда, зелень и современная городская точка в одном маршруте.',
      en: 'History, food, greenery, and a contemporary stop in one route.',
    },
    description: {
      ru: 'Маршрут создан для пользователя, который не хочет выбирать одну тему и предпочитает собрать район целиком.',
      en: 'This route is for users who do not want to commit to a single theme and would rather sample the neighborhood as a whole.',
    },
    audienceLabel: {
      ru: 'Для тех, кто хочет посмотреть район целиком',
      en: 'For people who want a fuller neighborhood overview',
    },
  },
]

export function createDiscoveryFeed(
  center: GeoPoint = appMapConfig.defaultCenter,
  locale: SupportedLocale = 'ru',
): DiscoveryFeed {
  const nearbyPoints = pointTemplates
    .map((template) => createNearbyPoint(template, center, locale))
    .sort((left, right) => left.distanceMeters - right.distanceMeters)

  const pointsById = new Map(nearbyPoints.map((point) => [point.id, point]))

  const excursions = routeTemplates.map((template) =>
    createExcursion(template, pointsById, center, locale),
  )

  return {
    excursions,
    nearbyPoints,
  }
}

function createNearbyPoint(
  template: PointTemplate,
  center: GeoPoint,
  locale: SupportedLocale,
): NearbyPoint {
  const coordinates = movePoint(center, template.northMeters, template.eastMeters)
  const distanceMeters = getDistanceMeters(center, coordinates)

  return {
    id: template.id,
    title: localizeText(template.title, locale),
    category: template.category,
    shortDescription: localizeText(template.shortDescription, locale),
    description: localizeText(template.description, locale),
    coordinates,
    imageUrl: template.imageUrl,
    expectedVisitMinutes: template.expectedVisitMinutes,
    rating: template.rating,
    scheduleLabel: localizeText(template.scheduleLabel, locale),
    distanceMeters,
  }
}

function createExcursion(
  template: RouteTemplate,
  pointsById: Map<string, NearbyPoint>,
  center: GeoPoint,
  locale: SupportedLocale,
): Excursion {
  const stops = template.stopIds
    .map((pointId, index) => {
      const point = pointsById.get(pointId)

      if (!point) {
        return null
      }

      return createRouteStop(point, index + 1, locale)
    })
    .filter((stop): stop is RouteStop => Boolean(stop))

  const routeDistanceKm = Math.max(
    0.7,
    getRouteDistanceKm([center, ...stops.map((stop) => stop.coordinates)]),
  )

  const visitMinutes = stops.reduce(
    (totalMinutes, stop) => totalMinutes + stop.expectedVisitMinutes,
    0,
  )
  const transitMinutes = Math.round(routeDistanceKm * 13)

  return {
    id: template.id,
    slug: template.slug,
    createdAt: template.createdAt,
    title: localizeText(template.title, locale),
    tagline: localizeText(template.tagline, locale),
    description: localizeText(template.description, locale),
    theme: template.theme,
    district: localizeText(
      {
        ru: 'Рядом с пользователем',
        en: 'Near the user',
      },
      locale,
    ),
    durationMinutes: visitMinutes + transitMinutes,
    distanceKm: routeDistanceKm,
    startLabel: stops[0]?.title ?? localizeText({ ru: 'Старт', en: 'Start' }, locale),
    finishLabel:
      stops.at(-1)?.title ?? localizeText({ ru: 'Финиш', en: 'Finish' }, locale),
    coverImageUrl: template.coverImageUrl,
    routeColor: template.routeColor,
    difficulty: template.difficulty,
    audienceLabel: localizeText(template.audienceLabel, locale),
    stops,
  }
}

function createRouteStop(
  point: NearbyPoint,
  order: number,
  locale: SupportedLocale,
): RouteStop {
  return {
    id: `${point.id}-stop`,
    order,
    title: point.title,
    category: point.category,
    shortDescription: point.shortDescription,
    description: point.description,
    coordinates: point.coordinates,
    imageUrl: point.imageUrl,
    expectedVisitMinutes: point.expectedVisitMinutes,
    rating: point.rating,
    scheduleLabel: point.scheduleLabel,
    audio: createAudioStory(point, locale),
  }
}

function createAudioStory(
  point: NearbyPoint,
  locale: SupportedLocale,
): AudioStory {
  return {
    id: `${point.id}-audio`,
    url: null,
    durationSeconds: 95,
    language: locale,
    transcriptPreview: localizeText(
      {
        ru: `Короткий рассказ о точке «${point.title}», ее атмосфере и том, зачем здесь задержаться на несколько минут.`,
        en: `A short story about ${point.title}, its atmosphere, and why this stop is worth a few extra minutes.`,
      },
      locale,
    ),
  }
}

function localizeText(value: LocalizedText, locale: SupportedLocale): string {
  return value[locale] ?? value.en ?? value.ru ?? ''
}

function movePoint(center: GeoPoint, northMeters: number, eastMeters: number): GeoPoint {
  const latDelta = northMeters / 111320
  const lngDelta =
    eastMeters / (111320 * Math.cos((center.lat * Math.PI) / 180 || 1))

  return {
    lat: center.lat + latDelta,
    lng: center.lng + lngDelta,
  }
}

function getRouteDistanceKm(points: GeoPoint[]): number {
  let distanceMeters = 0

  for (let index = 0; index < points.length - 1; index += 1) {
    distanceMeters += getDistanceMeters(points[index], points[index + 1])
  }

  return Number((distanceMeters / 1000).toFixed(1))
}

function getDistanceMeters(from: GeoPoint, to: GeoPoint): number {
  const earthRadius = 6371000
  const fromLat = degreesToRadians(from.lat)
  const toLat = degreesToRadians(to.lat)
  const deltaLat = degreesToRadians(to.lat - from.lat)
  const deltaLng = degreesToRadians(to.lng - from.lng)

  const haversine =
    Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
    Math.cos(fromLat) *
      Math.cos(toLat) *
      Math.sin(deltaLng / 2) *
      Math.sin(deltaLng / 2)

  return 2 * earthRadius * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine))
}

function degreesToRadians(value: number): number {
  return (value * Math.PI) / 180
}
