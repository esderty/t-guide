import type { RouteStop } from '@/entities/excursion/model/types'
import {
  formatDuration,
  formatPointCategory,
  formatRating,
} from '@/shared/lib/format'

interface PlaceDetailsCardProps {
  stop: RouteStop
}

export function PlaceDetailsCard({ stop }: PlaceDetailsCardProps) {
  return (
    <article className="details-card">
      <div className="details-card__image">
        <img src={stop.imageUrl} alt={stop.title} />
      </div>

      <div className="details-card__body">
        <div className="details-card__title-row">
          <div>
            <p className="eyebrow">{formatPointCategory(stop.category)}</p>
            <h2 className="details-card__title">{stop.title}</h2>
            <p className="page-description details-card__lead">{stop.shortDescription}</p>
          </div>
        </div>

        <div className="details-card__meta">
          <span className="chip chip--accent">Точка #{stop.order}</span>
          <span className="chip">Остановка {formatDuration(stop.expectedVisitMinutes)}</span>
          <span className="chip">Рейтинг {formatRating(stop.rating)}</span>
          <span className="chip">{stop.scheduleLabel}</span>
        </div>

        <p className="details-card__description">{stop.description}</p>
      </div>
    </article>
  )
}
