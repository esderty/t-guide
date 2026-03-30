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

interface CreateDiscoveryRoutesParams {
  activePointCategory: PointCategory | 'all'
  center: GeoPoint
  locale: SupportedLocale
  nearbyPoints: NearbyPoint[]
  radiusMeters: number
}

interface GeneratedRouteDraft {
  audienceLabel: string
  coverImageUrl: string
  createdAt: string
  description: string
  difficulty: ExcursionDifficulty
  district: string
  routeColor: string
  slug: string
  stopIds: string[]
  tagline: string
  theme: ExcursionTheme
  title: string
}

interface MixedBlueprint {
  categories: PointCategory[]
  key: string
  theme: ExcursionTheme
}

const themeColors: Record<ExcursionTheme, string> = {
  walk: '#0f766e',
  food: '#d97706',
  nature: '#4f772d',
  fun: '#7c3aed',
  mixed: '#0f4c81',
}

const themePriority: Record<ExcursionTheme, number> = {
  mixed: 0,
  walk: 1,
  food: 2,
  nature: 3,
  fun: 4,
}

const categoryThemeMap: Record<PointCategory, ExcursionTheme> = {
  museum: 'walk',
  landmark: 'walk',
  food: 'food',
  park: 'nature',
  entertainment: 'fun',
}

const mixedBlueprints: MixedBlueprint[] = [
  { key: 'city-story', categories: ['landmark', 'museum', 'park', 'food'], theme: 'mixed' },
  { key: 'after-lunch', categories: ['food', 'landmark', 'park', 'museum'], theme: 'mixed' },
  { key: 'weekend', categories: ['museum', 'food', 'entertainment', 'park'], theme: 'mixed' },
  { key: 'evening', categories: ['entertainment', 'food', 'landmark', 'park'], theme: 'mixed' },
  { key: 'slow-day', categories: ['park', 'landmark', 'food', 'museum'], theme: 'mixed' },
  { key: 'city-highlight', categories: ['landmark', 'park', 'food', 'entertainment'], theme: 'mixed' },
]

const routeCreatedAtBase = Date.parse('2026-03-30T08:00:00.000Z')

export function createDiscoveryRoutes({
  activePointCategory,
  center,
  locale,
  nearbyPoints,
  radiusMeters,
}: CreateDiscoveryRoutesParams): Excursion[] {
  const sortedPoints = [...nearbyPoints].sort(
    (left, right) => left.distanceMeters - right.distanceMeters,
  )

  if (sortedPoints.length < 2) {
    return []
  }

  const drafts: GeneratedRouteDraft[] = []
  const seenRoutes = new Set<string>()
  const pointsByCategory = groupPointsByCategory(sortedPoints)
  const categoryOrder =
    activePointCategory === 'all'
      ? getPresentCategories(pointsByCategory)
      : [activePointCategory]

  if (activePointCategory === 'all') {
    for (const blueprint of mixedBlueprints) {
      const stops = pickMixedRouteStops(pointsByCategory, blueprint.categories)

      if (stops.length >= 3) {
        pushRouteDraft(
          drafts,
          seenRoutes,
          buildMixedRouteDraft(blueprint, stops, locale, radiusMeters),
          stops,
        )
      }
    }
  }

  categoryOrder.forEach((category) => {
    const categoryPoints = pointsByCategory[category] ?? []
    const categoryDrafts = buildCategoryRouteDrafts(
      category,
      categoryPoints,
      locale,
      radiusMeters,
    )

    categoryDrafts.forEach(({ draft, stops }) => {
      pushRouteDraft(drafts, seenRoutes, draft, stops)
    })
  })

  if (!drafts.length) {
    const fallbackStops = sortedPoints.slice(0, Math.min(sortedPoints.length, 3))

    if (fallbackStops.length >= 2) {
      pushRouteDraft(
        drafts,
        seenRoutes,
        buildFallbackRouteDraft(fallbackStops, locale, radiusMeters),
        fallbackStops,
      )
    }
  }

  return drafts
    .sort(sortDiscoveryDrafts)
    .map((draft, index) =>
      createExcursionFromDraft({
        center,
        draft,
        index,
        locale,
        stops: draft.stopIds
          .map((stopId) => sortedPoints.find((point) => point.id === stopId))
          .filter((point): point is NearbyPoint => Boolean(point)),
      }),
    )
}

function buildCategoryRouteDrafts(
  category: PointCategory,
  categoryPoints: NearbyPoint[],
  locale: SupportedLocale,
  radiusMeters: number,
) {
  if (categoryPoints.length < 2) {
    return []
  }

  return [2, 3, 4]
    .filter((size, index, source) => size <= categoryPoints.length && source.indexOf(size) === index)
    .map((size, variantIndex) => {
      const stops = categoryPoints.slice(0, size)

      return {
        draft: buildCategoryRouteDraft(category, stops, locale, radiusMeters, variantIndex),
        stops,
      }
    })
}

function buildCategoryRouteDraft(
  category: PointCategory,
  stops: NearbyPoint[],
  locale: SupportedLocale,
  radiusMeters: number,
  variantIndex: number,
): GeneratedRouteDraft {
  const theme = categoryThemeMap[category]
  const variantLabel = getVariantLabel(stops.length, variantIndex, locale)
  const categoryLabel = getCategoryRouteLabel(category, locale)
  const radiusLabel = formatRadiusLabel(radiusMeters, locale)
  const firstStop = stops[0]
  const lastStop = stops.at(-1) ?? firstStop

  return {
    audienceLabel: buildAudienceLabel(theme, stops.length, locale),
    coverImageUrl: pickRouteCoverImage(stops),
    createdAt: new Date(routeCreatedAtBase - variantIndex * 60_000).toISOString(),
    description:
      locale === 'ru'
        ? buildCategoryDescription(category, stops, radiusLabel)
        : `A curated ${categoryLabel.toLowerCase()} route inside ${radiusLabel}.`,
    difficulty: getDifficultyByStops(stops),
    district: `${categoryLabel} • ${radiusLabel}`,
    routeColor: themeColors[theme],
    slug: createRouteSlug(theme, stops, variantLabel),
    stopIds: stops.map((stop) => stop.id),
    tagline: buildCategoryTagline(category, firstStop.title, lastStop.title, locale),
    theme,
    title: buildCategoryTitle(category, stops.length, variantIndex, locale),
  }
}

function buildMixedRouteDraft(
  blueprint: MixedBlueprint,
  stops: NearbyPoint[],
  locale: SupportedLocale,
  radiusMeters: number,
): GeneratedRouteDraft {
  const radiusLabel = formatRadiusLabel(radiusMeters, locale)
  const firstStop = stops[0]
  const lastStop = stops.at(-1) ?? firstStop

  return {
    audienceLabel: buildAudienceLabel('mixed', stops.length, locale),
    coverImageUrl: pickRouteCoverImage(stops),
    createdAt: new Date(routeCreatedAtBase - 10 * 60_000 - stops.length * 1_000).toISOString(),
    description:
      locale === 'ru'
        ? buildMixedDescription(blueprint.key, radiusLabel)
        : `A balanced route for ${radiusLabel} that mixes city stories, pauses, food, and vivid stops.`,
    difficulty: getDifficultyByStops(stops),
    district: locale === 'ru' ? `Смешанный маршрут • ${radiusLabel}` : `Mixed route • ${radiusLabel}`,
    routeColor: themeColors[blueprint.theme],
    slug: createRouteSlug(blueprint.theme, stops, blueprint.key),
    stopIds: stops.map((stop) => stop.id),
    tagline: buildMixedTagline(blueprint.key, firstStop.title, lastStop.title, locale),
    theme: blueprint.theme,
    title: getMixedRouteTitle(blueprint.key, locale),
  }
}

function buildFallbackRouteDraft(
  stops: NearbyPoint[],
  locale: SupportedLocale,
  radiusMeters: number,
): GeneratedRouteDraft {
  const radiusLabel = formatRadiusLabel(radiusMeters, locale)

  return {
    audienceLabel: buildAudienceLabel('mixed', stops.length, locale),
    coverImageUrl: pickRouteCoverImage(stops),
    createdAt: new Date(routeCreatedAtBase - 99 * 60_000).toISOString(),
    description:
      locale === 'ru'
        ? `Ближайший маршрут, собранный из доступных точек в радиусе ${radiusLabel}.`
        : `The closest route built from available stops within ${radiusLabel}.`,
    difficulty: getDifficultyByStops(stops),
    district: locale === 'ru' ? `Ближайшие точки • ${radiusLabel}` : `Nearby places • ${radiusLabel}`,
    routeColor: themeColors.mixed,
    slug: createRouteSlug('mixed', stops, 'fallback'),
    stopIds: stops.map((stop) => stop.id),
    tagline: `${stops[0].title} > ${stops.at(-1)?.title ?? stops[0].title}`,
    theme: 'mixed',
    title: locale === 'ru' ? 'Быстрый маршрут рядом' : 'Quick route nearby',
  }
}

function createExcursionFromDraft({
  center,
  draft,
  index,
  locale,
  stops,
}: {
  center: GeoPoint
  draft: GeneratedRouteDraft
  index: number
  locale: SupportedLocale
  stops: NearbyPoint[]
}): Excursion {
  const routeStops = stops.map((stop, stopIndex) => createRouteStop(stop, stopIndex + 1, locale))
  const routeDistanceKm = Math.max(
    0.4,
    getRouteDistanceKm([center, ...routeStops.map((stop) => stop.coordinates)]),
  )
  const visitMinutes = routeStops.reduce(
    (totalMinutes, stop) => totalMinutes + stop.expectedVisitMinutes,
    0,
  )
  const transitMinutes = Math.max(8, Math.round(routeDistanceKm * 12))

  return {
    id: index + 1,
    slug: draft.slug,
    createdAt: draft.createdAt,
    title: draft.title,
    tagline: draft.tagline,
    description: draft.description,
    theme: draft.theme,
    district: draft.district,
    durationMinutes: visitMinutes + transitMinutes,
    distanceKm: routeDistanceKm,
    startLabel: routeStops[0]?.title ?? (locale === 'ru' ? 'Старт' : 'Start'),
    finishLabel: routeStops.at(-1)?.title ?? (locale === 'ru' ? 'Финиш' : 'Finish'),
    coverImageUrl: draft.coverImageUrl,
    routeColor: draft.routeColor,
    difficulty: draft.difficulty,
    audienceLabel: draft.audienceLabel,
    stops: routeStops,
  }
}

function createRouteStop(point: NearbyPoint, order: number, locale: SupportedLocale): RouteStop {
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

function createAudioStory(point: NearbyPoint, locale: SupportedLocale): AudioStory {
  return {
    id: `${point.id}-audio`,
    url: null,
    durationSeconds: 95,
    language: locale,
    transcriptPreview:
      locale === 'ru'
        ? `Короткий рассказ о точке «${point.title}», ее атмосфере и том, почему она важна в маршруте.`
        : `A short story about ${point.title}, its atmosphere, and why it matters in the route.`,
  }
}

function pickMixedRouteStops(
  pointsByCategory: Record<PointCategory, NearbyPoint[]>,
  categoryPattern: PointCategory[],
) {
  const selectedIds = new Set<string>()
  const picked: NearbyPoint[] = []

  for (const category of categoryPattern) {
    const nextPoint = (pointsByCategory[category] ?? []).find((point) => !selectedIds.has(point.id))

    if (!nextPoint) {
      continue
    }

    selectedIds.add(nextPoint.id)
    picked.push(nextPoint)
  }

  return picked
}

function groupPointsByCategory(points: NearbyPoint[]) {
  return points.reduce<Record<PointCategory, NearbyPoint[]>>(
    (groups, point) => {
      groups[point.category].push(point)
      return groups
    },
    {
      museum: [],
      food: [],
      park: [],
      entertainment: [],
      landmark: [],
    },
  )
}

function getPresentCategories(pointsByCategory: Record<PointCategory, NearbyPoint[]>) {
  const order: PointCategory[] = ['landmark', 'museum', 'park', 'food', 'entertainment']
  return order.filter((category) => (pointsByCategory[category] ?? []).length >= 2)
}

function pushRouteDraft(
  drafts: GeneratedRouteDraft[],
  seenRoutes: Set<string>,
  draft: GeneratedRouteDraft,
  stops: NearbyPoint[],
) {
  const key = stops
    .map((stop) => stop.id)
    .sort()
    .join('|')

  if (!key || seenRoutes.has(key)) {
    return
  }

  seenRoutes.add(key)
  drafts.push(draft)
}

function sortDiscoveryDrafts(left: GeneratedRouteDraft, right: GeneratedRouteDraft) {
  const themeDiff = themePriority[left.theme] - themePriority[right.theme]

  if (themeDiff !== 0) {
    return themeDiff
  }

  return left.title.localeCompare(right.title, 'ru')
}

function getCategoryRouteLabel(category: PointCategory, locale: SupportedLocale) {
  if (locale !== 'ru') {
    switch (category) {
      case 'museum':
        return 'Museum route'
      case 'landmark':
        return 'Historic route'
      case 'food':
        return 'Food route'
      case 'park':
        return 'Nature route'
      case 'entertainment':
        return 'Fun route'
      default:
        return 'Route'
    }
  }

  switch (category) {
    case 'museum':
      return 'Музейный маршрут'
    case 'landmark':
      return 'Исторический маршрут'
    case 'food':
      return 'Гастро-маршрут'
    case 'park':
      return 'Зеленый маршрут'
    case 'entertainment':
      return 'Маршрут впечатлений'
    default:
      return 'Маршрут'
  }
}

function getVariantLabel(stopCount: number, variantIndex: number, locale: SupportedLocale) {
  const labels =
    locale === 'ru'
      ? ['быстрый круг', 'основной сценарий', 'расширенная версия']
      : ['quick loop', 'main scenario', 'extended version']

  if (stopCount <= 2) {
    return labels[0]
  }

  return labels[Math.min(variantIndex, labels.length - 1)]
}

function getMixedRouteTitle(key: string, locale: SupportedLocale) {
  const ru: Record<string, string> = {
    'city-story': 'Городской микс рядом',
    'after-lunch': 'Послеобеденная прогулка',
    weekend: 'Маршрут на свободный день',
    evening: 'Вечер в районе',
    'slow-day': 'Спокойный день рядом',
    'city-highlight': 'Лучшее поблизости',
  }

  const en: Record<string, string> = {
    'city-story': 'A city route without a single theme',
    'after-lunch': 'After-lunch neighborhood walk',
    weekend: 'A route for a free day',
    evening: 'Evening city scenario',
    'slow-day': 'A slower day nearby',
    'city-highlight': 'Highlights nearby',
  }

  return locale === 'ru' ? ru[key] ?? ru['city-story'] : en[key] ?? en['city-story']
}

function buildAudienceLabel(theme: ExcursionTheme, stopCount: number, locale: SupportedLocale) {
  if (locale !== 'ru') {
    switch (theme) {
      case 'food':
        return stopCount <= 2 ? 'For a quick bite and a short walk' : 'For a slower route with food stops'
      case 'nature':
        return 'For a quieter pace and more air'
      case 'fun':
        return 'For a more vivid walk with scene changes'
      case 'mixed':
        return 'For users who want a balanced route'
      case 'walk':
      default:
        return 'For a comfortable city walk'
    }
  }

  switch (theme) {
    case 'food':
      return stopCount <= 2 ? 'Для короткой прогулки и паузы на еду' : 'Для маршрута с гастро-остановками'
    case 'nature':
      return 'Для более спокойного темпа и зеленых пауз'
    case 'fun':
      return 'Для живой прогулки со сменой сцен'
    case 'mixed':
      return 'Для тех, кто хочет готовый маршрут с разным ритмом'
    case 'walk':
    default:
      return 'Для комфортной прогулки по району'
  }
}

function buildCategoryTitle(
  category: PointCategory,
  stopCount: number,
  variantIndex: number,
  locale: SupportedLocale,
) {
  if (locale !== 'ru') {
    return `${getCategoryRouteLabel(category, locale)} ${variantIndex + 1}`
  }

  const titleSets: Record<PointCategory, string[]> = {
    museum: ['Музеи рядом', 'Музейная прогулка', 'Галереи и музейные точки'],
    landmark: ['Исторические места рядом', 'Следы города', 'Памятники и истории'],
    food: ['Вкусный маршрут рядом', 'Гастро-паузы по району', 'Где поесть по пути'],
    park: ['Зеленая прогулка рядом', 'Парки и тихие точки', 'Маршрут для паузы и воздуха'],
    entertainment: ['Маршрут впечатлений', 'Где провести время', 'Яркие точки рядом'],
  }

  const pool = titleSets[category]
  const baseTitle = pool[Math.min(variantIndex, pool.length - 1)]

  if (stopCount >= 4 && category !== 'food') {
    return `${baseTitle}: расширенный`
  }

  if (stopCount <= 2) {
    return `${baseTitle}: короткий`
  }

  return baseTitle
}

function buildCategoryTagline(
  category: PointCategory,
  firstTitle: string,
  lastTitle: string,
  locale: SupportedLocale,
) {
  if (locale !== 'ru') {
    return `${firstTitle} > ${lastTitle}`
  }

  switch (category) {
    case 'museum':
      return `От ${firstTitle} к ${lastTitle} с музейными паузами`
    case 'landmark':
      return `История района от ${firstTitle} до ${lastTitle}`
    case 'food':
      return `Маршрут со вкусными остановками: ${firstTitle} > ${lastTitle}`
    case 'park':
      return `Зеленый темп от ${firstTitle} до ${lastTitle}`
    case 'entertainment':
      return `Живой сценарий от ${firstTitle} до ${lastTitle}`
    default:
      return `${firstTitle} > ${lastTitle}`
  }
}

function buildCategoryDescription(
  category: PointCategory,
  stops: NearbyPoint[],
  radiusLabel: string,
) {
  const countLabel =
    stops.length === 2 ? 'двух точках' : stops.length === 3 ? 'трех точках' : `${stops.length} точках`

  switch (category) {
    case 'museum':
      return `Неспешный маршрут по ${countLabel} с музейной атмосферой и удобными паузами внутри ${radiusLabel}.`
    case 'landmark':
      return `Маршрут по историческим местам в пределах ${radiusLabel}: памятники, городские символы и короткие остановки по пути.`
    case 'food':
      return `Гастрономический сценарий в радиусе ${radiusLabel}: быстрые и приятные остановки там, где удобно продолжать прогулку.`
    case 'park':
      return `Зеленый маршрут в пределах ${radiusLabel} для более спокойного ритма, воздуха и коротких передышек.`
    case 'entertainment':
      return `Живой маршрут по точкам, где можно задержаться, переключить темп и провести время по-настоящему интересно.`
    default:
      return `Маршрут собран внутри ${radiusLabel}.`
  }
}

function buildMixedDescription(key: string, radiusLabel: string) {
  const descriptions: Record<string, string> = {
    'city-story': `История, музейные остановки, зеленая пауза и еда собираются в одном маршруте внутри ${radiusLabel}.`,
    'after-lunch': `Комфортный маршрут после еды: короткие переходы, городские точки и спокойная развязка в пределах ${radiusLabel}.`,
    weekend: `Маршрут на свободный день: разный темп, выразительные места и ощущение цельной прогулки внутри ${radiusLabel}.`,
    evening: `Сценарий для второй половины дня: яркие места, пауза на еду и спокойный финал в радиусе ${radiusLabel}.`,
    'slow-day': 'Спокойный маршрут с зеленой паузой, историей и местом для отдыха — без спешки и лишних переходов.',
    'city-highlight': 'Собранный сценарий по сильным точкам района: городская история, вид, еда и место, где приятно задержаться.',
  }

  return descriptions[key] ?? descriptions['city-story']
}

function buildMixedTagline(
  key: string,
  firstTitle: string,
  lastTitle: string,
  locale: SupportedLocale,
) {
  if (locale !== 'ru') {
    return `${firstTitle} > ${lastTitle}`
  }

  const taglines: Record<string, string> = {
    'city-story': 'История, пауза и вкусный финал в одном маршруте',
    'after-lunch': 'Легкая прогулка после еды с хорошим ритмом',
    weekend: 'Когда хочется не выбирать одну тему',
    evening: 'Маршрут на поздний день с живым темпом',
    'slow-day': 'Спокойный сценарий без лишней спешки',
    'city-highlight': 'Лучшее из района в одном маршруте',
  }

  return taglines[key] ?? `${firstTitle} > ${lastTitle}`
}

function pickRouteCoverImage(stops: NearbyPoint[]) {
  const photoStop = stops.find((stop) => !stop.imageUrl.includes('staticmap.openstreetmap.de'))
  return photoStop?.imageUrl ?? stops[0]?.imageUrl ?? '/illustrations/landmark-card.svg'
}

function getDifficultyByStops(stops: NearbyPoint[]): ExcursionDifficulty {
  const totalVisitMinutes = stops.reduce(
    (minutes, stop) => minutes + stop.expectedVisitMinutes,
    0,
  )

  if (stops.length <= 2 && totalVisitMinutes <= 50) {
    return 'easy'
  }

  if (stops.length <= 4 && totalVisitMinutes <= 95) {
    return 'medium'
  }

  return 'hard'
}

function createRouteSlug(theme: ExcursionTheme, stops: NearbyPoint[], suffix: string) {
  const stopPart = stops
    .map((stop) => stop.id.replace(/[^a-z0-9-]/gi, '-'))
    .join('-')

  return `${theme}-${suffix.toLowerCase().replace(/\s+/g, '-')}-${stopPart}`
}

function getRouteDistanceKm(points: GeoPoint[]) {
  let distanceMeters = 0

  for (let index = 0; index < points.length - 1; index += 1) {
    distanceMeters += getDistanceMeters(points[index], points[index + 1])
  }

  return Number((distanceMeters / 1000).toFixed(1))
}

function getDistanceMeters(from: GeoPoint, to: GeoPoint) {
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

function degreesToRadians(value: number) {
  return (value * Math.PI) / 180
}

function formatRadiusLabel(radiusMeters: number, locale: SupportedLocale) {
  const radiusKm = radiusMeters / 1000
  return locale === 'ru' ? `${radiusKm} км` : `${radiusKm} km`
}
