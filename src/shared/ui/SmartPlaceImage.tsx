import { useEffect, useMemo, useState } from 'react'
import type { ImgHTMLAttributes } from 'react'

import type { GeoPoint, PointCategory } from '@/entities/excursion/model/types'
import { resolvePlacePhoto } from '@/entities/place/lib/place-photo'
import {
  buildStaticPlaceImageUrl,
  isStaticPlaceImageUrl,
} from '@/entities/place/lib/place-images'
import { buildPlacePlaceholderImage } from '@/shared/lib/placeholder-images'
import { ResilientImage } from '@/shared/ui/ResilientImage'

interface SmartPlaceImageProps extends Omit<ImgHTMLAttributes<HTMLImageElement>, 'src'> {
  category: PointCategory
  coordinates: GeoPoint
  fallbackSrcs?: string[]
  src?: string | null
  wikidataId?: string
  wikipediaTitle?: string
}

export function SmartPlaceImage({
  category,
  coordinates,
  fallbackSrcs = [],
  src,
  wikidataId,
  wikipediaTitle,
  ...props
}: SmartPlaceImageProps) {
  const [resolvedPhotoState, setResolvedPhotoState] = useState<{ key: string; url: string | null }>({
    key: '',
    url: null,
  })
  const placeholderImage = useMemo(
    () => buildPlacePlaceholderImage(category),
    [category],
  )
  const staticFallback = useMemo(
    () => buildStaticPlaceImageUrl(coordinates, category, 16),
    [category, coordinates],
  )
  const allFallbacks = useMemo(
    () =>
      [staticFallback, ...fallbackSrcs, '/illustrations/landmark-card.svg'].filter(
        (value, index, source): value is string =>
          typeof value === 'string' && value.length > 0 && source.indexOf(value) === index,
      ),
    [fallbackSrcs, staticFallback],
  )
  const shouldLookupPhoto =
    !src || isStaticPlaceImageUrl(src) || src.includes('/illustrations/') || src.startsWith('data:image/svg+xml')
  const lookupKey = useMemo(
    () => [wikipediaTitle ?? '', wikidataId ?? ''].join('::'),
    [wikidataId, wikipediaTitle],
  )
  const resolvedPhoto = resolvedPhotoState.key === lookupKey ? resolvedPhotoState.url : null

  useEffect(() => {
    let isActive = true

    if (!shouldLookupPhoto || !lookupKey.replaceAll(':', '').trim()) {
      return () => {
        isActive = false
      }
    }

    resolvePlacePhoto({
      wikidataId,
      wikipediaTitle,
    }).then((photoUrl) => {
      if (isActive) {
        setResolvedPhotoState({
          key: lookupKey,
          url: photoUrl,
        })
      }
    })

    return () => {
      isActive = false
    }
  }, [lookupKey, shouldLookupPhoto, wikidataId, wikipediaTitle])

  return (
    <ResilientImage
      {...props}
      fallbackSrcs={allFallbacks}
      placeholderSrc={placeholderImage}
      src={resolvedPhoto ?? src ?? undefined}
    />
  )
}
