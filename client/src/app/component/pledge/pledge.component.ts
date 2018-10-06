import {Component, OnInit} from '@angular/core';
import {PledgeService} from '../../service/pledge.service';
import {Pledge} from '../../model/pledge';

@Component({
  selector: 'app-pledge',
  templateUrl: './pledge.component.html',
  styleUrls: ['./pledge.component.css']
})
export class PledgeComponent implements OnInit {

  pledges: Pledge[];

  constructor(
    private pledgeService: PledgeService
  ) {
  }

  ngOnInit() {
    this.pledgeService.pledges()
      .subscribe(pledges => this.pledges = pledges);
  }

}
