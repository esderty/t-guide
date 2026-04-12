import {
  useEffect,
  useRef,
  useState,
  type FormEvent,
} from 'react'
import { Link } from 'react-router-dom'

import { useAuth } from '@/app/providers/useAuth'
import type { Excursion, SupportedLocale } from '@/entities/excursion/model/types'
import { useUserRoutes } from '@/features/user-routes/model/useUserRoutes'
import { useProfileOverview } from '@/shared/api/useProfileOverview'
import { appRoutes } from '@/shared/config/routes'
import {
  formatDistance,
  formatDuration,
  formatLocaleLabel,
  formatStopCount,
  formatTheme,
} from '@/shared/lib/format'
import { useExpandableList } from '@/shared/lib/useExpandableList'
import './ProfilePage.css'

const localeOptions: SupportedLocale[] = ['ru', 'en', 'de', 'fr', 'es']

export function ProfilePage() {
  const { session, updateProfile } = useAuth()
  const {
    personalRoutes,
    removePersonalRoute,
    removeSavedRoute,
    savedRoutes,
    shareRoute,
  } = useUserRoutes()
  const isAuthenticated = Boolean(session?.isAuthenticated && session.profile)
  const { error, isLoading, overview } = useProfileOverview(isAuthenticated)
  const profile = session?.profile ?? overview?.profile ?? null
  const [name, setName] = useState(profile?.name ?? '')
  const [email, setEmail] = useState(profile?.email ?? '')
  const [language, setLanguage] = useState<SupportedLocale>(profile?.language ?? 'ru')
  const [isSaving, setIsSaving] = useState(false)
  const [saveMessage, setSaveMessage] = useState('')
  const [isLocaleMenuOpen, setIsLocaleMenuOpen] = useState(false)
  const localeSelectRef = useRef<HTMLDivElement | null>(null)
  const historyItems = overview?.routeHistory ?? []
  const {
    canToggle: canTogglePersonalRoutes,
    isExpanded: arePersonalRoutesExpanded,
    toggle: togglePersonalRoutes,
    visibleItems: visiblePersonalRoutes,
  } = useExpandableList(personalRoutes, 6)
  const {
    canToggle: canToggleHistoryRoutes,
    isExpanded: areHistoryRoutesExpanded,
    toggle: toggleHistoryRoutes,
    visibleItems: visibleHistoryRoutes,
  } = useExpandableList(historyItems, 3)

  useEffect(() => {
    if (!profile) {
      return
    }

    setName(profile.name)
    setEmail(profile.email)
    setLanguage(profile.language)
  }, [profile])

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

  async function handleProfileSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setIsSaving(true)
    setSaveMessage('')

    try {
      await updateProfile({
        email,
        language,
        name,
      })
      setSaveMessage('Профиль сохранен')
    } catch (nextError) {
      setSaveMessage(nextError instanceof Error ? nextError.message : 'Не удалось сохранить профиль')
    } finally {
      setIsSaving(false)
    }
  }

  if (!isAuthenticated) {
    return (
      <section className="profile-page page-shell">
        <article className="profile-page__hero section-surface">
          <div>
            <p className="eyebrow">Личный кабинет</p>
            <h1 className="profile-page__title">Войдите в профиль</h1>
            <p className="profile-page__text">
              Аккаунт нужен для личных маршрутов, истории прогулок и настроек языка.
            </p>
          </div>
          <Link className="button button--primary" to={appRoutes.signIn}>
            Войти или зарегистрироваться
          </Link>
        </article>
      </section>
    )
  }

  if (isLoading) {
    return <ProfileSkeleton />
  }

  if (error) {
    return (
      <section className="status-card">
        <h1 className="status-card__title">Не удалось открыть профиль</h1>
        <p className="status-card__text">{error}</p>
      </section>
    )
  }

  return (
    <section className="profile-page page-shell">
      <article className="profile-page__hero section-surface">
        <div>
          <p className="eyebrow">Личный кабинет</p>
          <h1 className="profile-page__title">{profile?.name ?? 'Профиль'}</h1>
          <p className="profile-page__text">
            Управляйте языком аудиогида, личными маршрутами и историей прохождения.
          </p>
        </div>
        <Link className="button button--secondary" to={appRoutes.excursions}>
          Открыть маршруты
        </Link>
      </article>

      <section className="profile-page__layout">
        <form className="profile-card profile-form" onSubmit={handleProfileSubmit}>
          <div>
            <p className="eyebrow">Настройки</p>
            <h2 className="profile-card__title">Данные профиля</h2>
          </div>

          <label className="field">
            <span className="field__label">Имя</span>
            <input
              className="field__input"
              onChange={(event) => setName(event.target.value)}
              value={name}
            />
          </label>

          <label className="field">
            <span className="field__label">Почта</span>
            <input
              className="field__input"
              onChange={(event) => setEmail(event.target.value)}
              type="email"
              value={email}
            />
          </label>

          <div className="field" ref={localeSelectRef}>
            <span className="field__label">Язык аудиогида</span>
            <button
              aria-expanded={isLocaleMenuOpen}
              className="profile-select"
              onClick={() => setIsLocaleMenuOpen((isOpen) => !isOpen)}
              type="button"
            >
              <span>{formatLocaleLabel(language)}</span>
              <span aria-hidden="true" className="profile-select__chevron" />
            </button>

            <div
              className={`profile-select__menu${isLocaleMenuOpen ? ' profile-select__menu--open' : ''}`}
              role="listbox"
            >
              {localeOptions.map((locale) => (
                <button
                  aria-selected={locale === language}
                  className={`profile-select__option${locale === language ? ' profile-select__option--active' : ''}`}
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

          {saveMessage ? <p className="profile-form__message">{saveMessage}</p> : null}

          <button className="button button--primary button--wide" disabled={isSaving} type="submit">
            {isSaving ? 'Сохраняем' : 'Сохранить изменения'}
          </button>
        </form>

        <section className={`profile-card profile-card--large profile-card--saved${savedRoutes.length ? '' : ' profile-card--empty-art'}`}>
          <div className="profile-card__header">
            <div>
              <p className="eyebrow">Личные маршруты</p>
              <h2 className="profile-card__title">Сохраненные прогулки</h2>
            </div>
            <span className="chip chip--accent">{savedRoutes.length}</span>
          </div>

          <div className="profile-routes">
            {savedRoutes.length ? (
              savedRoutes.map((route) => (
                <ProfileRouteCard
                  key={route.slug}
                  onRemove={() => removeSavedRoute(route.slug)}
                  onShare={() => void shareRoute(route)}
                  route={route}
                />
              ))
            ) : (
              <p className="profile-card__text">Сохраняйте маршруты из каталога, чтобы быстро возвращаться к ним.</p>
            )}
          </div>
        </section>
      </section>

      <section className={`profile-card profile-card--large profile-card--personal${personalRoutes.length ? '' : ' profile-card--empty-art profile-card--wide-art'}`}>
        <div className="profile-card__header">
          <div>
            <p className="eyebrow">Свои прогулки</p>
            <h2 className="profile-card__title">Пользовательские маршруты</h2>
          </div>
          <span className="chip chip--accent">{personalRoutes.length}</span>
        </div>

        <div className="profile-routes profile-routes--grid">
          {personalRoutes.length ? (
            visiblePersonalRoutes.map((route) => (
              <ProfileRouteCard
                key={route.slug}
                onRemove={() => removePersonalRoute(route.slug)}
                onShare={() => void shareRoute(route)}
                route={route}
              />
            ))
          ) : (
            <p className="profile-card__text">Соберите маршрут из точек рядом на главной странице.</p>
          )}
        </div>

        {canTogglePersonalRoutes ? (
          <div className="profile-card__footer">
            <button
              className="button button--secondary profile-card__toggle"
              onClick={togglePersonalRoutes}
              type="button"
            >
              {arePersonalRoutesExpanded ? 'Скрыть' : 'Показать все'}
            </button>
          </div>
        ) : null}
      </section>

      <section className="profile-card profile-card--large">
        <div className="profile-card__header">
          <div>
            <p className="eyebrow">История</p>
            <h2 className="profile-card__title">Последние маршруты</h2>
          </div>
        </div>

        <div className="profile-history">
          {visibleHistoryRoutes.map((item) => (
            <article className="profile-history__item" key={item.id}>
              <div className="profile-history__route">
                <span className="profile-route__theme">{formatTheme(item.route.theme)}</span>
                <h3 className="profile-history__title">{item.route.title}</h3>
              </div>
              <div className="profile-history__progress" aria-label={`Прогресс ${item.progressPercent}%`}>
                <span style={{ width: `${item.progressPercent}%` }} />
              </div>
              <span className="chip">{item.status === 'completed' ? 'Завершен' : 'В процессе'}</span>
              <Link className="button button--secondary" to={appRoutes.excursion(item.route.slug)}>
                {item.status === 'completed' ? 'Повторить' : 'Продолжить'}
              </Link>
              <button
                className="button button--ghost"
                onClick={() => void shareRoute(item.route)}
                type="button"
              >
                Поделиться
              </button>
            </article>
          ))}
        </div>

        {canToggleHistoryRoutes ? (
          <div className="profile-card__footer">
            <button
              className="button button--secondary profile-card__toggle"
              onClick={toggleHistoryRoutes}
              type="button"
            >
              {areHistoryRoutesExpanded ? 'Скрыть' : 'Показать все'}
            </button>
          </div>
        ) : null}
      </section>
    </section>
  )
}

interface ProfileRouteCardProps {
  route: Excursion
  onRemove: () => void
  onShare: () => void
}

function ProfileRouteCard({ route, onRemove, onShare }: ProfileRouteCardProps) {
  return (
    <article className="profile-route">
      <Link className="profile-route__main" to={appRoutes.excursion(route.slug)}>
        <span className="profile-route__theme">{formatTheme(route.theme)}</span>
        <h3 className="profile-route__title">{route.title}</h3>
        <p className="profile-route__text">{route.tagline}</p>
        <div className="profile-route__meta">
          <span>{formatDuration(route.durationMinutes)}</span>
          <span>{formatDistance(route.distanceKm)}</span>
          <span>{formatStopCount(route.stops.length)}</span>
        </div>
      </Link>

      <div className="profile-route__actions">
        <button className="button button--secondary" onClick={onShare} type="button">
          Поделиться
        </button>
        <button className="button button--ghost" onClick={onRemove} type="button">
          Убрать
        </button>
      </div>
    </article>
  )
}

function ProfileSkeleton() {
  return (
    <section className="profile-page page-shell" aria-label="Загрузка профиля">
      <article className="profile-page__hero section-surface">
        <div className="profile-page__skeleton-copy">
          <span className="profile-page__skeleton profile-page__skeleton--eyebrow" />
          <span className="profile-page__skeleton profile-page__skeleton--title" />
          <span className="profile-page__skeleton profile-page__skeleton--text" />
        </div>
        <span className="profile-page__skeleton profile-page__skeleton--button" />
      </article>

      <section className="profile-page__layout">
        <section className="profile-card profile-form">
          <span className="profile-page__skeleton profile-page__skeleton--eyebrow" />
          <span className="profile-page__skeleton profile-page__skeleton--section-title" />
          <span className="profile-page__skeleton profile-page__skeleton--field" />
          <span className="profile-page__skeleton profile-page__skeleton--field" />
          <span className="profile-page__skeleton profile-page__skeleton--field" />
          <span className="profile-page__skeleton profile-page__skeleton--button profile-page__skeleton--button-wide" />
        </section>

        <section className="profile-card profile-card--large">
          <span className="profile-page__skeleton profile-page__skeleton--eyebrow" />
          <span className="profile-page__skeleton profile-page__skeleton--section-title" />
          <span className="profile-page__skeleton profile-page__skeleton--text" />
          <span className="profile-page__skeleton profile-page__skeleton--route" />
          <span className="profile-page__skeleton profile-page__skeleton--route" />
        </section>
      </section>

      <section className="profile-card profile-card--large">
        <span className="profile-page__skeleton profile-page__skeleton--eyebrow" />
        <span className="profile-page__skeleton profile-page__skeleton--section-title" />
        <span className="profile-page__skeleton profile-page__skeleton--route" />
        <span className="profile-page__skeleton profile-page__skeleton--route" />
      </section>
    </section>
  )
}
