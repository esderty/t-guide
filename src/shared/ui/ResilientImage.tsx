import { useMemo, useState } from 'react'
import type { ImgHTMLAttributes } from 'react'

interface ResilientImageProps extends ImgHTMLAttributes<HTMLImageElement> {
  fallbackSrcs?: string[]
}

export function ResilientImage({
  src,
  fallbackSrcs = [],
  onError,
  ...props
}: ResilientImageProps) {
  const sources = useMemo(
    () =>
      [src, ...fallbackSrcs].filter(
        (value, index, source): value is string =>
          typeof value === 'string' && value.length > 0 && source.indexOf(value) === index,
      ),
    [fallbackSrcs, src],
  )
  const sourceKey = sources.join('||')
  const [imageState, setImageState] = useState({ key: sourceKey, index: 0 })

  if (!sources.length) {
    return null
  }

  const activeState = imageState.key === sourceKey ? imageState : { key: sourceKey, index: 0 }
  const activeIndex = Math.min(activeState.index, sources.length - 1)

  return (
    <img
      {...props}
      onError={(event) => {
        const nextIndex = activeIndex + 1

        if (nextIndex < sources.length) {
          setImageState({ key: sourceKey, index: nextIndex })
          return
        }

        onError?.(event)
      }}
      src={sources[activeIndex]}
    />
  )
}