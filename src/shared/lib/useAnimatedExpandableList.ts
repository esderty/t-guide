import {
  useCallback,
  useLayoutEffect,
  useRef,
  useState,
} from 'react'

export function useAnimatedExpandableList<T>(
  items: T[],
  initialVisibleCount: number,
) {
  const containerRef = useRef<HTMLDivElement | null>(null)
  const [isExpanded, setIsExpanded] = useState(false)

  const canToggle = items.length > initialVisibleCount
  const visibleCount = isExpanded
    ? items.length
    : Math.min(initialVisibleCount, items.length)
  const visibleItems = isExpanded
    ? items
    : items.slice(0, initialVisibleCount)

  useLayoutEffect(() => {
    const container = containerRef.current

    if (!container) {
      return
    }

    const children = Array.from(container.children) as HTMLElement[]

    if (!children.length || visibleCount === 0) {
      container.style.removeProperty('--profile-expandable-max-height')
      return
    }

    const lastVisibleChild = children[Math.min(visibleCount, children.length) - 1]

    if (!lastVisibleChild) {
      container.style.removeProperty('--profile-expandable-max-height')
      return
    }

    container.style.setProperty(
      '--profile-expandable-max-height',
      `${Math.ceil(lastVisibleChild.offsetTop + lastVisibleChild.offsetHeight)}px`,
    )
  }, [items.length, visibleCount])

  const toggle = useCallback(() => {
    if (!canToggle) {
      return
    }

    setIsExpanded((current) => !current)
  }, [canToggle])

  return {
    canToggle,
    containerRef,
    isExpanded,
    toggle,
    visibleCount,
    visibleItems,
  }
}
