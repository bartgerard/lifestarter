import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ProjectComponent} from './component/project/project.component';
import {RegistrationComponent} from './component/registration/registration.component';
import {InfoComponent} from './component/info/info.component';

const routes: Routes = [
  {path: '', component: InfoComponent},
  {path: 'project', component: ProjectComponent},
  {path: 'registration', component: RegistrationComponent}
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule {
}
