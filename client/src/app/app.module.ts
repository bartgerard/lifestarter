import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {HttpClientModule} from "@angular/common/http";
import {HeaderComponent} from './component/header/header.component';
import {FooterComponent} from './component/footer/footer.component';
import {AppRoutingModule} from './app-routing.module';
import {ProjectComponent} from './component/project/project.component';
import {GuestComponent} from './component/guest/guest.component';
import {ButtonModule, DropdownModule, InputTextModule} from "primeng/primeng";
import {FormsModule} from "@angular/forms";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {AddressComponent} from './component/address/address.component';
import {AllergyComponent} from './component/allergy/allergy.component';
import {RegistrationComponent} from './component/registration/registration.component';
import {UserComponent} from './component/user/user.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    ProjectComponent,
    GuestComponent,
    AddressComponent,
    AllergyComponent,
    RegistrationComponent,
    UserComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    AppRoutingModule,
    FormsModule,
    InputTextModule,
    ButtonModule,
    DropdownModule
  ],
  providers: [],
  bootstrap: [
    AppComponent
  ]
})
export class AppModule {
}
