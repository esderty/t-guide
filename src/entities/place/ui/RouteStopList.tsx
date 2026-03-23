import type { RouteStop } from '@/entities/excursion/model/types'
import { formatCoordinates, formatDuration } from '@/shared/lib/format'

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
          <p className="section-description">Выберите место, чтобы открыть его карточку.</p>
        </div>
      </div>

      <div className="stop-list__items">
        {stops.map((stop) => {
          const isActive = stop.id === selectedStopId

          return (
            <button
              className={`stop-list__button${isActive ? ' stop-list__button--active' : ''}`}
              key={stop.id}
              onClick={() => onSelect(stop.id)}
              type="button"
            >
              <div className="stop-list__title-row">
                <div>
                  <h3 className="stop-list__title">{stop.title}</h3>
                  <p className="stop-list__summary">{stop.shortDescription}</p>
                </div>
                <span className="stop-list__order">{stop.order}</span>
              </div>

              <div className="stop-list__meta">
                <span className="chip">{formatCoordinates(stop.coordinates)}</span>
                <span className="chip">
                  На точку {formatDuration(stop.expectedVisitMinutes)}
                </span>
                <span className="chip">
                  Аудио {formatDuration(Math.ceil(stop.audio.durationSeconds / 60))}
                </span>
              </div>
            </button>
          )
        })}
      </div>
    </section>
  )
}
