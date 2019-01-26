package be.gerard.starter.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * GuestRestController
 *
 * @author bartgerard
 * @version v0.0.1
 */
@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserRestController {

    @GetMapping
    public Principal user(
            final Principal user
    ) {
        return user;
    }

    @GetMapping("info")
    public Map<String, Object> users(
            final Principal user
    ) {
        final Map<String, Object> map = new HashMap<>();

        map.put("name", user.getName());
        map.put("roles", AuthorityUtils.authorityListToSet(((Authentication) user).getAuthorities()));

        return map;
    }

}
