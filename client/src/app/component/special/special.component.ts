import {Component, OnInit} from '@angular/core';
import {ConfirmationService} from 'primeng/api';

@Component({
  selector: 'app-special',
  templateUrl: './special.component.html',
  styleUrls: ['./special.component.css'],
  providers: [ConfirmationService]
})
export class SpecialComponent implements OnInit {

  checked = false;

  constructor(
    private confirmationService: ConfirmationService
  ) {
  }

  ngOnInit(): void {
  }

  accept() {
    this.confirmationService.confirm({
      header: 'Finale stap',
      message: 'Stuur vlug een berichtje naar Bart en/of Kim!'
    });
  }

}
