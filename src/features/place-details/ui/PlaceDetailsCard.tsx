import type { RouteStop } from '@/entities/excursion/model/types'
import {
  formatDuration,
  formatPointCategory,
  formatRating,
} from '@/shared/lib/format'
import { SmartPlaceImage } from '@/shared/ui/SmartPlaceImage'

interface PlaceDetailsCardProps {
  stop: RouteStop
}

export function PlaceDetailsCard({ stop }: PlaceDetailsCardProps) {
  const hasRating = stop.rating > 0

  return (
    <article className="details-card">
      <div className="details-card__image">
        <SmartPlaceImage
          alt={stop.title}
          category={stop.category}
          coordinates={stop.coordinates}
          loading="lazy"
          referrerPolicy="no-referrer"
          src={stop.imageUrl}
          title={stop.title}
          wikidataId={stop.wikidataId}
          wikipediaTitle={stop.wikipediaTitle}
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
