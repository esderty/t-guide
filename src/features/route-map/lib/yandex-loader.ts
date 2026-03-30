import React from 'react'

type MapComponent = React.ComponentType<Record<string, unknown>>

export interface YandexMapsModules {
  ymaps3: never
  reactify: never
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

export function loadYandexMaps(): Promise<YandexMapsModules> {
  return Promise.reject(new Error('Yandex Maps provider is no longer used in this project.'))
}

export function resetYandexMapsLoader() {}
