import React from 'react'
import ReactDOM from 'react-dom'

import { appMapConfig } from '@/shared/config/map'

type MapComponent = React.ComponentType<Record<string, unknown>>

interface YandexMapsNamespace {
  ready?: Promise<void>
  import?: (moduleName: string) => Promise<Record<string, unknown>>
  getDefaultConfig?: () => {
    setApikeys?: (apikeys: { router?: string }) => void
  }
}

interface ReactifyModule {
  module: (moduleValue: Record<string, unknown>) => Record<string, MapComponent>
  useDefault: <T>(value: T, deps?: unknown[]) => T
}

export interface YandexMapsModules {
  ymaps3: YandexMapsNamespace & {
    route?: (options: {
      type: 'walking'
      points: Array<[number, number]>
      bounds?: boolean
    }) => Promise<Array<{ toRoute?: () => { geometry?: unknown } }>>
  }
  reactify: ReactifyModule
  components: {
    YMap: MapComponent
    YMapDefaultSchemeLayer: MapComponent
    YMapDefaultFeaturesLayer: MapComponent
    YMapFeature: MapComponent
    YMapMarker: MapComponent
    YMapControls: MapComponent
    YMapControl: MapComponent
  }
}

const yandexScriptId = 'yandex-maps-api-v3'
let mapsPromise: Promise<YandexMapsModules> | null = null

export function loadYandexMaps(): Promise<YandexMapsModules> {
  if (mapsPromise) {
    return mapsPromise
  }

  mapsPromise = createMapsPromise()
  return mapsPromise
}

export function resetYandexMapsLoader() {
  mapsPromise = null

  if (typeof document !== 'undefined') {
    document.getElementById(yandexScriptId)?.remove()
  }

  if (typeof window !== 'undefined') {
    delete (
      window as Window & {
        ymaps3?: YandexMapsNamespace
      }
    ).ymaps3
  }
}

async function createMapsPromise(): Promise<YandexMapsModules> {
  if (typeof window === 'undefined') {
    throw new Error('Карта доступна только в браузере.')
  }

  if (!appMapConfig.apiKey) {
    throw new Error('Не найден ключ Yandex Maps API.')
  }

  await ensureScript()

  const ymaps3 = getYmaps3()

  if (!ymaps3?.ready || !ymaps3.import) {
    throw new Error('Не удалось инициализировать Яндекс Карты.')
  }

  await ymaps3.ready

  ymaps3.getDefaultConfig?.().setApikeys?.({
    router: appMapConfig.routerApiKey || appMapConfig.apiKey,
  })

  const reactifyModule = await ymaps3.import('@yandex/ymaps3-reactify')

  const reactify = (
    reactifyModule as {
      reactify: {
        bindTo: (
          react: typeof React,
          reactDom: typeof ReactDOM,
        ) => ReactifyModule
      }
    }
  ).reactify.bindTo(React, ReactDOM)

  const coreModule = reactify.module(
    ymaps3 as unknown as Record<string, unknown>,
  ) as YandexMapsModules['components']

  return {
    ymaps3: ymaps3 as YandexMapsModules['ymaps3'],
    reactify,
    components: {
      YMap: coreModule.YMap,
      YMapDefaultSchemeLayer: coreModule.YMapDefaultSchemeLayer,
      YMapDefaultFeaturesLayer: coreModule.YMapDefaultFeaturesLayer,
      YMapFeature: coreModule.YMapFeature,
      YMapMarker: coreModule.YMapMarker,
      YMapControls: coreModule.YMapControls,
      YMapControl: coreModule.YMapControl,
    },
  }
}

function ensureScript(): Promise<void> {
  const readyNamespace = getYmaps3()

  if (readyNamespace) {
    return Promise.resolve()
  }

  const scriptSources = [
    `https://api-maps.yandex.ru/v3/?apikey=${encodeURIComponent(appMapConfig.apiKey)}&lang=ru_RU`,
    `https://enterprise.api-maps.yandex.ru/v3/?apikey=${encodeURIComponent(appMapConfig.apiKey)}&lang=ru_RU`,
  ]

  return (async () => {
    for (const src of scriptSources) {
      try {
        await loadScriptBySource(src)
        return
      } catch (error) {
        console.warn(`Не удалось загрузить карту по ${src}`, error)
      }
    }

    throw new Error('Не удалось загрузить Яндекс Карты.')
  })()
}

function loadScriptBySource(src: string): Promise<void> {
  return new Promise((resolve, reject) => {
    const existingScript = document.getElementById(
      yandexScriptId,
    ) as HTMLScriptElement | null

    if (existingScript) {
      existingScript.remove()
    }

    const script = document.createElement('script')
    script.id = yandexScriptId
    script.async = true
    script.src = src
    script.onload = () => resolve()
    script.onerror = () =>
      reject(new Error(`Не удалось загрузить Яндекс Карты по адресу ${src}`))

    document.head.append(script)
  })
}

function getYmaps3(): YandexMapsNamespace | undefined {
  return (
    window as Window & {
      ymaps3?: YandexMapsNamespace
    }
  ).ymaps3
}
