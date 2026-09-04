import {Component, OnInit} from '@angular/core';
import {RegistrationService} from '../../service/registration.service';
import {WaveService} from '../../service/wave.service';
import {Wave} from '../../model/wave';
import {ConfirmationService} from 'primeng/api';

const MILLISECONDS_PER_DAY = 1000 * 60 * 60 * 24;

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.css'],
  providers: [ConfirmationService]
})
export class InfoComponent implements OnInit {

  nbGuests = 0;
  currentWave: Wave | null = null;
  daysLeft = 0;

  constructor(
    private registrationService: RegistrationService,
    private waveService: WaveService,
    private confirmationService: ConfirmationService
  ) {
  }

  ngOnInit() {
    this.registrationService.statistics()
      .subscribe(statistics => this.nbGuests = statistics.totalGuests);

    this.waveService.currentWave()
      .subscribe(currentWave => {
        this.currentWave = currentWave;

        if (!currentWave) {
          this.daysLeft = 0;
          return;
        }

        const millisecondsLeft = new Date(currentWave.deadline).getTime() - new Date().getTime();
        this.daysLeft = Math.max(Math.ceil(millisecondsLeft / MILLISECONDS_PER_DAY), 0);
      });
  }

  reminder() {
    this.confirmationService.confirm({
      message: 'Vergeet je niet te registreren!',
      header: 'Herinnering'
    });
  }

}
