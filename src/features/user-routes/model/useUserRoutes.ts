import { useContext } from 'react'

import { UserRoutesContext } from '@/features/user-routes/model/user-routes-context'

export function useUserRoutes() {
  const context = useContext(UserRoutesContext)

  if (!context) {
    throw new Error('useUserRoutes must be used inside UserRoutesProvider')
  }

  return context
}
