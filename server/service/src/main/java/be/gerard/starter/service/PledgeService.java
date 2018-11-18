package be.gerard.starter.service;

import be.gerard.starter.model.Pledge;
import be.gerard.starter.repository.PledgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * PledgeService
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Service
@RequiredArgsConstructor
public class PledgeService {

    private static final List<Pledge> PLEDGES = Arrays.asList(
            Pledge.builder()
                    .name("early bird friends")
                    .orderId(1)
                    .price(BigDecimal.ZERO)
                    .description("Toegang tot alle festiviteiten.")
                    .content("2x Toegang tot alle festiviteiten")
                    .available(true)
                    .build(),
            Pledge.builder()
                    .name("friends")
                    .orderId(2)
                    .price(BigDecimal.ZERO)
                    .description("Toegang tot alle bijna festiviteiten. Helaas kunnen we niet iedereen uitnodigen op de feestmaaltijd, maar wees welkom op het aansluitende dessertenbuffet!")
                    .content("2x Toegang tot alle bijna festiviteiten")
                    .available(true)
                    .build(),
            Pledge.builder()
                    .name("family")
                    .orderId(3)
                    .price(BigDecimal.ZERO)
                    .description("Toegang tot alle festiviteiten.")
                    .content("2x Toegang tot alle festiviteiten")
                    .available(true)
                    .build(),
            Pledge.builder()
                    .name("ring bearer")
                    .orderId(4)
                    .price(BigDecimal.ZERO)
                    .description("Ringbear :-)")
                    .content("2x Toegang tot alle festiviteiten")
                    .content("1x Toegang tot de ceremonie alwaar je een zeer specifieke taak krijgt toegewezen")
                    .available(true)
                    .build(),
            Pledge.builder()
                    .name("vip")
                    .orderId(5)
                    .price(BigDecimal.valueOf(250))
                    .description("Omvat één geprinte versie van het trouwalbum.")
                    .content("2x Toegang tot alle festiviteiten")
                    .content("1x Trouwalbum (€250)")
                    .available(true)
                    .build(),
            Pledge.builder()
                    .name("vip+")
                    .orderId(6)
                    .price(BigDecimal.valueOf(250))
                    .description("Diamanten zijn voor altijd en zo ook het album bij deze pledge. De fotograaf drukt het finale fotoboek op ultra duurzaam materiaal. Zo kun je onze mooie dag, nog door enkele generaties na ons laten verder beleven.")
                    .content("2x Toegang tot alle festiviteiten")
                    .content("1x Diamanten Trouwalbum (€600)")
                    .available(true)
                    .build(),
            Pledge.builder()
                    .name("yolo")
                    .orderId(7)
                    .price(BigDecimal.valueOf(10000))
                    .description("Hou jij van uitdagingen? Wij ook! Voor deze pledge zal de bruidegom één politiek correct woord naar keuze verwerken in één van zijn speeches. Niet geldig voor merk getinte woorden. Zie hiervoor de commerciële pledge.")
                    .content("2x Toegang tot alle festiviteiten")
                    .content("1x Politiek correct woord naar keuze in speech bruidegom")
                    .available(true)
                    .build(),
            Pledge.builder()
                    .name("commercial")
                    .orderId(8)
                    .price(BigDecimal.valueOf(20000))
                    .description("Misschien heb jij een product waar je graag reclame voor wilt maken. Denk bijvoorbeeld aan het heerlijke bier Valduc. Met deze pledge krijg je een vermelding op deze site of tijdens één van de speeches.")
                    .content("2x Toegang tot alle festiviteiten")
                    .content("1x Vermelding van jouw product")
                    .available(true)
                    .build()
    );

    /*
    pledgeList: Pledge[] = [
            new Pledge(
      'friends',
              0,
              'Toegang tot (bijna) alle festiviteiten. Mogelijk vallen om budgetaire redenen bepaalde onderdelen weg, maar voel jullie daardoor zeker niet minder welkom!',
              [
              '2x Almost full access to the festivities'
              ]
    ),
    new Pledge(
      'family',
              0,
              '',
              [
              'Nx Full access to the festivities (with N > 0)'
              ]
    ),
    new Pledge(
      'ring bearer',
              0,
              'ringbear :-)',
              [
              '2x ',
              '1x '
              ]
    ),
    new Pledge(
      'vip',
              250,
              'This reward includes a print of the wedding book.',
              [
              '2x Full access to the festivities',
              '1x Wedding Book (€250)'
              ]
    ),
    new Pledge(
      'vip+',
              600,
              'Diamonds are forever en zo ook het trouwboek in deze pledge. De fotograaf drukt het finale fotoboek op ultra duurzaam materiaal. Zo kun je onze mooie dag, nog door enkele generaties na ons laten verder beleven.',
              [
              '2x Full access to the festivities',
              '1x Diamond Wedding Book (€600)'
              ]
    ),
    new Pledge(
      'yolo',
              10000,
              'Hou jij van uitdagingen? Wij ook! Voor deze pledge zal de bruidegom één politiek correct woord naar keuze verwerken in één van zijn speeches. Niet geldig voor merk getinte woorden. Zie hiervoor de commerciële pledge.',
              [
              '2x Full access to the festivities',
              '1x Politiek correct woord naar keuze in speech bruidegom'
              ]
    ),
    new Pledge(
      'commercial',
              20000,
              'Misschien heb jij een product waar je graag reclame voor wilt maken. Denk bijvoorbeeld aan het heerlijke bier Valduc. Met deze pledge krijg je een vermelding op deze site en tijdens één van de speeches.',
              [
              '2x Full access to the festivities',
              '1x Vermelding van jouw product'
              ]
    )
  ];
    */

    private final PledgeRepository pledgeRepository;

    public Flux<Pledge> findAll() {
        return Flux.fromIterable(pledgeRepository.findAll());
    }

    @PostConstruct
    public void init() {
        pledgeRepository.saveAll(PLEDGES);
    }

}
