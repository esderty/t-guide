import { Link } from 'react-router-dom'

import { appRoutes } from '@/shared/config/routes'

export function NotFoundPage() {
  return (
    <section className="not-found">
      <h1 className="not-found__title">Страница не найдена</h1>
      <p className="not-found__description">
        Такой страницы сейчас нет. Попробуйте вернуться на главную или открыть
        каталог экскурсий.
      </p>
      <div className="section-actions">
        <Link className="button button--secondary" to={appRoutes.home}>
          Главная
        </Link>
        <Link className="button button--primary" to={appRoutes.excursions}>
          Экскурсии
        </Link>
      </div>
    </section>
  )
}
