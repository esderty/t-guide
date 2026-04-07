import type { CSSProperties } from 'react'
import { Link } from 'react-router-dom'

import type { Excursion } from '@/entities/excursion/model/types'
import { buildStaticPlaceImageUrl } from '@/entities/place/lib/place-images'
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

interface ExcursionCardProps {
  excursion: Excursion
}

export function ExcursionCard({ excursion }: ExcursionCardProps) {
  const firstStop = excursion.stops[0]
  const coverFallbacks = firstStop
    ? [
        buildStaticPlaceImageUrl(firstStop.coordinates, firstStop.category, 15),
        '/illustrations/landmark-card.svg',
      ]
    : ['/illustrations/landmark-card.svg']

  return (
    <Link className="card" to={appRoutes.excursion(excursion.slug)}>
      <div
        className="card__cover card__cover--gradient"
        style={{ '--route-accent': excursion.routeColor } as CSSProperties}
      >
        <ResilientImage
          alt={excursion.title}
          fallbackSrcs={coverFallbacks}
          loading="lazy"
          placeholderSrc={buildRoutePlaceholderImage(excursion.theme)}
          referrerPolicy="no-referrer"
          src={excursion.coverImageUrl || undefined}
        />
        <span className="card__theme-badge">{formatTheme(excursion.theme)}</span>
      </div>

      <div className="card__content">
        <div className="card__title-row">
          <div>
            <h3 className="card__title">{excursion.title}</h3>
            <p className="page-description">{excursion.tagline}</p>
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
      </div>
    </Link>
  )
}
