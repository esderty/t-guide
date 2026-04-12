import { createContext } from 'react'

import type {
  RequestPasswordResetRequestDto,
  RegisterRequestDto,
  SessionDto,
  SignInRequestDto,
  UpdateProfileRequestDto,
  UserProfileDto,
} from '@/shared/api/contracts'

export interface AuthContextValue {
  isLoading: boolean
  session: SessionDto | null
  requestPasswordReset: (payload: RequestPasswordResetRequestDto) => Promise<void>
  register: (payload: RegisterRequestDto) => Promise<SessionDto>
  signIn: (payload: SignInRequestDto) => Promise<SessionDto>
  signOut: () => Promise<void>
  updateProfile: (payload: UpdateProfileRequestDto) => Promise<UserProfileDto>
}

export const AuthContext = createContext<AuthContextValue | null>(null)
