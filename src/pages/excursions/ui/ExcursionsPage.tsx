import { useMemo, useState } from 'react'

import type { ExcursionTheme } from '@/entities/excursion/model/types'
import { useRoutesCatalog } from '@/entities/excursion/model/useRoutesCatalog'
import { getStoredDiscoveryContext } from '@/shared/lib/discovery-context'
import {
  formatDistance,
  formatDuration,
  formatTheme,
} from '@/shared/lib/format'
import { ExcursionCatalog } from '@/widgets/excursion-catalog/ui/ExcursionCatalog'
import './ExcursionsPage.css'

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

  const {
    error,
    isLoading,
    routes,
  } = useRoutesCatalog({
    activePointCategory: storedContext.activePointCategory,
    center: storedContext.center,
    locale: storedContext.locale,
    radiusMeters: storedContext.radiusMeters,
  })

  const filteredRoutes = useMemo(
    () =>
      routes.filter((excursion) => {
        const matchesTheme = activeTheme === 'all' || excursion.theme === activeTheme
        const matchesDuration = maxDuration === null || excursion.durationMinutes <= maxDuration

        return matchesTheme && matchesDuration
      }),
    [activeTheme, maxDuration, routes],
  )

  const totalDistance = filteredRoutes.reduce(
    (distance, excursion) => distance + excursion.distanceKm,
    0,
  )

  if (isLoading) {
    return <RoutesSkeleton />
  }

  if (error) {
    return (
      <section className="status-card">
        <h1 className="status-card__title">Не удалось загрузить маршруты</h1>
        <p className="status-card__text">{error}</p>
      </section>
    )
  }

  return (
    <section className="excursions-page page-shell">
      <section className="excursions-page__hero section-surface">
        <div className="excursions-page__hero-copy">
          <span className="eyebrow">Каталог маршрутов</span>
          <h1 className="page-title">Готовые прогулки рядом</h1>
          <p className="page-subtitle">
            Отбирайте маршруты по теме и времени, чтобы быстро найти прогулку под текущую локацию.
          </p>
        </div>

        <div className="excursions-page__hero-stats">
          <div className="excursions-page__stat">
            <span className="excursions-page__stat-value">{filteredRoutes.length}</span>
            <span className="meta-label">Маршрутов сейчас</span>
          </div>
          <div className="excursions-page__stat">
            <span className="excursions-page__stat-value">{formatDistance(totalDistance)}</span>
            <span className="meta-label">Суммарная длина</span>
          </div>
          <div className="excursions-page__stat">
            <span className="excursions-page__stat-value">{storedContext.radiusMeters / 1000} км</span>
            <span className="meta-label">Текущий радиус</span>
          </div>
        </div>
      </section>

      <section className="excursions-page__filters section-surface">
        <div className="section-heading section-heading--stacked">
          <div>
            <h2 className="section-title">Фильтры каталога</h2>
            <p className="section-copy">Тема и лимит времени помогают быстро сузить каталог под нужный сценарий.</p>
          </div>

          <div className="excursions-page__filter-stack">
            <div className="filter-bar">
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

            <div className="filter-bar">
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
        </div>
      </section>

      <ExcursionCatalog
        emptyDescription="Попробуйте снять ограничение по времени или выбрать другой тип прогулки."
        emptyTitle="Маршруты по текущим условиям не найдены"
        excursions={filteredRoutes}
      />
    </section>
  )
}

function RoutesSkeleton() {
  return (
    <section className="excursions-page page-shell" aria-label="Загрузка маршрутов">
      <section className="excursions-page__hero section-surface excursions-page__skeleton-hero">
        <div className="skeleton-block skeleton-block--eyebrow" />
        <div className="skeleton-block skeleton-block--title" />
        <div className="skeleton-block skeleton-block--text" />
      </section>

      <section className="excursions-page__filters section-surface">
        <div className="excursions-page__skeleton-filters">
          {Array.from({ length: 8 }).map((_, index) => (
            <span className="skeleton-pill" key={index} />
          ))}
        </div>
      </section>

      <div className="catalog">
        {Array.from({ length: 6 }).map((_, index) => (
          <article className="route-skeleton-card" key={index}>
            <div className="route-skeleton-card__cover" />
            <div className="route-skeleton-card__body">
              <span className="skeleton-block skeleton-block--small" />
              <span className="skeleton-block skeleton-block--wide" />
              <span className="skeleton-block skeleton-block--text" />
              <span className="skeleton-block skeleton-block--text-short" />
            </div>
          </article>
        ))}
      </div>
    </section>
  )
}
