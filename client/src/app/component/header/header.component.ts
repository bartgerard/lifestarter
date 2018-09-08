import {Component, OnInit} from '@angular/core';
import {EventService} from "../../service/event.service";
import {Info} from "../../model/info";

@Component({
  selector: 'life-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  //lifeUri: string = 'assets/images/life.png';

  ping: Info;

  random: Info;

  constructor(
    private eventService: EventService
  ) {
  }

  ngOnInit() {
    //this.eventService.getPing()
    //  .subscribe(ping => this.ping = ping);

    /*
    let source = new EventSource('http://localhost:8080/random');
    source.addEventListener('message', event => {
      this.random = JSON.parse(event['data']);
    })
    */
  }

}
