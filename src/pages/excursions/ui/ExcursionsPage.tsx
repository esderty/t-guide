import { useDeferredValue, useEffect, useMemo, useRef, useState } from 'react'

import {
  defaultExcursionFilters,
  filterExcursions,
  getExcursionQuerySuggestion,
  hasActiveExcursionFilters,
  paginateItems,
} from '@/entities/excursion/lib/excursion-utils'
import { useExcursions } from '@/entities/excursion/model/useExcursions'
import { ExcursionCatalog } from '@/widgets/excursion-catalog/ui/ExcursionCatalog'
import { ExcursionFiltersPanel } from '@/features/excursion-filters/ui/ExcursionFiltersPanel'
import { runViewTransition } from '@/shared/lib/view-transition'
import { Pagination } from '@/shared/ui/Pagination'

const pageSize = 6

export function ExcursionsPage() {
  const { excursions, loading, error } = useExcursions()
  const [filters, setFilters] = useState(defaultExcursionFilters)
  const [page, setPage] = useState(1)
  const [catalogMinHeight, setCatalogMinHeight] = useState(0)
  const catalogContentRef = useRef<HTMLDivElement | null>(null)
  const deferredFilters = useDeferredValue(filters)

  function handleFiltersChange(nextFilters: typeof defaultExcursionFilters) {
    runViewTransition(() => {
      setFilters(nextFilters)
      setPage(1)
    })
  }

  const filteredExcursions = filterExcursions(excursions, deferredFilters)
  const totalPages = Math.max(1, Math.ceil(filteredExcursions.length / pageSize))
  const safePage = Math.min(page, totalPages)
  const paginatedExcursions = paginateItems(
    filteredExcursions,
    safePage,
    pageSize,
  )
  const districts = useMemo(
    () => [...new Set(excursions.map((excursion) => excursion.district))],
    [excursions],
  )
  const querySuggestion = getExcursionQuerySuggestion(excursions, filters.query)
  const showReset = hasActiveExcursionFilters(filters)
  const catalogMotionKey = [
    safePage,
    deferredFilters.query,
    deferredFilters.theme,
    deferredFilters.district,
    deferredFilters.difficulty,
    deferredFilters.maxDistance,
    deferredFilters.maxDuration,
    deferredFilters.sortBy,
  ].join('|')

  useEffect(() => {
    if (typeof window === 'undefined') {
      return
    }

    const node = catalogContentRef.current

    if (!node) {
      return
    }

    const compactViewport = window.matchMedia('(max-width: 720px)')

    function syncHeight(nextHeight: number) {
      if (compactViewport.matches) {
        setCatalogMinHeight(0)
        return
      }

      setCatalogMinHeight((currentHeight) =>
        Math.max(currentHeight, Math.ceil(nextHeight)),
      )
    }

    syncHeight(node.getBoundingClientRect().height)

    if (typeof ResizeObserver === 'undefined') {
      return
    }

    const resizeObserver = new ResizeObserver((entries) => {
      const entry = entries[0]

      if (entry) {
        syncHeight(entry.contentRect.height)
      }
    })

    const handleViewportChange = () => {
      if (compactViewport.matches) {
        setCatalogMinHeight(0)
        return
      }

      syncHeight(node.getBoundingClientRect().height)
    }

    resizeObserver.observe(node)
    compactViewport.addEventListener('change', handleViewportChange)

    return () => {
      resizeObserver.disconnect()
      compactViewport.removeEventListener('change', handleViewportChange)
    }
  }, [catalogMotionKey])

  return (
    <section className="page">
      <article className="page-banner">
        <div>
          <p className="eyebrow">Экскурсии</p>
          <h1 className="page-title">Каталог маршрутов по Самаре</h1>
          <p className="page-description">
            Выберите прогулку по времени, длине маршрута, теме и количеству
            точек.
          </p>
        </div>
        <div className="page-banner__stats">
          <div className="hero-stat">
            <span className="hero-stat__value">{excursions.length}</span>
            <span className="hero-stat__label">Всего маршрутов</span>
          </div>
          <div className="hero-stat">
            <span className="hero-stat__value">{filteredExcursions.length}</span>
            <span className="hero-stat__label">Подходят сейчас</span>
          </div>
        </div>
      </article>

      <ExcursionFiltersPanel
        districts={districts}
        filters={filters}
        onChange={handleFiltersChange}
        onReset={() => {
          runViewTransition(() => {
            setFilters(defaultExcursionFilters)
            setPage(1)
          })
        }}
        querySuggestion={querySuggestion}
        resultCount={filteredExcursions.length}
        showReset={showReset}
      />

      {loading ? (
        <section className="status-card">
          <h2 className="status-card__title">Загружаем экскурсии</h2>
          <p className="status-card__text">Подождите немного, каталог загружается.</p>
        </section>
      ) : error ? (
        <section className="status-card">
          <h2 className="status-card__title">Не удалось показать каталог</h2>
          <p className="status-card__text">{error}</p>
        </section>
      ) : (
        <div className="catalog-stage">
          <div
            className="catalog-stage__content"
            key={catalogMotionKey}
            ref={catalogContentRef}
            style={
              catalogMinHeight
                ? { minHeight: `${catalogMinHeight}px` }
                : undefined
            }
          >
            <ExcursionCatalog
              emptyDescription="Попробуйте изменить запрос или параметры фильтрации."
              emptyTitle="Экскурсии не найдены"
              excursions={paginatedExcursions}
            />
          </div>
          <Pagination
            onChange={(nextPage) => {
              runViewTransition(() => {
                setPage(nextPage)
              })
            }}
            page={safePage}
            totalPages={totalPages}
          />
        </div>
      )}
    </section>
  )
}
