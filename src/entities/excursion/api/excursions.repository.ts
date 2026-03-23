import type { Excursion } from '@/entities/excursion/model/types'
import { request } from '@/shared/api/http'

import { excursionsMock } from './excursions.mock'

const useMockData = !import.meta.env.VITE_API_URL

export async function getExcursions(): Promise<Excursion[]> {
  if (useMockData) {
    return Promise.resolve(excursionsMock)
  }

  return request<Excursion[]>('/excursions')
}

export async function getExcursionBySlug(
  slug: string,
): Promise<Excursion | null> {
  if (useMockData) {
    return Promise.resolve(
      excursionsMock.find((excursion) => excursion.slug === slug) ?? null,
    )
  }

  return request<Excursion>(`/excursions/${slug}`)
}
