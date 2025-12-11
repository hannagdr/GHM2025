package hu.nye.ghm.utility;

import hu.nye.ghm.domain.Prize;
import hu.nye.ghm.domain.Raffle;
import hu.nye.ghm.domain.RaffleUser;
import hu.nye.ghm.repository.PrizeRepository;
import hu.nye.ghm.repository.RaffleRepository;
import hu.nye.ghm.repository.RaffleUserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RaffleTestDataSupplierUtility {
    private final RaffleRepository raffleRepository;
    private final PrizeRepository prizeRepository;
    private final RaffleUserRepository raffleUserRepository;
    private final PasswordEncoder encoder;

    @PostConstruct
    public void setup() {
        deleteAllData();

        Raffle raffleSmartWatch = raffleRepository.save(Raffle.builder().name("Most jött el a Te időd: Csuklódra szabott szerencse!").closed(true).build());
        Raffle raffleConsole = raffleRepository.save(Raffle.builder().name("Lépj szintet a szórakozásban – Tiéd lehet a csúcskonzol!").build());
        Raffle raffleCellphone = raffleRepository.save(Raffle.builder().name("Hív a szerencse: Zsebre vágható csúcstechnika!").build());

        RaffleUser userOne = raffleUserRepository.save(RaffleUser.builder()
                .userName("test.elek")
                .emailAddress("test.elek@mail.eu")
                .name("Test Elek")
                .password(encoder.encode("test01"))
                .build());
        RaffleUser userTwo = raffleUserRepository.save(RaffleUser.builder()
                .userName("test.etel")
                .emailAddress("test.etel@mail.eu")
                .name("Test Etel")
                .password(encoder.encode("test01"))
                .build());
        RaffleUser userThree = raffleUserRepository.save(RaffleUser.builder()
                .userName("test.eugen")
                .emailAddress("test.eugen@mail.eu")
                .name("Test Eugen")
                .password(encoder.encode("test01"))
                .build());
        RaffleUser adminUser = raffleUserRepository.save(RaffleUser.builder()
                .userName("admin")
                .emailAddress("admin@mail.eu")
                .name("Admin Aladár")
                .password(encoder.encode("admin"))
                .roles(Set.of("USER", "ADMIN"))
                .build());

        Prize ps5Pro = prizeRepository.save(Prize.builder().name("Playstation 5 Pro").category("Játékkonzol").build());
        Prize appleIphone17 = prizeRepository.save(Prize.builder().name("Apple iPhone 17 Pro Max 2TB").category("Mobiltelefon").build());
        Prize garminSmartWatch = prizeRepository.save(Prize.builder().name("Garmin fēnix® 51mm AMOLED").category("Okosóra").build());

        raffleSmartWatch.getPlayers().add(userOne);
        raffleSmartWatch.setPrize(garminSmartWatch);

        raffleCellphone.getPlayers().addAll(List.of(userOne, userTwo));
        raffleCellphone.setPrize(appleIphone17);

        raffleConsole.getPlayers().addAll(List.of(userOne, userTwo, userThree));
        raffleConsole.setPrize(ps5Pro);
        raffleRepository.saveAll(List.of(raffleCellphone, raffleConsole, raffleSmartWatch));
    }

    private void deleteAllData() {
        this.raffleRepository.deleteAll();
        this.prizeRepository.deleteAll();
        this.raffleUserRepository.deleteAll();
    }
}
