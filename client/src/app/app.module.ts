import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {HttpClientModule} from '@angular/common/http';
import {HeaderComponent} from './component/header/header.component';
import {FooterComponent} from './component/footer/footer.component';
import {AppRoutingModule} from './app-routing.module';
import {ProjectComponent} from './component/project/project.component';
import {GuestComponent} from './component/guest/guest.component';
import {ButtonModule, DropdownModule, InputMaskModule, InputTextModule, ListboxModule} from 'primeng/primeng';
import {FormsModule} from '@angular/forms';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AddressComponent} from './component/address/address.component';
import {AllergyComponent} from './component/allergy/allergy.component';
import {RegistrationComponent} from './component/registration/registration.component';
import {UserComponent} from './component/user/user.component';
import {SeatComponent} from './component/seat/seat.component';
import {ContactInformationComponent} from './component/contact-information/contact-information.component';
import {TranslatePipe} from './pipe/translate.pipe';
import {InfoComponent} from './component/info/info.component';
import { PledgeComponent } from './component/pledge/pledge.component';

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
    UserComponent,
    SeatComponent,
    ContactInformationComponent,
    TranslatePipe,
    InfoComponent,
    PledgeComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    AppRoutingModule,
    FormsModule,
    InputTextModule,
    ButtonModule,
    DropdownModule,
    InputMaskModule,
    ListboxModule
  ],
  providers: [],
  bootstrap: [
    AppComponent
  ]
})
export class AppModule {
}
