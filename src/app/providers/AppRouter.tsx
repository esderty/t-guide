import { Suspense, lazy } from 'react'
import { Route, Routes } from 'react-router-dom'

import { appRoutes } from '@/shared/config/routes'

const HomePage = lazy(async () =>
  import('@/pages/home/ui/HomePage').then((module) => ({
    default: module.HomePage,
  })),
)
const ExcursionsPage = lazy(async () =>
  import('@/pages/excursions/ui/ExcursionsPage').then((module) => ({
    default: module.ExcursionsPage,
  })),
)
const ExcursionPage = lazy(async () =>
  import('@/pages/excursion/ui/ExcursionPage').then((module) => ({
    default: module.ExcursionPage,
  })),
)
const SignInPage = lazy(async () =>
  import('@/pages/sign-in/ui/SignInPage').then((module) => ({
    default: module.SignInPage,
  })),
)
const ProfilePage = lazy(async () =>
  import('@/pages/profile/ui/ProfilePage').then((module) => ({
    default: module.ProfilePage,
  })),
)
const NotFoundPage = lazy(async () =>
  import('@/pages/not-found/ui/NotFoundPage').then((module) => ({
    default: module.NotFoundPage,
  })),
)

export function AppRouter() {
  return (
    <Suspense fallback={null}>
      <Routes>
        <Route path={appRoutes.home} element={<HomePage />} />
        <Route path={appRoutes.excursions} element={<ExcursionsPage />} />
        <Route path={appRoutes.excursion()} element={<ExcursionPage />} />
        <Route path={appRoutes.signIn} element={<SignInPage />} />
        <Route path={appRoutes.profile} element={<ProfilePage />} />
        <Route path={appRoutes.savedRoutes} element={<ProfilePage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </Suspense>
  )
}
