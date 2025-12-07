package hu.nye.ghm.utility;

import hu.nye.ghm.entity.Prize;
import hu.nye.ghm.entity.Raffle;
import hu.nye.ghm.entity.RaffleUser;
import hu.nye.ghm.repository.PrizeRepository;
import hu.nye.ghm.repository.RaffleRepository;
import hu.nye.ghm.repository.RaffleUserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        Raffle raffleSmartWatch = raffleRepository.save(new Raffle(null, "Most jött el a Te időd: Csuklódra szabott szerencse!", new ArrayList<>(), null));
        Raffle raffleConsole = raffleRepository.save(new Raffle(null, "Lépj szintet a szórakozásban – Tiéd lehet a csúcskonzol!", new ArrayList<>(), null));
        Raffle raffleCellphone = raffleRepository.save(new Raffle(null, "Hív a szerencse: Zsebre vágható csúcstechnika!", new ArrayList<>(), null));

        RaffleUser userOne = raffleUserRepository.save(new RaffleUser(null, "test.elek",
                "test.elek@mail.eu", "Test Elek", encoder.encode("test01"), new ArrayList<>()));
        RaffleUser userTwo = raffleUserRepository.save(new RaffleUser(null, "test.etel",
                "test.etel@mail.eu", "Test Etel", encoder.encode("test01"), new ArrayList<>()));
        RaffleUser userThree = raffleUserRepository.save(new RaffleUser(null, "test.eugen",
                "test.eugen@mail.eu", "Test Eugén", encoder.encode("test01"), new ArrayList<>()));

        Prize ps5Pro = prizeRepository.save(new Prize(null, "Playstation 5 Pro", "Játékkonzol", new ArrayList<>()));
        Prize appleIphone17 = prizeRepository.save(new Prize(null, "Apple iPhone 17 Pro Max 2TB", "Mobiltelefon", new ArrayList<>()));
        Prize garminSmartWatch = prizeRepository.save(new Prize(null, "Garmin fēnix® 51mm AMOLED", "Okosóra", new ArrayList<>()));

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
