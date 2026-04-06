interface ResolvePlacePhotoParams {
  wikidataId?: string
  wikipediaTitle?: string
}

interface WikipediaPageImage {
  original?: {
    source?: string
  }
  thumbnail?: {
    source?: string
  }
}

const photoCache = new Map<string, Promise<string | null>>()
const supportedLanguages = ['ru', 'en', 'de', 'fr', 'es'] as const

export function resolvePlacePhoto({
  wikidataId,
  wikipediaTitle,
}: ResolvePlacePhotoParams) {
  const cacheKey = [wikipediaTitle ?? '', wikidataId ?? ''].join(':')

  if (!cacheKey.replaceAll(':', '').trim()) {
    return Promise.resolve(null)
  }

  const cached = photoCache.get(cacheKey)

  if (cached) {
    return cached
  }

  const request = findPlacePhoto({
    wikidataId,
    wikipediaTitle,
  }).catch(() => null)

  photoCache.set(cacheKey, request)
  return request
}

async function findPlacePhoto({
  wikidataId,
  wikipediaTitle,
}: ResolvePlacePhotoParams) {
  const exactReference = parseWikipediaReference(wikipediaTitle)

  if (exactReference) {
    const exactImage = await fetchWikipediaImageByTitle(
      exactReference.language,
      exactReference.title,
    )

    if (exactImage) {
      return exactImage
    }
  }

  if (wikidataId) {
    const wikidataImage = await fetchWikidataImage(wikidataId)

    if (wikidataImage) {
      return wikidataImage
    }
  }

  return null
}

async function fetchWikipediaImageByTitle(language: string, title: string) {
  const params = new URLSearchParams({
    action: 'query',
    format: 'json',
    formatversion: '2',
    origin: '*',
    prop: 'pageimages',
    piprop: 'original|thumbnail',
    pithumbsize: '1600',
    titles: title,
  })
  const payload = await fetchJson<{
    query?: {
      pages?: Array<WikipediaPageImage & { missing?: boolean }>
    }
  }>(`https://${language}.wikipedia.org/w/api.php?${params.toString()}`)
  const page = payload?.query?.pages?.find((entry) => !entry.missing)

  return page?.original?.source || page?.thumbnail?.source || null
}

async function fetchWikidataImage(wikidataId: string) {
  const payload = await fetchJson<{
    entities?: Record<
      string,
      {
        claims?: {
          P18?: Array<{
            mainsnak?: {
              datavalue?: {
                value?: string
              }
            }
          }>
        }
      }
    >
  }>(`https://www.wikidata.org/wiki/Special:EntityData/${encodeURIComponent(wikidataId)}.json`)

  const imageName =
    payload?.entities?.[wikidataId]?.claims?.P18?.[0]?.mainsnak?.datavalue?.value

  if (!imageName) {
    return null
  }

  return `https://commons.wikimedia.org/wiki/Special:FilePath/${encodeURIComponent(imageName)}?width=1600`
}

async function fetchJson<T>(url: string) {
  const response = await fetch(url, {
    headers: {
      Accept: 'application/json',
    },
  })

  if (!response.ok) {
    return null
  }

  return (await response.json()) as T
}

function parseWikipediaReference(reference?: string) {
  if (!reference) {
    return null
  }

  const [language, ...titleParts] = reference.split(':')

  if (!titleParts.length) {
    return null
  }

  return {
    language: normalizeLanguage(language) ?? 'en',
    title: titleParts.join(':'),
  }
}

function normalizeLanguage(value: string) {
  const trimmed = value.trim().toLocaleLowerCase()
  const [language] = trimmed.split('-')

  return supportedLanguages.includes(language as (typeof supportedLanguages)[number])
    ? language
    : null
}