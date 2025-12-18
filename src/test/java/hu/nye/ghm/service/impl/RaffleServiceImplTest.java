package hu.nye.ghm.service.impl;

import hu.nye.ghm.domain.Prize;
import hu.nye.ghm.domain.Raffle;
import hu.nye.ghm.domain.RaffleUser;
import hu.nye.ghm.repository.PrizeRepository;
import hu.nye.ghm.repository.RaffleRepository;
import hu.nye.ghm.repository.RaffleUserRepository;
import hu.nye.ghm.web.dto.RaffleDTO;
import hu.nye.ghm.web.dto.RaffleListTableDTO;
import hu.nye.ghm.web.dto.RaffleViewDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RaffleServiceImplTest {
    private static final String TEST_USERNAME = "TEST_USERNAME";
    private RaffleUserRepository raffleUserRepository;
    private RaffleRepository raffleRepository;
    private PrizeRepository prizeRepository;
    private RaffleServiceImpl raffleService;

    private RaffleUser playerOne;
    private RaffleUser playerTwo;
    private RaffleUser playerThree;

    private Raffle raffleClosed;
    private Raffle raffleDrawn;
    private Raffle raffleOpen;

    @BeforeEach
    void setUp() {
        this.prizeRepository = mock(PrizeRepository.class);
        this.raffleRepository = mock(RaffleRepository.class);
        this.raffleUserRepository = mock(RaffleUserRepository.class);
        this.raffleService = spy(new RaffleServiceImpl(this.prizeRepository, this.raffleRepository, this.raffleUserRepository));
        doReturn(TEST_USERNAME).when(this.raffleService).getCurrentUserName();

        this.playerOne = RaffleUser.builder().userName(TEST_USERNAME).build();
        this.playerTwo = RaffleUser.builder().userName(TEST_USERNAME + "01").build();
        this.playerThree = RaffleUser.builder().userName(TEST_USERNAME + "02").build();

        this.raffleClosed = Raffle.builder().id(1L).closed(true).players(List.of(playerOne, playerTwo, playerThree)).build();
        this.raffleDrawn = Raffle.builder().id(2L).closed(true).prize(new Prize()).players(List.of(playerOne, playerTwo, playerThree)).winner(playerOne).build();
        this.raffleOpen = Raffle.builder().id(3L).closed(false).prize(new Prize()).players(List.of(playerOne, playerTwo, playerThree)).build();

    }

    @Test
    @DisplayName("Get all Raffles")
    void getAllRaffles() {
        when(this.raffleRepository.findAll()).thenReturn(List.of(raffleOpen, raffleClosed, raffleDrawn));
        List<RaffleListTableDTO> allRaffles = this.raffleService.getAllRaffles();

        assertEquals(3, allRaffles.size());
        compareRaffleTableDTO(raffleOpen, allRaffles.get(0));
        compareRaffleTableDTO(raffleClosed, allRaffles.get(1));
        compareRaffleTableDTO(raffleDrawn, allRaffles.get(2));
    }

    @Test
    @DisplayName("Create or Update - no prize")
    void createOrUpdateNoPrize() {
        when(this.prizeRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> this.raffleService.createOrUpdateRaffle(RaffleDTO.builder().build()));
    }

    @Test
    @DisplayName("Create or Update - New Raffle")
    void createOrUpdateNewRaffle() {
        Long prizeId = 0L;
        String prizeName = "testPrize";
        Prize prize = Prize.builder().id(prizeId).name(prizeName).build();

        RaffleDTO raffleDTO = RaffleDTO.builder().prizeId(prizeId).build();
        when(this.prizeRepository.findById(any())).thenReturn(Optional.of(prize));

        this.raffleService.createOrUpdateRaffle(raffleDTO);
        Raffle expected = Raffle.builder().name(raffleDTO.getName()).prize(prize).build();
        verify(this.raffleRepository).save(eq(expected));
    }

    @Test
    @DisplayName("Create or Update - Existing Raffle")
    void createOrUpdateExistingRaffle() {
        Long prizeId = 0L;
        Long raffleId = 1L;
        String prizeName = "testPrize";
        Prize prize = Prize.builder().id(prizeId).name(prizeName).build();

        RaffleDTO raffleDTO = RaffleDTO.builder().id(raffleId).prizeId(prizeId).build();
        Raffle oldRaffle = Raffle.builder().id(raffleId).name("oldname").prize(new Prize()).build();

        when(this.prizeRepository.findById(any())).thenReturn(Optional.of(prize));
        when(this.raffleRepository.findById(eq(raffleId))).thenReturn(Optional.of(oldRaffle));

        this.raffleService.createOrUpdateRaffle(raffleDTO);
        Raffle expected = Raffle.builder()
                .id(raffleId)
                .name(raffleDTO.getName())
                .prize(prize)
                .build();
        verify(this.raffleRepository).save(eq(expected));
    }

    @Test
    @DisplayName("Create or Update - Existing Raffle - Error")
    void createOrUpdateExistingRaffleError() {
        Long prizeId = 0L;
        Long raffleId = 1L;
        String prizeName = "testPrize";
        Prize prize = Prize.builder().id(prizeId).name(prizeName).build();
        RaffleDTO raffleDTO = RaffleDTO.builder().id(raffleId).prizeId(prizeId).build();

        when(this.prizeRepository.findById(any())).thenReturn(Optional.of(prize));
        when(this.raffleRepository.findById(eq(raffleId))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> this.raffleService.createOrUpdateRaffle(raffleDTO));
    }

    @Test
    @DisplayName("Get Raffle view data")
    void getRaffleViewData() {
        for (Raffle raffle : List.of(raffleOpen, raffleDrawn, raffleClosed)) {
            when(this.raffleRepository.findById(eq(raffle.getId()))).thenReturn(Optional.of(raffle));

            RaffleViewDTO raffleViewData = this.raffleService.getRaffleViewData(raffle.getId());
            assertEquals(raffleViewData.getName(), raffle.getName());
            assertEquals(raffleViewData.getId(), raffle.getId());
            assertEquals(raffleViewData.isClosed(), raffle.isClosed());
        }
    }

    @Test
    @DisplayName("Get Raffle view data - error")
    void getRaffleViewDataError() {
        when(this.raffleRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> this.raffleService.getRaffleViewData(1L));
    }

    @Test
    @DisplayName("Apply to Raffle")
    void applyToRaffle() {
        ArrayList<RaffleUser> existingPlayers = spy(new ArrayList<>());
        Raffle raffleOpenNoPlayer = spy(Raffle.builder().id(4L).closed(false).prize(new Prize()).players(existingPlayers).build());

        when(this.raffleUserRepository.findByUserName(any())).thenReturn(Optional.of(playerOne));
        when(this.raffleRepository.findById(any())).thenReturn(Optional.of(raffleOpenNoPlayer));

        this.raffleService.applyToRaffle(0L, "");

        verify(raffleOpenNoPlayer).getPlayers();
        verify(existingPlayers).add(eq(playerOne));
        verify(this.raffleRepository).save(any());
    }

    @Test
    @DisplayName("Apply to Raffle - No raffle")
    void applyToRaffleNoRaffle() {
        when(this.raffleUserRepository.findByUserName(any())).thenReturn(Optional.of(playerOne));
        when(this.raffleRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> this.raffleService.applyToRaffle(0L, ""));
    }

    @Test
    @DisplayName("Apply to Raffle - No player")
    void applyToRaffleNoPlayer() {
        when(this.raffleUserRepository.findByUserName(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> this.raffleService.applyToRaffle(0L, ""));
    }

    @Test
    @DisplayName("Close Raffle")
    void closeRaffle() {
        Raffle testRaffle = spy(Raffle.builder().closed(false).build());
        when(this.raffleRepository.findById(any())).thenReturn(Optional.of(testRaffle));

        this.raffleService.closeRaffle(0L);

        verify(testRaffle).setClosed(eq(true));
        verify(this.raffleRepository).save(eq(testRaffle));
    }

    @Test
    @DisplayName("Close Raffle - not existing")
    void closeRaffleNotExisting() {
        when(this.raffleRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> this.raffleService.closeRaffle(0L));
    }

    @Test
    @DisplayName("Draw Raffle")
    void drawRaffle() {
        Raffle testRaffle = spy(raffleOpen);
        when(this.raffleRepository.findById(any())).thenReturn(Optional.of(testRaffle));

        this.raffleService.drawRaffle(0L);

        verify(testRaffle).setClosed(eq(true));
        verify(testRaffle).setWinner(notNull());
        verify(this.raffleRepository).save(any());
    }

    @Test
    @DisplayName("Draw Raffle - Already closed")
    void drawRaffleAlreadyClosed() {
        Raffle testRaffle = spy(raffleClosed);
        when(this.raffleRepository.findById(any())).thenReturn(Optional.of(testRaffle));

        this.raffleService.drawRaffle(0L);

        verify(this.raffleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Draw Raffle - Empty player list")
    void drawRaffleAlreadyEmptyPlayerList() {
        raffleOpen.setPlayers(new ArrayList<>());
        Raffle testRaffle = spy(raffleOpen);

        when(this.raffleRepository.findById(any())).thenReturn(Optional.of(testRaffle));

        this.raffleService.drawRaffle(0L);

        verify(testRaffle).setClosed(eq(true));
        verify(testRaffle, never()).setWinner(any());
        verify(this.raffleRepository).save(any());
    }

    @Test
    @DisplayName("Draw Raffle - Raffle not found")
    void drawRaffleNotFound() {
        when(this.raffleRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> this.raffleService.drawRaffle(0L));
    }

    private void compareRaffleTableDTO(Raffle raffleOpen, RaffleListTableDTO raffleListTableDTO) {
        assertEquals(raffleOpen.getName(), raffleListTableDTO.name());
        assertEquals(raffleOpen.isClosed(), raffleListTableDTO.closed());
    }

    @Test
    @DisplayName("Edit Raffle - Raffle not found")
    void editRaffleNotFound() {
        when(this.raffleRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> this.raffleService.getRaffleEditData(1L));
    }

    @Test
    @DisplayName("Edit Raffle")
    void editRaffle() {
        long prizeId = 2L;
        long raffleId = 1L;
        String raffleName = "RaffleTest";
        when(this.raffleRepository.findById(eq(raffleId))).thenReturn(Optional.of(Raffle.builder().id(raffleId).name(raffleName).prize(Prize.builder().id(prizeId).build()).build()));

        RaffleDTO raffleEditData = this.raffleService.getRaffleEditData(1L);
        assertEquals(prizeId, raffleEditData.getPrizeId());
        assertEquals(raffleId, raffleEditData.getId());
        assertEquals(raffleName, raffleEditData.getName());
    }
}