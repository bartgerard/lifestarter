package be.gerard.starter.service;

import be.gerard.starter.model.Vip;
import be.gerard.starter.repository.VipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * VipService
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Service
@RequiredArgsConstructor
public class VipService {

    private final VipRepository vipRepository;

    public List<String> findRoles(
            final String firstName,
            final String lastName
    ) {
        Assert.hasText(firstName, "firstName is invalid [null]");
        Assert.hasText(lastName, "lastName is invalid [null]");

        return vipRepository.findByFirstNameLikeAndLastNameLike(
                firstName.toLowerCase(),
                lastName.toLowerCase()
        )
                .map(Vip::getRoles)
                .orElseGet(Collections::emptyList);
    }
    
    //@PostConstruct
    public void init() {
        vipRepository.save(new Vip(
                "bart",
                "gerard",
                Arrays.asList(
                        "guest.main",
                        "guest.plus1"
                )
        ));
    }

}
