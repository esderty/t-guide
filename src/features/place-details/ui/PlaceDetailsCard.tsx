import type { RouteStop } from '@/entities/excursion/model/types'
import {
  formatCoordinates,
  formatDuration,
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
            <h2 className="details-card__title">{stop.title}</h2>
            <p className="page-description">{stop.shortDescription}</p>
          </div>
        </div>

        <div className="details-card__meta">
          <span className="chip">Точка маршрута #{stop.order}</span>
          <span className="chip">{formatCoordinates(stop.coordinates)}</span>
          <span className="chip">
            На осмотр {formatDuration(stop.expectedVisitMinutes)}
          </span>
        </div>

        <p className="details-card__description">{stop.description}</p>
      </div>
    </article>
  )
}
