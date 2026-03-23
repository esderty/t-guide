import { BrowserRouter, Link, NavLink } from 'react-router-dom'

import { AppRouter } from '@/app/providers/AppRouter'
import { appRoutes } from '@/shared/config/routes'

function App() {
  return (
    <BrowserRouter>
      <div className="app-shell">
        <header className="topbar">
          <Link className="brand" to={appRoutes.home}>
            <span className="brand__eyebrow">Samara</span>
            <span className="brand__title">Аудиогид</span>
          </Link>

          <nav className="topbar__nav">
            <NavLink
              className={({ isActive }) =>
                `topbar__link${isActive ? ' topbar__link--active' : ''}`
              }
              end
              to={appRoutes.home}
            >
              Главная
            </NavLink>
            <NavLink
              className={({ isActive }) =>
                `topbar__link${isActive ? ' topbar__link--active' : ''}`
              }
              to={appRoutes.excursions}
            >
              Экскурсии
            </NavLink>
          </nav>
        </header>

        <main className="app-shell__content">
          <AppRouter />
        </main>
      </div>
    </BrowserRouter>
  )
}

export default App
