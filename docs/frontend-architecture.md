# Frontend Architecture

## Target stack

- `React + TypeScript + Vite`
- `React Router` for navigation
- feature-sliced layers for predictable growth
- mocked repository now, REST API integration next

## Layers

- `app/` application shell, router, global styles
- `pages/` route-level screens
- `widgets/` assembled screen blocks
- `features/` user actions and interactive parts
- `entities/` domain models, repositories and small domain UI
- `shared/` reusable config, API helpers and utilities

## Data flow

1. `pages` request data from `entities/*/api`
2. repository returns mocked data now
3. later the same repository will call backend through `shared/api/http.ts`
4. `widgets` and `features` receive typed props only

## Current routes

- `/` excursion list
- `/excursions/:slug` excursion route page

## Why this shape fits the project

- easy to replace mocks with Spring Boot API without rewriting UI
- route map, place card and audio guide are isolated features
- team can work in parallel on pages, features and domain entities
- the structure is already ready for Leaflet, localization and offline caching

## Next milestones

1. connect backend endpoints in `entities/excursion/api/excursions.repository.ts`
2. swap schematic route preview with `react-leaflet`
3. add real HTML5 audio playback with MP3 URLs from backend
4. add filters, user geolocation and progress tracking
