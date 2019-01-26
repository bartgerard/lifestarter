package be.gerard.starter.web;

import be.gerard.starter.model.Guest;
import be.gerard.starter.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * GuestRestController
 *
 * @author bartgerard
 * @version v0.0.1
 */
@RestController
@RequestMapping("guests")
@RequiredArgsConstructor
public class GuestRestController {

    private final GuestService guestService;

    @GetMapping
    public Flux<Guest> guests() {
        return guestService.findAll();
    }

    @GetMapping("slow")
    public Flux<Guest> findAllSlowly() {
        return guestService.findAllSlowly();
    }

}
