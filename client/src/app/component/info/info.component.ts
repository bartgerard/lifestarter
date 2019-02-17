import {Component, OnInit} from '@angular/core';
import {RegistrationService} from '../../service/registration.service';
import {WaveService} from '../../service/wave.service';
import {Wave} from '../../model/wave';

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.css']
})
export class InfoComponent implements OnInit {

  nbGuests = 0;
  currentWave: Wave;
  daysLeft = 0;

  constructor(
    private registrationService: RegistrationService,
    private waveService: WaveService
  ) {
  }

  ngOnInit() {
    this.registrationService.nbGuests()
      .subscribe(nbGuests => this.nbGuests = nbGuests);

    this.waveService.currentWave()
      .subscribe(currentWave => {
        this.currentWave = currentWave;
        const now = new Date();
        this.daysLeft = Math.max(
          Math.ceil((new Date(currentWave.deadline).getTime() - now.getTime()) / (1000 * 3600 * 24)),
          0
        );
      });
  }

}
