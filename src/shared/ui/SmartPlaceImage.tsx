import { useMemo } from 'react'
import type { ImgHTMLAttributes } from 'react'

import type { GeoPoint, PointCategory } from '@/entities/excursion/model/types'
import { buildStaticPlaceImageUrl } from '@/entities/place/lib/place-images'
import { buildPlacePlaceholderImage } from '@/shared/lib/placeholder-images'
import { ResilientImage } from '@/shared/ui/ResilientImage'

interface SmartPlaceImageProps extends Omit<ImgHTMLAttributes<HTMLImageElement>, 'src'> {
  category: PointCategory
  coordinates: GeoPoint
  fallbackSrcs?: string[]
  src?: string | null
}

export function SmartPlaceImage({
  category,
  coordinates,
  fallbackSrcs = [],
  src,
  ...props
}: SmartPlaceImageProps) {
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

  return (
    <ResilientImage
      {...props}
      fallbackSrcs={allFallbacks}
      placeholderSrc={placeholderImage}
      src={src ?? undefined}
    />
  )
}
