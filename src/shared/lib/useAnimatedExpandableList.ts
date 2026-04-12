import {
  useCallback,
  useEffect,
  useLayoutEffect,
  useRef,
  useState,
} from 'react'

const collapseExpandAnimationDurationMs = 420

type ExpandableAnimationState = 'idle' | 'expanding' | 'collapsing'

export function useAnimatedExpandableList<T>(
  items: T[],
  initialVisibleCount: number,
) {
  const containerRef = useRef<HTMLDivElement | null>(null)
  const animationFrameRef = useRef<number | null>(null)
  const animationTimeoutRef = useRef<number | null>(null)
  const [isExpanded, setIsExpanded] = useState(false)
  const [animationState, setAnimationState] =
    useState<ExpandableAnimationState>('idle')

  const collapsedVisibleCount = Math.min(initialVisibleCount, items.length)
  const canToggle = items.length > initialVisibleCount
  const shouldRenderAllItems = isExpanded || animationState !== 'idle'
  const visibleItems = shouldRenderAllItems
    ? items
    : items.slice(0, initialVisibleCount)

  const clearScheduledAnimation = useCallback(() => {
    if (animationFrameRef.current !== null) {
      window.cancelAnimationFrame(animationFrameRef.current)
      animationFrameRef.current = null
    }

    if (animationTimeoutRef.current !== null) {
      window.clearTimeout(animationTimeoutRef.current)
      animationTimeoutRef.current = null
    }
  }, [])

  useEffect(
    () => () => {
      clearScheduledAnimation()
    },
    [clearScheduledAnimation],
  )

  useLayoutEffect(() => {
    const container = containerRef.current

    if (!container) {
      return
    }

    const children = Array.from(container.children) as HTMLElement[]
    const collapsedHeight = getExpandableContentHeight(
      children,
      collapsedVisibleCount,
    )

    if (animationState === 'idle') {
      clearScheduledAnimation()
      container.style.willChange = ''
      container.style.opacity = '1'
      container.style.overflow = isExpanded ? 'visible' : 'hidden'
      container.style.maxHeight =
        isExpanded || collapsedHeight === 0 ? 'none' : `${collapsedHeight}px`
      return
    }

    if (!children.length || collapsedVisibleCount === 0) {
      clearScheduledAnimation()
      setAnimationState('idle')
      return
    }

    const expandedHeight = getExpandableContentHeight(children, children.length)
    const prefersReducedMotion = window.matchMedia(
      '(prefers-reduced-motion: reduce)',
    ).matches

    if (prefersReducedMotion) {
      clearScheduledAnimation()
      setIsExpanded(animationState === 'expanding')
      setAnimationState('idle')
      return
    }

    const startHeight =
      animationState === 'expanding' ? collapsedHeight : expandedHeight
    const targetHeight =
      animationState === 'expanding' ? expandedHeight : collapsedHeight

    container.style.overflow = 'hidden'
    container.style.willChange = 'max-height, opacity'
    container.style.maxHeight = `${startHeight}px`
    container.style.opacity = animationState === 'expanding' ? '0.985' : '1'

    // Force layout before starting the CSS transition.
    container.getBoundingClientRect()

    const finishAnimation = () => {
      clearScheduledAnimation()
      setIsExpanded(animationState === 'expanding')
      setAnimationState('idle')
    }

    animationFrameRef.current = window.requestAnimationFrame(() => {
      container.style.maxHeight = `${targetHeight}px`
      container.style.opacity = animationState === 'expanding' ? '1' : '0.98'
    })

    animationTimeoutRef.current = window.setTimeout(
      finishAnimation,
      collapseExpandAnimationDurationMs + 40,
    )

    return () => {
      clearScheduledAnimation()
    }
  }, [
    animationState,
    clearScheduledAnimation,
    collapsedVisibleCount,
    isExpanded,
    items.length,
  ])

  const toggle = useCallback(() => {
    if (!canToggle || animationState !== 'idle') {
      return
    }

    setAnimationState(isExpanded ? 'collapsing' : 'expanding')
  }, [animationState, canToggle, isExpanded])

  return {
    animationState,
    canToggle,
    containerRef,
    isExpanded,
    toggle,
    visibleItems,
    isExtraAnimatedItem: (index: number) => index >= collapsedVisibleCount,
  }
}

function getExpandableContentHeight(
  children: HTMLElement[],
  visibleCount: number,
) {
  const lastVisibleChild = children[Math.min(visibleCount, children.length) - 1]

  if (!lastVisibleChild) {
    return 0
  }

  return Math.ceil(lastVisibleChild.offsetTop + lastVisibleChild.offsetHeight)
}
