import type { Excursion } from '@/entities/excursion/model/types'
import { ExcursionCard } from '@/entities/excursion/ui/ExcursionCard'
import './ExcursionCatalog.css'

interface ExcursionCatalogProps {
  excursions: Excursion[]
  emptyTitle?: string
  emptyDescription?: string
}

export function ExcursionCatalog({
  excursions,
  emptyTitle = 'Маршруты пока не найдены',
  emptyDescription = 'Попробуйте сменить категорию или время прогулки.',
}: ExcursionCatalogProps) {
  if (!excursions.length) {
    return (
      <section className="status-card">
        <h3 className="status-card__title">{emptyTitle}</h3>
        <p className="status-card__text">{emptyDescription}</p>
      </section>
    )
  }

  return (
    <div className="catalog">
      {excursions.map((excursion) => (
        <ExcursionCard excursion={excursion} key={excursion.slug} />
      ))}
    </div>
  )
}
