export const appRoutes = {
  home: '/',
  excursions: '/excursions',
  excursion: (slug = ':slug') => `/excursions/${slug}`,
}
