package be.gerard.starter.web;

import be.gerard.starter.model.Guest;
import be.gerard.starter.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public List<Guest> guests() {
        return guestService.findAll();
    }

}
