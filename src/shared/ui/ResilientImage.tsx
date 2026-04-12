import { useEffect, useMemo, useState } from 'react'
import type { ImgHTMLAttributes } from 'react'

interface ResilientImageProps extends ImgHTMLAttributes<HTMLImageElement> {
  fallbackSrcs?: string[]
  placeholderSrc?: string
}

export function ResilientImage({
  src,
  fallbackSrcs = [],
  placeholderSrc,
  ...props
}: ResilientImageProps) {
  const candidates = useMemo(
    () =>
      [src, ...fallbackSrcs].filter(
        (value, index, source): value is string =>
          typeof value === 'string' && value.length > 0 && source.indexOf(value) === index,
      ),
    [fallbackSrcs, src],
  )
  const fallbackDisplaySrc = useMemo(
    () => placeholderSrc ?? fallbackSrcs[0] ?? src ?? '',
    [fallbackSrcs, placeholderSrc, src],
  )
  const candidateKey = [fallbackDisplaySrc, ...candidates].join('||')
  const [resolvedSrc, setResolvedSrc] = useState(fallbackDisplaySrc)

  useEffect(() => {
    let isActive = true

    async function resolveSource() {
      for (const candidate of candidates) {
        const loaded = await preloadImage(candidate)

        if (!isActive) {
          return
        }

        if (loaded) {
          setResolvedSrc(candidate)
          return
        }
      }

      if (isActive) {
        setResolvedSrc(fallbackDisplaySrc)
      }
    }

    void resolveSource()

    return () => {
      isActive = false
    }
  }, [candidateKey, candidates, fallbackDisplaySrc])

  if (!resolvedSrc) {
    return null
  }

  return <img {...props} src={resolvedSrc} />
}

function preloadImage(src: string) {
  return new Promise<boolean>((resolve) => {
    const image = new Image()
    image.decoding = 'async'
    image.loading = 'eager'
    image.onload = () => resolve(true)
    image.onerror = () => resolve(false)
    image.src = src
  })
}

