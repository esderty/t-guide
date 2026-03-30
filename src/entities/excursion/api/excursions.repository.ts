import type { Excursion, NearbyPoint } from '@/entities/excursion/model/types'
import { createDiscoveryFeed } from '@/entities/excursion/lib/discovery-feed'
import { request } from '@/shared/api/http'
import { getStoredDiscoveryContext } from '@/shared/lib/discovery-context'

const useMockData = !import.meta.env.VITE_API_URL

export async function getExcursions(): Promise<Excursion[]> {
  if (useMockData) {
    const context = getStoredDiscoveryContext()
    return Promise.resolve(createDiscoveryFeed(context.center, context.locale).excursions)
  }

  return request<Excursion[]>('/excursions')
}

export async function getExcursionBySlug(
  slug: string,
): Promise<Excursion | null> {
  if (useMockData) {
    const context = getStoredDiscoveryContext()
    return Promise.resolve(
      createDiscoveryFeed(context.center, context.locale).excursions.find(
        (excursion) => excursion.slug === slug,
      ) ?? null,
    )
  }

  return request<Excursion>(`/excursions/${slug}`)
}

export async function getNearbyPoints(): Promise<NearbyPoint[]> {
  if (useMockData) {
    const context = getStoredDiscoveryContext()
    return Promise.resolve(createDiscoveryFeed(context.center, context.locale).nearbyPoints)
  }

  return request<NearbyPoint[]>('/places/nearby')
}
