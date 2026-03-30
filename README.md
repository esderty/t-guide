# Global Audio Guide

Geo-first audio guide MVP built with React, TypeScript, Vite, Leaflet, and OpenStreetMap.

The app starts from the user's current location, shows nearby places on a live map, generates ready-to-use routes from those places, and opens a route screen with map navigation, point details, and an audio guide panel.

## What Works Now

- Requests user geolocation on app open.
- Shows nearby places around the user on an OpenStreetMap map.
- Supports category filtering:
  - `All places`
  - `Museums`
  - `Entertainment`
  - `History`
  - `Food`
  - `Nature`
- Supports radius filtering:
  - `1 km`
  - `3 km`
  - `5 km`
- Lets the user cycle through nearby places with previous/next controls.
- Builds dynamic route suggestions from the currently selected nearby places.
- Shows the first 6 routes on the home page and the full list on the routes page.
- Filters routes by theme and duration.
- Opens a route details page with:
  - route map
  - route stops list
  - selected place details
  - audio guide block
- Tries to build a real walking path between route points using OSM routing.
- Falls back safely to straight segments if the routing service is unavailable.
- Uses resilient place images:
  - original image if available
  - OSM static map snapshot if image is broken or missing
  - local placeholder as final fallback

## Current User Flow

1. User opens the app.
2. The app requests geolocation.
3. The map centers on the user.
4. Nearby places are loaded for the selected category and radius.
5. The user changes category or radius if needed.
6. The app generates route suggestions from the visible nearby places.
7. The user opens a route.
8. The route page shows the route map, stops, selected place details, and audio guide UI.

## Route Logic

Routes are generated dynamically from the current discovery context:

- selected nearby category
- selected radius
- user center point
- nearby places returned by OSM

The app generates:

- category-specific routes
- mixed routes with different stop combinations
- short and extended route variants when enough points exist

## Tech Stack

- React
- TypeScript
- Vite
- React Router
- Leaflet
- OpenStreetMap

## Environment

The project currently uses OpenStreetMap by default.

Example `.env.local`:

```env
VITE_MAP_PROVIDER=osm
```

## Getting Started

Install dependencies:

```bash
npm install
```

Run the development server:

```bash
npm run dev
```

Run checks:

```bash
npx tsc --noEmit
npm run lint
npm run build
```

## Project Structure

```text
src/
  app/                App shell, router, global styles
  pages/              Home, routes list, route details, not found
  widgets/            Larger UI blocks such as route overview and catalog
  features/           Map, audio guide, place details, filters
  entities/           Domain models, route generation, nearby places, API helpers
  shared/             Config, formatters, reusable UI, common helpers
```

## Current Limitations

- The audio guide panel is UI-ready, but real generated audio is not connected yet.
- Nearby places depend on OSM data quality in the current location.
- Walking route building depends on external OSM routing availability.
- Backend integration is not connected yet; the current version is frontend-first.

## Next Steps

- Connect backend APIs for nearby places and route generation.
- Add real AI audio narration per place and per language.
- Improve route ranking and route editing.
- Add route completion, ratings, and saved progress.
