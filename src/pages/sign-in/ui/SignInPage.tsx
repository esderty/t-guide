import {
  useEffect,
  useRef,
  useState,
  type FormEvent,
} from 'react'
import { Link, useNavigate } from 'react-router-dom'

import { useAuth } from '@/app/providers/useAuth'
import type { SupportedLocale } from '@/entities/excursion/model/types'
import { appRoutes } from '@/shared/config/routes'
import { formatLocaleLabel } from '@/shared/lib/format'
import './SignInPage.css'

type AuthMode = 'sign-in' | 'register' | 'reset'

const localeOptions: SupportedLocale[] = ['ru', 'en', 'de', 'fr', 'es']

export function SignInPage() {
  const navigate = useNavigate()
  const { register, requestPasswordReset, signIn } = useAuth()
  const [mode, setMode] = useState<AuthMode>('sign-in')
  const [name, setName] = useState('')
  const [phone, setPhone] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [language, setLanguage] = useState<SupportedLocale>('ru')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)
  const [isLocaleMenuOpen, setIsLocaleMenuOpen] = useState(false)
  const localeSelectRef = useRef<HTMLDivElement | null>(null)

  const title =
    mode === 'sign-in'
      ? 'Вход в аккаунт'
      : mode === 'register'
        ? 'Регистрация'
        : 'Восстановление доступа'
  const submitLabel =
    mode === 'sign-in'
      ? 'Войти'
      : mode === 'register'
        ? 'Создать профиль'
        : 'Отправить инструкции'
  const modeActionLabel = mode === 'sign-in' ? 'Создать аккаунт' : 'У меня уже есть аккаунт'

  useEffect(() => {
    function handlePointerDown(event: PointerEvent) {
      if (!localeSelectRef.current?.contains(event.target as Node)) {
        setIsLocaleMenuOpen(false)
      }
    }

    window.addEventListener('pointerdown', handlePointerDown)

    return () => {
      window.removeEventListener('pointerdown', handlePointerDown)
    }
  }, [])

  function switchMode(nextMode: AuthMode) {
    setMode(nextMode)
    setError(null)
    setSuccessMessage(null)
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError(null)
    setSuccessMessage(null)
    setIsSubmitting(true)

    try {
      if (mode === 'sign-in') {
        await signIn({
          login: email,
          password,
        })
        navigate(appRoutes.profile)
        return
      }

      if (mode === 'register') {
        await register({
          email,
          language,
          name,
          password,
          phone,
        })
        navigate(appRoutes.profile)
        return
      }

      await requestPasswordReset({
        login: email,
      })
      setSuccessMessage('Инструкции для восстановления отправлены. Проверьте почту или телефон.')
      setMode('sign-in')
    } catch (nextError) {
      setError(nextError instanceof Error ? nextError.message : 'Не удалось выполнить действие.')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <section className="auth-page">
      <div className="auth-page__panel">
        <p className="eyebrow">Аккаунт T Guide</p>
        <h1 className="auth-page__title">{title}</h1>

        <div className="auth-page__mode-switch" role="tablist">
          <button
            className={`auth-page__mode-button${mode === 'sign-in' ? ' auth-page__mode-button--active' : ''}`}
            onClick={() => switchMode('sign-in')}
            type="button"
          >
            Вход
          </button>
          <button
            className={`auth-page__mode-button${mode === 'register' ? ' auth-page__mode-button--active' : ''}`}
            onClick={() => switchMode('register')}
            type="button"
          >
            Регистрация
          </button>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          {mode === 'register' ? (
            <>
              <label className="field">
                <span className="field__label">Имя</span>
                <input
                  className="field__input"
                  onChange={(event) => setName(event.target.value)}
                  placeholder="Анна"
                  required
                  type="text"
                  value={name}
                />
              </label>
              <label className="field">
                <span className="field__label">Телефон</span>
                <input
                  className="field__input"
                  onChange={(event) => setPhone(event.target.value)}
                  placeholder="+7 999 000-00-00"
                  required
                  type="tel"
                  value={phone}
                />
              </label>
            </>
          ) : null}

          <label className="field">
            <span className="field__label">{mode === 'sign-in' ? 'Почта или телефон' : 'Почта'}</span>
            <input
              className="field__input"
              onChange={(event) => setEmail(event.target.value)}
              placeholder={mode === 'sign-in' ? 'name@example.com или +7 999 000-00-00' : 'name@example.com'}
              required
              type={mode === 'sign-in' ? 'text' : 'email'}
              value={email}
            />
          </label>

          {mode !== 'reset' ? (
            <>
              <label className="field">
                <span className="field__label">Пароль</span>
                <input
                  className="field__input"
                  minLength={8}
                  onChange={(event) => setPassword(event.target.value)}
                  placeholder="Минимум 8 символов"
                  required
                  type="password"
                  value={password}
                />
              </label>

              {mode === 'sign-in' ? (
                <div className="auth-form__aux">
                  <button
                    className="auth-page__inline-action"
                    onClick={() => switchMode('reset')}
                    type="button"
                  >
                    Забыли пароль?
                  </button>
                </div>
              ) : null}
            </>
          ) : null}

          {mode === 'register' ? (
            <div className="field auth-select" ref={localeSelectRef}>
              <span className="field__label">Язык аудиогида</span>
              <button
                aria-expanded={isLocaleMenuOpen}
                className="auth-select__trigger"
                onClick={() => setIsLocaleMenuOpen((current) => !current)}
                type="button"
              >
                <span>{formatLocaleLabel(language)}</span>
                <span aria-hidden="true" className="auth-select__chevron" />
              </button>

              <div className={`auth-select__menu${isLocaleMenuOpen ? ' auth-select__menu--open' : ''}`} role="listbox">
                {localeOptions.map((locale) => (
                  <button
                    aria-selected={locale === language}
                    className={`auth-select__option${locale === language ? ' auth-select__option--active' : ''}`}
                    key={locale}
                    onClick={() => {
                      setLanguage(locale)
                      setIsLocaleMenuOpen(false)
                    }}
                    role="option"
                    type="button"
                  >
                    {formatLocaleLabel(locale)}
                  </button>
                ))}
              </div>
            </div>
          ) : null}

          {error ? <p className="auth-page__error">{error}</p> : null}
          {successMessage ? <p className="auth-page__success">{successMessage}</p> : null}

          <button className="button button--primary button--wide" disabled={isSubmitting} type="submit">
            {isSubmitting ? 'Проверяем данные' : submitLabel}
          </button>
        </form>

        {mode === 'reset' ? (
          <div className="auth-page__footer auth-page__footer--center">
            <button
              className="button button--secondary"
              onClick={() => switchMode('sign-in')}
              type="button"
            >
              Вернуться ко входу
            </button>
          </div>
        ) : (
          <div className="auth-page__footer">
            <span>{mode === 'sign-in' ? 'Еще нет профиля?' : 'Профиль уже создан?'}</span>
            <button
              className="button button--secondary"
              onClick={() => switchMode(mode === 'sign-in' ? 'register' : 'sign-in')}
              type="button"
            >
              {modeActionLabel}
            </button>
          </div>
        )}

        <Link className="inline-link" to={appRoutes.home}>
          На главную
        </Link>
      </div>
    </section>
  )
}
