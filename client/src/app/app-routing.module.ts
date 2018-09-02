import {NgModule} from '@angular/core';
import {RouterModule, Routes} from "@angular/router";
import {ProjectComponent} from "./component/project/project.component";
import {RegistrationComponent} from "./component/registration/registration.component";

const routes: Routes = [
  {path: '', component: RegistrationComponent},
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
