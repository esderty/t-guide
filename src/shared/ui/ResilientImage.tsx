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
  const sources = [src, ...fallbackSrcs].filter(
    (value, index, source): value is string =>
      typeof value === 'string' && value.length > 0 && source.indexOf(value) === index,
  )
  const initialSrc = sources[0]

  if (!initialSrc) {
    return null
  }

  return (
    <img
      {...props}
      data-fallback-index="0"
      onError={(event) => {
        const image = event.currentTarget
        const currentIndex = Number(image.dataset.fallbackIndex ?? '0')
        const nextIndex = currentIndex + 1

        if (nextIndex < sources.length) {
          image.dataset.fallbackIndex = String(nextIndex)
          image.src = sources[nextIndex]
          return
        }

        onError?.(event)
      }}
      src={initialSrc}
    />
  )
}