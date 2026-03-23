import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'

import {
  getExcursionBySlug,
} from '@/entities/excursion/api/excursions.repository'
import type { Excursion } from '@/entities/excursion/model/types'
import { AudioGuidePanel } from '@/features/audio-guide/ui/AudioGuidePanel'
import { PlaceDetailsCard } from '@/features/place-details/ui/PlaceDetailsCard'
import { appRoutes } from '@/shared/config/routes'
import {
  formatDifficulty,
  formatDistance,
  formatDuration,
  formatStopCount,
  formatTheme,
} from '@/shared/lib/format'
import { RouteOverview } from '@/widgets/route-overview/ui/RouteOverview'

export function ExcursionPage() {
  const { slug } = useParams<{ slug: string }>()
  const [excursion, setExcursion] = useState<Excursion | null>(null)
  const [selectedStopId, setSelectedStopId] = useState<string>('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const excursionSlug = slug

    if (!excursionSlug) {
      setError('Маршрут не найден.')
      setLoading(false)
      return
    }

    const stableSlug: string = excursionSlug

    let isMounted = true

    async function loadExcursion() {
      try {
        setLoading(true)
        setError(null)

        const nextExcursion = await getExcursionBySlug(stableSlug)

        if (!isMounted) {
          return
        }

        if (!nextExcursion) {
          setError('Маршрут не найден.')
          setExcursion(null)
          return
        }

        setExcursion(nextExcursion)
        setSelectedStopId(nextExcursion.stops[0]?.id ?? '')
      } catch {
        if (!isMounted) {
          return
        }

        setError('Не удалось загрузить экскурсию.')
      } finally {
        if (isMounted) {
          setLoading(false)
        }
      }
    }

    void loadExcursion()

    return () => {
      isMounted = false
    }
  }, [slug])

  const selectedStop =
    excursion?.stops.find((stop) => stop.id === selectedStopId) ??
    excursion?.stops[0] ??
    null

  const nextStop =
    excursion && selectedStop
      ? excursion.stops.find((stop) => stop.order === selectedStop.order + 1)
      : undefined

  if (loading) {
    return (
      <section className="status-card">
        <h1 className="status-card__title">Загружаем маршрут</h1>
        <p className="status-card__text">Подождите немного, страница открывается.</p>
      </section>
    )
  }

  if (error || !excursion || !selectedStop) {
    return (
      <section className="not-found">
        <h1 className="not-found__title">Маршрут пока недоступен</h1>
        <p className="not-found__description">
          {error ?? 'Мы не нашли данные по выбранной экскурсии.'}
        </p>
        <Link className="button button--secondary" to={appRoutes.excursions}>
          Вернуться к экскурсиям
        </Link>
      </section>
    )
  }

  return (
    <section className="page">
      <article className="route-hero">
        <Link className="inline-link" to={appRoutes.excursions}>
          К экскурсиям
        </Link>

        <div className="route-hero__title-row">
          <div>
            <p className="eyebrow">Экскурсия</p>
            <h1 className="route-title">{excursion.title}</h1>
          </div>
          <span className="card__tag">{formatDifficulty(excursion.difficulty)}</span>
        </div>

        <p className="route-hero__description">{excursion.description}</p>

        <div className="route-hero__meta">
          <div className="meta-pill">
            <span className="meta-pill__value">
              {formatDuration(excursion.durationMinutes)}
            </span>
            <span className="meta-pill__label">Время прогулки</span>
          </div>
          <div className="meta-pill">
            <span className="meta-pill__value">
              {formatDistance(excursion.distanceKm)}
            </span>
            <span className="meta-pill__label">Протяженность</span>
          </div>
          <div className="meta-pill">
            <span className="meta-pill__value">
              {formatStopCount(excursion.stops.length)}
            </span>
            <span className="meta-pill__label">Точек</span>
          </div>
        </div>

        <div className="route-hero__path">
          <span className="chip chip--accent">{formatTheme(excursion.theme)}</span>
          <span className="chip">{excursion.district}</span>
          <span className="chip">Старт: {excursion.startLabel}</span>
          <span className="chip">Финиш: {excursion.finishLabel}</span>
          <span className="chip">Активная точка: {selectedStop.title}</span>
        </div>
      </article>

      <div className="route-layout">
        <div className="route-layout__column">
          <RouteOverview
            routeColor={excursion.routeColor}
            selectedStopId={selectedStop.id}
            stops={excursion.stops}
            onSelectStop={setSelectedStopId}
          />
        </div>

        <div className="route-layout__column">
          <PlaceDetailsCard stop={selectedStop} />
          <AudioGuidePanel
            nextStop={nextStop}
            onNextStop={() => {
              if (nextStop) {
                setSelectedStopId(nextStop.id)
              }
            }}
            stop={selectedStop}
          />
        </div>
      </div>
    </section>
  )
}
