import type { CSSProperties } from 'react'
import { Link } from 'react-router-dom'

import { useAuth } from '@/app/providers/useAuth'
import type { Excursion } from '@/entities/excursion/model/types'
import { buildStaticPlaceImageUrl } from '@/entities/place/lib/place-images'
import { useUserRoutes } from '@/features/user-routes/model/useUserRoutes'
import { appRoutes } from '@/shared/config/routes'
import { buildRoutePlaceholderImage } from '@/shared/lib/placeholder-images'
import {
  formatDifficulty,
  formatDistance,
  formatDuration,
  formatStopCount,
  formatTheme,
} from '@/shared/lib/format'
import { ResilientImage } from '@/shared/ui/ResilientImage'
import './ExcursionCard.css'

interface ExcursionCardProps {
  excursion: Excursion
}

export function ExcursionCard({ excursion }: ExcursionCardProps) {
  const { session } = useAuth()
  const {
    isRouteSaved,
    shareRoute,
    toggleSavedRoute,
  } = useUserRoutes()
  const isAuthenticated = Boolean(session?.isAuthenticated && session.profile)
  const firstStop = excursion.stops[0]
  const routeUrl = appRoutes.excursion(excursion.slug)
  const isSaved = isRouteSaved(excursion.slug)
  const coverFallbacks = firstStop
    ? [
        buildStaticPlaceImageUrl(firstStop.coordinates, firstStop.category, 15),
        buildRoutePlaceholderImage(excursion.theme),
      ]
    : [buildRoutePlaceholderImage(excursion.theme)]
  const routePlaceholder = buildRoutePlaceholderImage(excursion.theme)
  const coverSrc =
    excursion.coverImageUrl && !excursion.coverImageUrl.startsWith('/illustrations/')
      ? excursion.coverImageUrl
      : routePlaceholder

  return (
    <article className="card">
      <Link
        aria-label={`Открыть маршрут ${excursion.title}`}
        className="card__cover-link"
        to={routeUrl}
      >
        <div
          className="card__cover card__cover--gradient"
          style={{ '--route-accent': excursion.routeColor } as CSSProperties}
        >
          <ResilientImage
            alt={excursion.title}
            fallbackSrcs={coverFallbacks}
            loading="lazy"
            placeholderSrc={routePlaceholder}
            referrerPolicy="no-referrer"
            src={coverSrc}
          />
          <span className="card__theme-badge">{formatTheme(excursion.theme)}</span>
        </div>
      </Link>

      <div className="card__content">
        <div className="card__title-row">
          <div>
            <Link className="card__title-link" to={routeUrl}>
              <h3 className="card__title">{excursion.title}</h3>
            </Link>
            <p className="card__tagline">{excursion.tagline}</p>
          </div>
          <span className="card__tag">{formatDifficulty(excursion.difficulty)}</span>
        </div>

        <p className="card__description">{excursion.description}</p>

        <div className="card__stop-preview">
          <span className="chip chip--accent">{excursion.audienceLabel}</span>
          <span className="chip">{excursion.district}</span>
        </div>

        <div className="card__meta">
          <div className="meta-pill">
            <span className="meta-pill__value">{formatDuration(excursion.durationMinutes)}</span>
            <span className="meta-pill__label">Длительность</span>
          </div>
          <div className="meta-pill">
            <span className="meta-pill__value">{formatDistance(excursion.distanceKm)}</span>
            <span className="meta-pill__label">Дистанция</span>
          </div>
          <div className="meta-pill">
            <span className="meta-pill__value">{formatStopCount(excursion.stops.length)}</span>
            <span className="meta-pill__label">Точки</span>
          </div>
        </div>

        <div className="card__route-points">
          <span className="card__route-point">
            <strong>Старт:</strong> {excursion.startLabel}
          </span>
          <span className="card__route-point">
            <strong>Финиш:</strong> {excursion.finishLabel}
          </span>
        </div>

        <div className="card__actions">
          <Link className="button button--primary" to={routeUrl}>
            Открыть
          </Link>
          {isAuthenticated ? (
            <button
              aria-pressed={isSaved}
              className={`card__icon-button${isSaved ? ' card__icon-button--active' : ''}`}
              onClick={() => toggleSavedRoute(excursion)}
              type="button"
            >
              <span aria-hidden="true">{isSaved ? '♥' : '♡'}</span>
              <span>{isSaved ? 'Сохранено' : 'Сохранить'}</span>
            </button>
          ) : null}
          <button
            className="card__icon-button"
            onClick={() => void shareRoute(excursion)}
            type="button"
          >
            <span aria-hidden="true">↗</span>
            <span>Поделиться</span>
          </button>
        </div>
      </div>
    </article>
  )
}
