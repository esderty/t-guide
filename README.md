# T Guide

Frontend client for a geo-based audio guide. The app is built as a backend-ready React application: UI screens consume one API contract, while the current repository uses mock data until real endpoints are available.

## Current Scope

- Home page with user geolocation, nearby points, category filtering, radius filtering, search, and map navigation.
- Route catalog with ready-made excursions filtered by route theme and duration.
- Route details page with route map, stop list, selected place card, and audio guide panel.
- Sign-in and registration screens prepared for account-based flows.
- Profile page with editable profile fields, saved routes, and route history.
- Shared API contract for future backend integration.
- Mock API implementation matching the frontend data flow.
- Component-local CSS files placed next to page, widget, entity, and feature components.

## User Roles Covered In UI

- Guest: can use geolocation, view nearby points, open route catalog, start excursions, choose language context, sign in, and register.
- Authenticated user: can view and edit profile, open saved routes, continue route history, and use profile-based navigation.
- Admin: included in the shared role model for backend compatibility. Admin screens are not implemented yet.

## Backend Integration Point

The frontend talks through `src/shared/api/client.ts`.

Mock mode is active when:

```env
VITE_USE_MOCK_API=true
```

or when `VITE_API_URL` is not set.

Production API mode is selected with:

```env
VITE_USE_MOCK_API=false
VITE_API_URL=https://your-api.example.com/api
```

Expected frontend endpoints:

```text
POST /discovery/feed
POST /routes/catalog
POST /routes/:slug
GET  /auth/session
POST /auth/sign-in
POST /auth/register
POST /auth/sign-out
PATCH /profile
GET  /profile/overview
```

## Main Data Contracts

Contracts live in:

```text
src/shared/api/contracts.ts
```

Key DTO groups:

- `SessionDto`, `UserProfileDto`, `UserRole`
- `SignInRequestDto`, `RegisterRequestDto`, `UpdateProfileRequestDto`
- `DiscoveryFeedRequest`, `DiscoveryFeedDto`
- `RouteCatalogRequest`, `RouteDetailsRequest`
- `ProfileOverviewDto`, `RouteHistoryItemDto`

Domain models live in:

```text
src/entities/excursion/model/types.ts
```

Core models:

- `Excursion`
- `RouteStop`
- `NearbyPoint`
- `AudioStory`
- `GeoPoint`

## Project Structure

```text
src/
  app/
    App.tsx
    App.css
    providers/
      AppRouter.tsx
      AuthProvider.tsx
      auth-context.ts
      useAuth.ts
    styles/
      global.css
      tokens.css

  pages/
    home/
    excursions/
    excursion/
    sign-in/
    profile/
    not-found/

  widgets/
    excursion-catalog/
    route-overview/

  features/
    audio-guide/
    place-details/
    route-map/

  entities/
    excursion/
    place/

  shared/
    api/
    config/
    lib/
    ui/
```

## Styling

Global design tokens are stored in:

```text
src/app/styles/tokens.css
```

Only base primitives and reusable utility classes are stored in:

```text
src/app/styles/global.css
```

Component-specific styles are colocated with components:

```text
Component.tsx
Component.css
```

## Scripts

```bash
npm install
npm run dev
npm run lint
npm run build
npx tsc --noEmit
```
