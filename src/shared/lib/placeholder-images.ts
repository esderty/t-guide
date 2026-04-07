import type { ExcursionTheme, PointCategory } from '@/entities/excursion/model/types'

interface IllustrationPalette {
  accent: string
  accentSoft: string
  backgroundFrom: string
  backgroundTo: string
  foreground: string
  glow: string
}

const placePalettes: Record<PointCategory, IllustrationPalette> = {
  museum: {
    accent: '#355070',
    accentSoft: '#89a3b5',
    backgroundFrom: '#eef4f8',
    backgroundTo: '#d7e4eb',
    foreground: '#173042',
    glow: '#c7d9e7',
  },
  food: {
    accent: '#d97706',
    accentSoft: '#f4c27a',
    backgroundFrom: '#fff6ea',
    backgroundTo: '#f8e2be',
    foreground: '#5f3404',
    glow: '#ffe5b8',
  },
  park: {
    accent: '#4f772d',
    accentSoft: '#9ec27d',
    backgroundFrom: '#edf7ec',
    backgroundTo: '#d7e8d1',
    foreground: '#1f3b16',
    glow: '#d9efd2',
  },
  entertainment: {
    accent: '#7c3aed',
    accentSoft: '#c4a5ff',
    backgroundFrom: '#f4efff',
    backgroundTo: '#e6dafe',
    foreground: '#372257',
    glow: '#e3d9ff',
  },
  landmark: {
    accent: '#0f4c81',
    accentSoft: '#7eb2dc',
    backgroundFrom: '#edf5fb',
    backgroundTo: '#d7e4ef',
    foreground: '#102a43',
    glow: '#d0e4f4',
  },
}

const routePalettes: Record<ExcursionTheme, IllustrationPalette> = {
  walk: {
    accent: '#0f766e',
    accentSoft: '#8dd3cd',
    backgroundFrom: '#ecfbf8',
    backgroundTo: '#d6efea',
    foreground: '#173042',
    glow: '#d6f6f1',
  },
  food: {
    accent: '#d97706',
    accentSoft: '#f4c27a',
    backgroundFrom: '#fff7ea',
    backgroundTo: '#f9e4c4',
    foreground: '#5f3404',
    glow: '#ffe5bf',
  },
  nature: {
    accent: '#4f772d',
    accentSoft: '#9ec27d',
    backgroundFrom: '#edf7ec',
    backgroundTo: '#d8ead2',
    foreground: '#1f3b16',
    glow: '#d8efd1',
  },
  fun: {
    accent: '#7c3aed',
    accentSoft: '#c4a5ff',
    backgroundFrom: '#f3efff',
    backgroundTo: '#e6dcff',
    foreground: '#372257',
    glow: '#e8ddff',
  },
  mixed: {
    accent: '#0f4c81',
    accentSoft: '#8ab6d6',
    backgroundFrom: '#eef5fa',
    backgroundTo: '#dae7f0',
    foreground: '#173042',
    glow: '#d8e9f5',
  },
}

export function buildPlacePlaceholderImage(category: PointCategory) {
  const palette = placePalettes[category]
  return buildIllustrationDataUrl(palette, 1200, 720)
}

export function buildRoutePlaceholderImage(theme: ExcursionTheme) {
  const palette = routePalettes[theme]
  return buildIllustrationDataUrl(palette, 1400, 840)
}

function buildIllustrationDataUrl(
  palette: IllustrationPalette,
  width: number,
  height: number,
) {
  const svg = `
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ${width} ${height}" width="${width}" height="${height}" fill="none">
      <defs>
        <linearGradient id="bg" x1="0" y1="0" x2="1" y2="1">
          <stop offset="0%" stop-color="${palette.backgroundFrom}"/>
          <stop offset="100%" stop-color="${palette.backgroundTo}"/>
        </linearGradient>
        <linearGradient id="wave" x1="0" y1="0" x2="1" y2="1">
          <stop offset="0%" stop-color="${palette.accentSoft}" stop-opacity="0.9"/>
          <stop offset="100%" stop-color="${palette.accent}" stop-opacity="0.95"/>
        </linearGradient>
      </defs>
      <rect width="${width}" height="${height}" rx="44" fill="url(#bg)"/>
      <circle cx="${width * 0.16}" cy="${height * 0.18}" r="${height * 0.12}" fill="${palette.glow}"/>
      <path d="M0 ${height * 0.68} C ${width * 0.18} ${height * 0.54}, ${width * 0.32} ${height * 0.78}, ${width * 0.5} ${height * 0.68} C ${width * 0.68} ${height * 0.58}, ${width * 0.82} ${height * 0.82}, ${width} ${height * 0.66} V ${height} H 0 Z" fill="url(#wave)"/>
      <path d="M${width * 0.12} ${height * 0.78} H${width * 0.88}" stroke="${palette.foreground}" stroke-opacity="0.12" stroke-width="10" stroke-linecap="round"/>
      <rect x="${width * 0.16}" y="${height * 0.36}" width="${width * 0.12}" height="${height * 0.28}" rx="22" fill="${palette.foreground}" fill-opacity="0.78"/>
      <rect x="${width * 0.38}" y="${height * 0.4}" width="${width * 0.18}" height="${height * 0.22}" rx="18" fill="${palette.accent}" fill-opacity="0.78"/>
      <path d="M${width * 0.62} ${height * 0.62} V${height * 0.32} L${width * 0.72} ${height * 0.18} L${width * 0.82} ${height * 0.32} V${height * 0.62} Z" fill="${palette.accentSoft}" fill-opacity="0.95"/>
      <g fill="${palette.backgroundFrom}" fill-opacity="0.9">
        <rect x="${width * 0.42}" y="${height * 0.45}" width="${width * 0.04}" height="${height * 0.05}" rx="8"/>
        <rect x="${width * 0.5}" y="${height * 0.45}" width="${width * 0.04}" height="${height * 0.05}" rx="8"/>
        <rect x="${width * 0.42}" y="${height * 0.54}" width="${width * 0.04}" height="${height * 0.05}" rx="8"/>
        <rect x="${width * 0.5}" y="${height * 0.54}" width="${width * 0.04}" height="${height * 0.05}" rx="8"/>
      </g>
    </svg>
  `.trim()

  return `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(svg)}`
}
