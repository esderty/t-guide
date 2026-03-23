import type { ReactNode } from 'react'

import type { RouteMapProps } from '@/features/route-map/model/types'
import { projectRouteStops } from '@/shared/lib/geo'

interface RouteMapPreviewProps extends RouteMapProps {
  embedded?: boolean
  showLegend?: boolean
  statusSlot?: ReactNode
}

function RouteMapPreviewContent({
  stops,
  selectedStopId,
  routeColor,
  onSelect,
  showLegend = true,
}: RouteMapPreviewProps) {
  const projectedStops = projectRouteStops(stops)
  const polyline = projectedStops.map((stop) => `${stop.x},${stop.y}`).join(' ')

  return (
    <>
      <div className="map-card__canvas">
        <svg className="map-card__svg" preserveAspectRatio="none" viewBox="0 0 100 100">
          <polyline
            fill="none"
            points={polyline}
            stroke={routeColor}
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth="3.2"
          />
        </svg>

        {projectedStops.map((stop) => {
          const isActive = stop.id === selectedStopId

          return (
            <button
              className={`map-card__stop${isActive ? ' map-card__stop--active' : ''}`}
              key={stop.id}
              onClick={() => onSelect(stop.id)}
              style={{ left: `${stop.x}%`, top: `${stop.y}%` }}
              type="button"
            >
              {stop.order}
            </button>
          )
        })}
      </div>

      {showLegend ? (
        <div className="map-card__legend">
          <span className="chip">Маршрут</span>
          <span className="chip">Точки экскурсии</span>
          <span className="chip">Активная точка</span>
        </div>
      ) : null}
    </>
  )
}

export function RouteMapPreview({
  embedded = false,
  statusSlot,
  ...props
}: RouteMapPreviewProps) {
  if (embedded) {
    return (
      <div className="map-card__preview">
        <RouteMapPreviewContent {...props} />
      </div>
    )
  }

  return (
    <section className="map-card">
      <div className="map-card__header">
        <h2 className="map-card__title">Карта маршрута</h2>
        {statusSlot}
      </div>

      <RouteMapPreviewContent {...props} />
    </section>
  )
}
