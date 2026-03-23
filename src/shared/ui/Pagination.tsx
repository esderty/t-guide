interface PaginationProps {
  page: number
  totalPages: number
  onChange: (page: number) => void
}

export function Pagination({
  page,
  totalPages,
  onChange,
}: PaginationProps) {
  if (totalPages <= 1) {
    return null
  }

  const pages = Array.from({ length: totalPages }, (_, index) => index + 1)

  return (
    <nav aria-label="Навигация по страницам" className="pagination">
      <button
        className="pagination__button"
        disabled={page === 1}
        onClick={() => onChange(page - 1)}
        type="button"
      >
        Назад
      </button>

      <div className="pagination__pages">
        {pages.map((pageNumber) => (
          <button
            className={`pagination__page${pageNumber === page ? ' pagination__page--active' : ''}`}
            key={pageNumber}
            onClick={() => onChange(pageNumber)}
            type="button"
          >
            {pageNumber}
          </button>
        ))}
      </div>

      <button
        className="pagination__button"
        disabled={page === totalPages}
        onClick={() => onChange(page + 1)}
        type="button"
      >
        Вперед
      </button>
    </nav>
  )
}
