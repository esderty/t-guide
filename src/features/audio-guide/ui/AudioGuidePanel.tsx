import type { RouteStop } from '@/entities/excursion/model/types'
import {
  formatDuration,
  formatLocaleLabel,
} from '@/shared/lib/format'
import './AudioGuidePanel.css'

interface AudioGuidePanelProps {
  stop: RouteStop
  nextStop?: RouteStop
  onNextStop: () => void
}

export function AudioGuidePanel({
  stop,
  nextStop,
  onNextStop,
}: AudioGuidePanelProps) {
  const audioIsReady = Boolean(stop.audio.url)

  return (
    <section className="audio-panel">
      <div className="audio-panel__title-row">
        <div>
          <h2 className="audio-panel__title">Аудиогид точки</h2>
          <p className="audio-panel__summary">{stop.audio.transcriptPreview}</p>
        </div>
      </div>

      <div className="audio-panel__meta">
        <span className="chip">
          Длительность {formatDuration(Math.ceil(stop.audio.durationSeconds / 60))}
        </span>
        <span className="chip">Язык {formatLocaleLabel(stop.audio.language)}</span>
      </div>

      {audioIsReady ? (
        <audio controls preload="metadata" src={stop.audio.url ?? undefined} />
      ) : (
        <p className="audio-panel__placeholder">
          Сейчас доступно текстовое описание точки.
        </p>
      )}

      <div className="audio-panel__actions">
        <button className="button button--primary" disabled={!audioIsReady} type="button">
          Запустить аудиогид
        </button>
        <button
          className="button button--secondary"
          disabled={!nextStop}
          onClick={onNextStop}
          type="button"
        >
          {nextStop ? `Следующая точка: ${nextStop.order}` : 'Маршрут завершен'}
        </button>
      </div>
    </section>
  )
}
