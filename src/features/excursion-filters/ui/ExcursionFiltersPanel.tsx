import type { ExcursionFilters } from '@/entities/excursion/lib/excursion-utils'
import type { ExcursionTheme } from '@/entities/excursion/model/types'
import { formatTheme } from '@/shared/lib/format'
import { SearchSuggestInput } from '@/shared/ui/SearchSuggestInput'
import { SelectMenu, type SelectMenuOption } from '@/shared/ui/SelectMenu'

interface ExcursionFiltersPanelProps {
  districts: string[]
  filters: ExcursionFilters
  resultCount: number
  querySuggestion: string | null
  showReset: boolean
  onChange: (nextFilters: ExcursionFilters) => void
  onReset: () => void
}

const themeOptionsRaw: ExcursionTheme[] = [
  'walk',
  'food',
  'nature',
  'fun',
  'mixed',
]

export function ExcursionFiltersPanel({
  districts,
  filters,
  resultCount,
  querySuggestion,
  showReset,
  onChange,
  onReset,
}: ExcursionFiltersPanelProps) {
  const themeOptions: SelectMenuOption<ExcursionFilters['theme']>[] = [
    { value: 'all', label: 'Все категории' },
    ...themeOptionsRaw.map((theme) => ({
      value: theme,
      label: formatTheme(theme),
    })),
  ]

  const districtOptions: SelectMenuOption<ExcursionFilters['district']>[] = [
    { value: 'all', label: 'Любая зона' },
    ...districts.map((district) => ({
      value: district,
      label: district,
    })),
  ]

  const difficultyOptions: SelectMenuOption<ExcursionFilters['difficulty']>[] = [
    { value: 'all', label: 'Любой темп' },
    { value: 'easy', label: 'Легко' },
    { value: 'medium', label: 'Средне' },
    { value: 'hard', label: 'Насыщенно' },
  ]

  const distanceOptions: SelectMenuOption<string>[] = [
    { value: '', label: 'Любая длина' },
    { value: '1.5', label: 'До 1,5 км' },
    { value: '2.5', label: 'До 2,5 км' },
    { value: '4', label: 'До 4 км' },
    { value: '6', label: 'До 6 км' },
  ]

  const durationOptions: SelectMenuOption<string>[] = [
    { value: '', label: 'Любая длительность' },
    { value: '30', label: 'До 30 мин' },
    { value: '45', label: 'До 45 мин' },
    { value: '60', label: 'До 1 часа' },
    { value: '90', label: 'До 1,5 часов' },
    { value: '120', label: 'До 2 часов' },
  ]

  const sortOptions: SelectMenuOption<ExcursionFilters['sortBy']>[] = [
    { value: 'newest', label: 'Сначала новые' },
    { value: 'duration-asc', label: 'Сначала короткие' },
    { value: 'distance-asc', label: 'Сначала близкие' },
    { value: 'stops-desc', label: 'Больше точек' },
  ]

  return (
    <section className="filters-panel">
      <div className="section-heading">
        <div>
          <h2 className="section-title">Поиск и фильтры</h2>
          <p className="section-description">
            Подберите маршрут по названию, типу, времени и длине прогулки.
          </p>
        </div>
      </div>

      <SearchSuggestInput
        label="Поиск"
        onAcceptSuggestion={(suggestion) =>
          onChange({
            ...filters,
            query: suggestion,
          })
        }
        onChange={(query) =>
          onChange({
            ...filters,
            query,
          })
        }
        placeholder="Название маршрута или точки"
        suggestion={querySuggestion}
        value={filters.query}
      />

      <div className="filters-grid">
        <SelectMenu
          label="Категория"
          onChange={(theme) =>
            onChange({
              ...filters,
              theme,
            })
          }
          options={themeOptions}
          value={filters.theme}
        />

        <SelectMenu
          label="Зона"
          onChange={(district) =>
            onChange({
              ...filters,
              district,
            })
          }
          options={districtOptions}
          value={filters.district}
        />

        <SelectMenu
          label="Темп"
          onChange={(difficulty) =>
            onChange({
              ...filters,
              difficulty,
            })
          }
          options={difficultyOptions}
          value={filters.difficulty}
        />

        <SelectMenu
          label="Длина"
          onChange={(distanceValue) =>
            onChange({
              ...filters,
              maxDistance: distanceValue ? Number(distanceValue) : null,
            })
          }
          options={distanceOptions}
          value={String(filters.maxDistance ?? '')}
        />

        <SelectMenu
          label="Время"
          onChange={(durationValue) =>
            onChange({
              ...filters,
              maxDuration: durationValue ? Number(durationValue) : null,
            })
          }
          options={durationOptions}
          value={String(filters.maxDuration ?? '')}
        />

        <SelectMenu
          label="Сортировка"
          onChange={(sortBy) =>
            onChange({
              ...filters,
              sortBy,
            })
          }
          options={sortOptions}
          value={filters.sortBy}
        />
      </div>

      <div className="filters-panel__footer">
        <span className="results-counter">Найдено: {resultCount}</span>
        <div className="filters-panel__reset-slot">
          <button
            className={`button button--secondary filters-panel__reset${showReset ? ' filters-panel__reset--visible' : ''}`}
            onClick={onReset}
            tabIndex={showReset ? 0 : -1}
            type="button"
          >
            Сбросить фильтры
          </button>
        </div>
        <span className="filters-panel__spacer" aria-hidden="true"></span>
      </div>
    </section>
  )
}
