import { useEffect, useRef, useState } from 'react'
import { BrowserRouter, Link, NavLink } from 'react-router-dom'

import { AuthProvider } from '@/app/providers/AuthProvider'
import { AppRouter } from '@/app/providers/AppRouter'
import { useAuth } from '@/app/providers/useAuth'
import { UserRoutesProvider } from '@/features/user-routes/model/UserRoutesProvider'
import { appRoutes } from '@/shared/config/routes'
import './App.css'

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <UserRoutesBoundary />
      </AuthProvider>
    </BrowserRouter>
  )
}

function UserRoutesBoundary() {
  const { session } = useAuth()
  const storageScope = session?.profile?.id ?? 'guest'

  return (
    <UserRoutesProvider key={storageScope}>
      <AppFrame />
    </UserRoutesProvider>
  )
}

function AppFrame() {
  const { session, signOut } = useAuth()
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const headerRef = useRef<HTMLElement | null>(null)

  useEffect(() => {
    function handlePointerDown(event: PointerEvent) {
      if (!headerRef.current?.contains(event.target as Node)) {
        setIsMenuOpen(false)
      }
    }

    if (isMenuOpen) {
      window.addEventListener('pointerdown', handlePointerDown)
    }

    return () => {
      window.removeEventListener('pointerdown', handlePointerDown)
    }
  }, [isMenuOpen])

  return (
    <div className="app-shell">
      <header className="app-header" ref={headerRef}>
        <div className={`app-header__inner${isMenuOpen ? ' app-header__inner--menu-open' : ''}`}>
          <Link className="app-brand" onClick={() => setIsMenuOpen(false)} to={appRoutes.home}>
            <span className="app-brand__badge">T Guide</span>
            <span className="app-brand__title">Аудиогид, маршруты и точки рядом</span>
          </Link>

          <button
            aria-controls="app-navigation"
            aria-expanded={isMenuOpen}
            aria-label={isMenuOpen ? 'Закрыть меню' : 'Открыть меню'}
            className={`app-header__toggle${isMenuOpen ? ' app-header__toggle--active' : ''}`}
            onClick={() => setIsMenuOpen((current) => !current)}
            type="button"
          >
            <span />
            <span />
            <span />
          </button>

          <div className={`app-header__panel${isMenuOpen ? ' app-header__panel--open' : ''}`}>
            <nav className="app-nav" id="app-navigation">
              <NavLink
                className={({ isActive }) => `app-nav__link${isActive ? ' app-nav__link--active' : ''}`}
                end
                onClick={() => setIsMenuOpen(false)}
                to={appRoutes.home}
              >
                Главная
              </NavLink>
              <NavLink
                className={({ isActive }) => `app-nav__link${isActive ? ' app-nav__link--active' : ''}`}
                onClick={() => setIsMenuOpen(false)}
                to={appRoutes.excursions}
              >
                Маршруты
              </NavLink>
              <NavLink
                className={({ isActive }) => `app-nav__link${isActive ? ' app-nav__link--active' : ''}`}
                onClick={() => setIsMenuOpen(false)}
                to={appRoutes.profile}
              >
                Профиль
              </NavLink>
            </nav>

            <div className="app-header__actions">
              {session?.isAuthenticated && session.profile ? (
                <button
                  className="button button--ghost"
                  onClick={() => {
                    setIsMenuOpen(false)
                    void signOut()
                  }}
                  type="button"
                >
                  Выйти
                </button>
              ) : (
                <Link className="button button--primary" onClick={() => setIsMenuOpen(false)} to={appRoutes.signIn}>
                  Войти
                </Link>
              )}
            </div>
          </div>
        </div>
      </header>

      <main className="app-shell__content">
        <AppRouter />
      </main>
    </div>
  )
}

export default App
