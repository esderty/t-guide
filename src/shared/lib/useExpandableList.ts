import { useMemo, useState } from 'react'

export function useExpandableList<T>(items: T[], initialVisibleCount: number) {
  const [isExpanded, setIsExpanded] = useState(false)

  const visibleItems = useMemo(
    () => (isExpanded ? items : items.slice(0, initialVisibleCount)),
    [initialVisibleCount, isExpanded, items],
  )

  const canToggle = items.length > initialVisibleCount

  function toggle() {
    if (!canToggle) {
      return
    }

    setIsExpanded((current) => !current)
  }

  return {
    canToggle,
    isExpanded,
    toggle,
    visibleItems,
  }
}
