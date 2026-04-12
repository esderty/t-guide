export const appRoutes = {
  home: '/',
  excursions: '/excursions',
  excursion: (slug = ':slug') => `/excursions/${slug}`,
  signIn: '/auth/sign-in',
  profile: '/profile',
  savedRoutes: '/profile/routes',
}
