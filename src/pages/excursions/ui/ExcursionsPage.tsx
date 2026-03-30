import { useMemo, useState } from 'react'

import { useDiscoveryRoutes } from '@/entities/excursion/model/useDiscoveryRoutes'
import type { ExcursionTheme } from '@/entities/excursion/model/types'
import { getStoredDiscoveryContext } from '@/shared/lib/discovery-context'
import {
  formatDistance,
  formatDuration,
  formatTheme,
} from '@/shared/lib/format'
import { ExcursionCatalog } from '@/widgets/excursion-catalog/ui/ExcursionCatalog'

const themeOptions: Array<ExcursionTheme | 'all'> = [
  'all',
  'walk',
  'food',
  'nature',
  'fun',
  'mixed',
]
const durationOptions = [30, 45, 60, 90, 120]

export function ExcursionsPage() {
  const storedContext = useMemo(() => getStoredDiscoveryContext(), [])
  const [activeTheme, setActiveTheme] = useState<ExcursionTheme | 'all'>('all')
  const [maxDuration, setMaxDuration] = useState<number | null>(null)

  const { excursions, isLoading } = useDiscoveryRoutes({
    activePointCategory: storedContext.activePointCategory,
    center: storedContext.center,
    locale: storedContext.locale,
    radiusMeters: storedContext.radiusMeters,
  })

  const filteredRoutes = useMemo(
    () =>
      excursions.filter((excursion) => {
        const matchesTheme = activeTheme === 'all' || excursion.theme === activeTheme
        const matchesDuration = maxDuration === null || excursion.durationMinutes <= maxDuration

        return matchesTheme && matchesDuration
      }),
    [activeTheme, excursions, maxDuration],
  )

  const totalDistance = filteredRoutes.reduce(
    (distance, excursion) => distance + excursion.distanceKm,
    0,
  )

  return (
    <section className="page">
      <article className="page-banner page-banner--compact">
        <div>
          <h1 className="page-title">Все маршруты рядом</h1>
          <p className="page-description">
            Собранные прогулки по текущему району: короткие, насыщенные, спокойные и смешанные.
          </p>
        </div>

        <div className="page-banner__stats">
          <div className="hero-stat">
            <span className="hero-stat__value">{filteredRoutes.length}</span>
            <span className="hero-stat__label">Маршрутов сейчас</span>
          </div>
          <div className="hero-stat">
            <span className="hero-stat__value">{formatDistance(totalDistance)}</span>
            <span className="hero-stat__label">Суммарная длина</span>
          </div>
          <div className="hero-stat">
            <span className="hero-stat__value">{storedContext.radiusMeters / 1000} км</span>
            <span className="hero-stat__label">Текущий радиус</span>
          </div>
        </div>
      </article>

      <section className="page-section page-section--tight">
        <div className="filter-stack">
          <div className="filter-row filter-row--wrap">
            {themeOptions.map((theme) => (
              <button
                className={`filter-pill${activeTheme === theme ? ' filter-pill--active' : ''}`}
                key={theme}
                onClick={() => setActiveTheme(theme)}
                type="button"
              >
                {theme === 'all' ? 'Все маршруты' : formatTheme(theme)}
              </button>
            ))}
          </div>

          <div className="filter-row filter-row--wrap">
            <button
              className={`filter-pill${maxDuration === null ? ' filter-pill--active' : ''}`}
              onClick={() => setMaxDuration(null)}
              type="button"
            >
              Любое время
            </button>
            {durationOptions.map((duration) => (
              <button
                className={`filter-pill${maxDuration === duration ? ' filter-pill--active' : ''}`}
                key={duration}
                onClick={() => setMaxDuration(duration)}
                type="button"
              >
                До {formatDuration(duration)}
              </button>
            ))}
          </div>
        </div>
      </section>

      {isLoading ? (
        <section className="status-card">
          <h3 className="status-card__title">Подбираем маршруты</h3>
          <p className="status-card__text">Собираем прогулки рядом с вами.</p>
        </section>
      ) : (
        <ExcursionCatalog
          emptyDescription="Попробуйте ослабить фильтр по времени или выбрать другой тип маршрута."
          emptyTitle="Маршруты по текущим условиям пока не найдены"
          excursions={filteredRoutes}
        />
      )}
    </section>
  )
}
