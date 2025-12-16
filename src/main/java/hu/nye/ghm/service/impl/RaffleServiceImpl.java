package hu.nye.ghm.service.impl;

import hu.nye.ghm.domain.Prize;
import hu.nye.ghm.domain.Raffle;
import hu.nye.ghm.domain.RaffleUser;
import hu.nye.ghm.repository.PrizeRepository;
import hu.nye.ghm.repository.RaffleRepository;
import hu.nye.ghm.repository.RaffleUserRepository;
import hu.nye.ghm.service.RaffleService;
import hu.nye.ghm.web.dto.PrizeDTO;
import hu.nye.ghm.web.dto.RaffleDTO;
import hu.nye.ghm.web.dto.RaffleListTableDTO;
import hu.nye.ghm.web.dto.RaffleViewDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RaffleServiceImpl implements RaffleService {
    private final PrizeRepository prizeRepository;
    private final RaffleRepository raffleRepository;
    private final RaffleUserRepository raffleUserRepository;

    @Override
    public List<RaffleListTableDTO> getAllRaffles() {
        Iterable<Raffle> raffles = raffleRepository.findAll();
        List<RaffleListTableDTO> raffleIdNameResponse = new ArrayList<>();

        for (Raffle raffle : raffles) {
            boolean isCanceled = raffle.isClosed() && raffle.getWinner() == null;
            boolean alreadyApplied = isUserAlreadyApplied(raffle);
            raffleIdNameResponse.add(new RaffleListTableDTO(raffle.getId(), raffle.getName(), raffle.isClosed(), isCanceled, alreadyApplied));
        }

        return raffleIdNameResponse;
    }

    @Override
    @Transactional
    public void createOrUpdateRaffle(RaffleDTO raffleDTO) {
        Optional<Prize> prize = prizeRepository.findById(raffleDTO.getPrizeId());
        if (prize.isEmpty()) {
            throw new RuntimeException("Cannot find prize with the received id");
        }
        Raffle raffle;
        if (raffleDTO.getId() == null) {
            raffle = Raffle.builder().name(raffleDTO.getName()).prize(prize.get()).build();
        } else {
            raffle = raffleRepository.findById(raffleDTO.getId())
                    .map(rf -> {
                        rf.setName(raffleDTO.getName());
                        rf.setPrize(prize.get());
                        return rf;
                    })
                    .orElseThrow(() -> new RuntimeException("Raffle cannot be found by id"));
        }
        raffleRepository.save(raffle);
    }

    @Override
    @Transactional
    public void drawRaffle(Long raffleId) {
        Optional<Raffle> raffleOpt = raffleRepository.findById(raffleId);
        if (raffleOpt.isEmpty()) {
            throw new RuntimeException("Cannot find raffle with ID");
        }
        Raffle raffle = raffleOpt.get();
        if (raffle.isClosed()) {
            System.err.println("The raffle is already closed!");
        }

        List<RaffleUser> players = raffle.getPlayers();
        if (!players.isEmpty()) {
            int selectedPlayerIndex = new Random().nextInt(0, players.size());
            raffle.setWinner(players.get(selectedPlayerIndex));
        }

        raffle.setClosed(true);
        raffleRepository.save(raffle);
    }

    @Override
    @Transactional
    public void closeRaffle(Long raffleId) {
        Optional<Raffle> raffleOpt = raffleRepository.findById(raffleId);
        if (raffleOpt.isEmpty()) {
            throw new RuntimeException("Cannot find raffle with ID");
        }
        Raffle raffle = raffleOpt.get();

        raffle.setClosed(true);
        raffleRepository.save(raffle);
    }

    @Override
    public void addPrizeToRaffle(Long raffleId, Long prizeId) {
        throw new RuntimeException("addPrizeToRaffle is not implemented");
    }

    @Override
    public void addNewPrizeToRaffle(Long raffleId, String prizeName) {
        throw new RuntimeException("addNewPrizeToRaffle is not implemented");
    }

    @Override
    public RaffleViewDTO getRaffleViewData(Long id) {
        Optional<Raffle> raffleOpt = raffleRepository.findById(id);
        if (raffleOpt.isEmpty()) {
            throw new RuntimeException("No raffle found with id");
        }
        Raffle raffle = raffleOpt.get();
        Prize prize = raffle.getPrize();

        PrizeDTO prizeDTO = prize == null ? null : PrizeDTO.builder()
                .category(prize.getCategory())
                .name(prize.getName())
                .build();

        List<String> playerNames = raffle.getPlayers().stream().map(RaffleUser::getName).toList();
        boolean alreadyApplied = isUserAlreadyApplied(raffle);

        return RaffleViewDTO.builder()
                .name(raffle.getName())
                .id(raffle.getId())
                .closed(raffle.isClosed())
                .canceled(raffle.isClosed() && raffle.getWinner() == null)
                .prize(prizeDTO)
                .playerNames(playerNames)
                .alreadyApplied(alreadyApplied)
                .winnerUsername(raffle.getWinner() != null ? raffle.getWinner().getUserName() : null)
                .build();
    }

    private boolean isUserAlreadyApplied(Raffle raffle) {
        String currentUserName = getCurrentUserName();
        return raffle.getPlayers().stream()
                .anyMatch(raffleUser -> Objects.equals(currentUserName, raffleUser.getUserName()));
    }

    private String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new RuntimeException("No authentication context found");
        }
        User user = (User) auth.getPrincipal();
        if (user == null) {
            throw new RuntimeException("No authenticated user found");
        }
        return user.getUsername();
    }

    @Override
    @Transactional
    public void applyToRaffle(Long raffleId, String userName) {
        Optional<RaffleUser> raffleUserOpt = raffleUserRepository.findByUserName(userName);
        if (raffleUserOpt.isEmpty()) {
            throw new RuntimeException("User cannot be found");
        }
        RaffleUser raffleUser = raffleUserOpt.get();

        Optional<Raffle> raffleOpt = raffleRepository.findById(raffleId);
        if (raffleOpt.isEmpty()) {
            throw new RuntimeException("Raffle cannot be found");
        }

        Raffle raffle = raffleOpt.get();

        raffle.getPlayers().add(raffleUser);
        raffleRepository.save(raffle);
    }
}
