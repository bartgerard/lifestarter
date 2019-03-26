package be.gerard.starter.service;

import be.gerard.starter.model.Bouncer;
import be.gerard.starter.model.Registration;
import be.gerard.starter.model.Vip;
import be.gerard.starter.repository.BouncerRepository;
import be.gerard.starter.repository.RegistrationRepository;
import be.gerard.starter.repository.VipRepository;
import be.gerard.starter.value.Activity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * VipService
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Service
@RequiredArgsConstructor
public class VipService {

    private final BouncerRepository bouncerRepository;
    private final VipRepository vipRepository;
    private final RegistrationRepository registrationRepository;

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
    public void initVips() {
        vipRepository.save(new Vip(
                "bart",
                "gerard",
                Arrays.asList(
                        "guest.main",
                        "guest.plus1"
                )
        ));
    }

    public List<Activity> findActivities(
            final String pledge,
            final String firstFirstName,
            final String firstLastName,
            final String secondFirstName,
            final String secondLastName
    ) {
        Assert.hasText(firstFirstName, "firstName is invalid [null]");
        Assert.hasText(firstLastName, "lastName is invalid [null]");

        final List<Activity> result = Stream.concat(
                bouncerRepository.findByFirstNameLikeAndLastNameLike(
                        firstFirstName.toLowerCase(),
                        firstLastName.toLowerCase()
                )
                        .map(Bouncer::getActivities)
                        .map(List::stream)
                        .orElseGet(Stream::empty),
                StringUtils.hasText(secondFirstName) && StringUtils.hasText(secondLastName)
                        ? bouncerRepository.findByFirstNameLikeAndLastNameLike(
                        secondFirstName.toLowerCase(),
                        secondLastName.toLowerCase()
                )
                        .map(Bouncer::getActivities)
                        .map(List::stream)
                        .orElseGet(Stream::empty)
                        : Stream.empty()
        )
                .distinct()
                .sorted(Comparator.comparing(Enum::ordinal))
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            if ("family".equals(pledge) || nbDinerGuests() < 90) {
                return Arrays.asList(
                        Activity.CEREMONY,
                        Activity.DINNER,
                        Activity.PARTY
                );
            } else {
                return Arrays.asList(
                        Activity.CEREMONY,
                        Activity.PARTY
                );
            }
        } else {
            return result;
        }
    }

    //@PostConstruct
    public void initBouncers() {
        bouncerRepository.save(new Bouncer(
                "bart",
                "gerard",
                Arrays.asList(
                        Activity.values()
                )
        ));
    }

    public long nbDinerGuests() {
        return registrationRepository.findAll()
                .stream()
                .filter(registration -> Objects.nonNull(registration.getActivities())
                        && registration.getActivities().contains(Activity.DINNER)
                )
                .map(Registration::getGuests)
                .mapToInt(List::size)
                .sum();
    }

}
