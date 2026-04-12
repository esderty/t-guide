import { Route, Routes } from 'react-router-dom'

import { ExcursionPage } from '@/pages/excursion/ui/ExcursionPage'
import { ExcursionsPage } from '@/pages/excursions/ui/ExcursionsPage'
import { HomePage } from '@/pages/home/ui/HomePage'
import { NotFoundPage } from '@/pages/not-found/ui/NotFoundPage'
import { ProfilePage } from '@/pages/profile/ui/ProfilePage'
import { SignInPage } from '@/pages/sign-in/ui/SignInPage'
import { appRoutes } from '@/shared/config/routes'

export function AppRouter() {
  return (
    <Routes>
      <Route path={appRoutes.home} element={<HomePage />} />
      <Route path={appRoutes.excursions} element={<ExcursionsPage />} />
      <Route path={appRoutes.excursion()} element={<ExcursionPage />} />
      <Route path={appRoutes.signIn} element={<SignInPage />} />
      <Route path={appRoutes.profile} element={<ProfilePage />} />
      <Route path={appRoutes.savedRoutes} element={<ProfilePage />} />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  )
}
