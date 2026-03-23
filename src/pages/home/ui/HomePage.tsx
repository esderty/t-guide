import { Link } from 'react-router-dom'

import {
  getExcursionStats,
  getLatestExcursions,
} from '@/entities/excursion/lib/excursion-utils'
import { useExcursions } from '@/entities/excursion/model/useExcursions'
import { appRoutes } from '@/shared/config/routes'
import {
  formatDistance,
  formatDuration,
} from '@/shared/lib/format'
import { ExcursionCatalog } from '@/widgets/excursion-catalog/ui/ExcursionCatalog'

export function HomePage() {
  const { excursions, loading, error } = useExcursions()
  const { totalCount, totalDistance, shortestExcursion, longestExcursion } =
    getExcursionStats(excursions)
  const latestExcursions = getLatestExcursions(excursions, 3)

  return (
    <section className="page">
      <article className="page-hero">
        <div className="hero-copy">
          <p className="eyebrow">Аудиогид по Самаре</p>
          <h1 className="page-title">
            Готовые прогулки по городу с маршрутами, точками и аудиоисториями
          </h1>
          <p className="page-description">
            Выбирайте экскурсию, открывайте маршрут и проходите его в удобном
            темпе, знакомясь с достопримечательностями Самары.
          </p>
          <div className="hero-actions">
            <Link className="button button--primary" to={appRoutes.excursions}>
              Смотреть экскурсии
            </Link>
          </div>
        </div>

        <div className="stats-grid">
          <div className="hero-stat">
            <span className="hero-stat__value">{totalCount}</span>
            <span className="hero-stat__label">Всего экскурсий</span>
          </div>
          <div className="hero-stat">
            <span className="hero-stat__value">
              {formatDistance(totalDistance)}
            </span>
            <span className="hero-stat__label">Суммарная длина</span>
          </div>
          <div className="hero-stat">
            <span className="hero-stat__value">
              {shortestExcursion
                ? formatDuration(shortestExcursion.durationMinutes)
                : '0 мин'}
            </span>
            <span className="hero-stat__label">Самая короткая</span>
          </div>
          <div className="hero-stat">
            <span className="hero-stat__value">
              {longestExcursion
                ? formatDuration(longestExcursion.durationMinutes)
                : '0 мин'}
            </span>
            <span className="hero-stat__label">Самая длинная</span>
          </div>
        </div>
      </article>

      <section className="page-section">
        <div className="section-heading">
          <div>
            <h2 className="section-title">Последние экскурсии</h2>
            <p className="section-description">
              Подборка новых маршрутов, которые уже доступны в приложении.
            </p>
          </div>
        </div>

        {loading ? (
          <section className="status-card">
            <h3 className="status-card__title">Загружаем маршруты</h3>
            <p className="status-card__text">Подождите немного, маршруты загружаются.</p>
          </section>
        ) : error ? (
          <section className="status-card">
            <h3 className="status-card__title">Не удалось загрузить главную</h3>
            <p className="status-card__text">{error}</p>
          </section>
        ) : (
          <>
            <ExcursionCatalog excursions={latestExcursions} />
            <div className="section-actions">
              <Link className="button button--secondary" to={appRoutes.excursions}>
                Посмотреть все
              </Link>
            </div>
          </>
        )}
      </section>
    </section>
  )
}
