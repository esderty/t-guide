import {
  useCallback,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react'

import {
  AuthContext,
  type AuthContextValue,
} from '@/app/providers/auth-context'
import { appApi } from '@/shared/api/client'
import type {
  RequestPasswordResetRequestDto,
  RegisterRequestDto,
  SessionDto,
  SignInRequestDto,
  UpdateProfileRequestDto,
} from '@/shared/api/contracts'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<SessionDto | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    let isMounted = true

    appApi
      .getSession()
      .then((nextSession) => {
        if (isMounted) {
          setSession(nextSession)
        }
      })
      .finally(() => {
        if (isMounted) {
          setIsLoading(false)
        }
      })

    return () => {
      isMounted = false
    }
  }, [])

  const signIn = useCallback(async (payload: SignInRequestDto) => {
    const nextSession = await appApi.signIn(payload)
    setSession(nextSession)
    return nextSession
  }, [])

  const requestPasswordReset = useCallback(async (payload: RequestPasswordResetRequestDto) => {
    await appApi.requestPasswordReset(payload)
  }, [])

  const register = useCallback(async (payload: RegisterRequestDto) => {
    const nextSession = await appApi.register(payload)
    setSession(nextSession)
    return nextSession
  }, [])

  const signOut = useCallback(async () => {
    const nextSession = await appApi.signOut()
    setSession(nextSession)
  }, [])

  const updateProfile = useCallback(async (payload: UpdateProfileRequestDto) => {
    const nextProfile = await appApi.updateProfile(payload)
    setSession({
      isAuthenticated: true,
      profile: nextProfile,
    })
    return nextProfile
  }, [])

  const value = useMemo<AuthContextValue>(
    () => ({
      isLoading,
      requestPasswordReset,
      register,
      session,
      signIn,
      signOut,
      updateProfile,
    }),
    [isLoading, requestPasswordReset, register, session, signIn, signOut, updateProfile],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
