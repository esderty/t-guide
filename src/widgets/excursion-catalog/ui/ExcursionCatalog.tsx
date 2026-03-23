import type { Excursion } from '@/entities/excursion/model/types'
import { ExcursionCard } from '@/entities/excursion/ui/ExcursionCard'

interface ExcursionCatalogProps {
  excursions: Excursion[]
  emptyTitle?: string
  emptyDescription?: string
}

export function ExcursionCatalog({
  excursions,
  emptyTitle = 'Экскурсии пока недоступны',
  emptyDescription = 'Новые маршруты появятся здесь немного позже.',
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
        <ExcursionCard excursion={excursion} key={excursion.id} />
      ))}
    </div>
  )
}
