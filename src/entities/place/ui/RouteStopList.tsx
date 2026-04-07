import type { RouteStop } from '@/entities/excursion/model/types'
import {
  formatDuration,
  formatPointCategory,
  formatRating,
} from '@/shared/lib/format'

interface RouteStopListProps {
  stops: RouteStop[]
  selectedStopId: string
  onSelect: (stopId: string) => void
}

export function RouteStopList({
  stops,
  selectedStopId,
  onSelect,
}: RouteStopListProps) {
  return (
    <section className="stop-list">
      <div className="section-heading">
        <div>
          <h2 className="section-title">Точки маршрута</h2>
        </div>
      </div>

      <div className="stop-list__items">
        {stops.map((stop) => {
          const isActive = stop.id === selectedStopId
          const hasRating = stop.rating > 0

          return (
            <button
              className={`stop-list__button${isActive ? ' stop-list__button--active' : ''}`}
              key={stop.id}
              onClick={() => onSelect(stop.id)}
              type="button"
            >
              <div className="stop-list__title-row">
                <div>
                  <p className="eyebrow">{formatPointCategory(stop.category)}</p>
                  <h3 className="stop-list__title">{stop.title}</h3>
                  <p className="stop-list__summary">{stop.shortDescription}</p>
                </div>
                <span className="stop-list__order">{stop.order}</span>
              </div>

              <div className="stop-list__meta">
                <span className="chip">{stop.scheduleLabel}</span>
                <span className="chip">Остановка {formatDuration(stop.expectedVisitMinutes)}</span>
                {hasRating ? <span className="chip">Рейтинг {formatRating(stop.rating)}</span> : null}
              </div>
            </button>
          )
        })}
      </div>
    </section>
  )
}
