import { Route, Routes } from 'react-router-dom'

import { ExcursionPage } from '@/pages/excursion/ui/ExcursionPage'
import { ExcursionsPage } from '@/pages/excursions/ui/ExcursionsPage'
import { HomePage } from '@/pages/home/ui/HomePage'
import { NotFoundPage } from '@/pages/not-found/ui/NotFoundPage'
import { appRoutes } from '@/shared/config/routes'

export function AppRouter() {
  return (
    <Routes>
      <Route path={appRoutes.home} element={<HomePage />} />
      <Route path={appRoutes.excursions} element={<ExcursionsPage />} />
      <Route path={appRoutes.excursion()} element={<ExcursionPage />} />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  )
}
