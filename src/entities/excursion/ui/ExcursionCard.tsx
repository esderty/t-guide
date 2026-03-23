import { Link } from 'react-router-dom'

import type { Excursion } from '@/entities/excursion/model/types'
import { appRoutes } from '@/shared/config/routes'
import {
  formatDifficulty,
  formatDistance,
  formatDuration,
  formatStopCount,
  formatTheme,
} from '@/shared/lib/format'

interface ExcursionCardProps {
  excursion: Excursion
}

export function ExcursionCard({ excursion }: ExcursionCardProps) {
  return (
    <Link className="card" to={appRoutes.excursion(excursion.slug)}>
      <div className="card__cover">
        <img src={excursion.coverImageUrl} alt={excursion.title} />
      </div>

      <div className="card__content">
        <div className="card__title-row">
          <div>
            <h3 className="card__title">{excursion.title}</h3>
            <p className="page-description">{excursion.tagline}</p>
          </div>
          <span className="card__tag">
            {formatDifficulty(excursion.difficulty)}
          </span>
        </div>

        <p className="card__description">{excursion.description}</p>

        <div className="card__stop-preview">
          <span className="chip chip--accent">{formatTheme(excursion.theme)}</span>
          <span className="chip">{excursion.district}</span>
        </div>

        <div className="card__meta">
          <div className="meta-pill">
            <span className="meta-pill__value">
              {formatDuration(excursion.durationMinutes)}
            </span>
            <span className="meta-pill__label">Длительность</span>
          </div>
          <div className="meta-pill">
            <span className="meta-pill__value">
              {formatDistance(excursion.distanceKm)}
            </span>
            <span className="meta-pill__label">Длина пути</span>
          </div>
          <div className="meta-pill">
            <span className="meta-pill__value">
              {formatStopCount(excursion.stops.length)}
            </span>
            <span className="meta-pill__label">Точки маршрута</span>
          </div>
        </div>

        <div className="card__route-points">
          <span className="card__route-point">
            <strong>Начальная:</strong> {excursion.startLabel}
          </span>
          <span className="card__route-point">
            <strong>Конечная:</strong> {excursion.finishLabel}
          </span>
        </div>
      </div>
    </Link>
  )
}
