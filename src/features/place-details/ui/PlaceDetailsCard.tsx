import type { RouteStop } from '@/entities/excursion/model/types'
import { buildStaticPlaceImageUrl } from '@/entities/place/lib/place-images'
import {
  formatDuration,
  formatPointCategory,
  formatRating,
} from '@/shared/lib/format'
import { ResilientImage } from '@/shared/ui/ResilientImage'

interface PlaceDetailsCardProps {
  stop: RouteStop
}

export function PlaceDetailsCard({ stop }: PlaceDetailsCardProps) {
  const hasRating = stop.rating > 0

  return (
    <article className="details-card">
      <div className="details-card__image">
        <ResilientImage
          alt={stop.title}
          fallbackSrcs={[
            buildStaticPlaceImageUrl(stop.coordinates, stop.category, 16),
            '/illustrations/landmark-card.svg',
          ]}
          loading="lazy"
          referrerPolicy="no-referrer"
          src={stop.imageUrl}
        />
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
          {hasRating ? <span className="chip">Рейтинг {formatRating(stop.rating)}</span> : null}
          <span className="chip">{stop.scheduleLabel}</span>
        </div>

        <p className="details-card__description">{stop.description}</p>
      </div>
    </article>
  )
}