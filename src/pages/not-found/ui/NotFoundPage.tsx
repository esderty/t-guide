import { Link } from 'react-router-dom'

import { appRoutes } from '@/shared/config/routes'

export function NotFoundPage() {
  return (
    <section className="not-found">
      <p className="eyebrow">404</p>
      <h1 className="not-found__title">Страница не найдена</h1>
      <p className="not-found__description">
        Такой страницы сейчас нет. Можно вернуться на главную и продолжить поиск nearby routes.
      </p>
      <Link className="button button--primary" to={appRoutes.home}>
        Вернуться на главную
      </Link>
    </section>
  )
}
