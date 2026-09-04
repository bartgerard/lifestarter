import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./campaign/campaign-page').then((m) => m.CampaignPage),
    title: 'Lifestarter',
  },
  {
    path: 'updates',
    loadComponent: () => import('./updates/updates-page').then((m) => m.UpdatesPage),
    title: 'Lifestarter — Updates',
  },
  {
    path: 'registration',
    loadComponent: () =>
      import('./registration/registration-page').then((m) => m.RegistrationPage),
    title: 'Lifestarter — RSVP',
  },
  {
    path: 'special',
    loadComponent: () => import('./special/special-page').then((m) => m.SpecialPage),
    title: 'Lifestarter — The After Special',
  },
  { path: '**', redirectTo: '' },
];
