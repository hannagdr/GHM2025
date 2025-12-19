package hu.nye.ghm.controller;

import hu.nye.ghm.service.PrizeService;
import hu.nye.ghm.service.RaffleService;
import hu.nye.ghm.web.dto.RaffleDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RaffleControllerTest {
    private RaffleService raffleService;
    private PrizeService prizeService;
    private RaffleController raffleController;

    @BeforeEach
    void setUp() {
        this.raffleService = mock(RaffleService.class);
        this.prizeService = mock(PrizeService.class);
        this.raffleController = spy(new RaffleController(this.raffleService, this.prizeService));
    }

    @Test
    @DisplayName("Raffle Home")
    void raffleHome() {
        Model model = mock(Model.class);
        String raffleHome = this.raffleController.raffleHome(model);

        verify(model).addAttribute(eq("raffles"), any());
        verify(this.raffleService).getAllRaffles();
        assertEquals("home", raffleHome);
    }

    @Test
    @DisplayName("New Raffle")
    void newRaffle() {
        Model model = mock(Model.class);
        String newRaffle = this.raffleController.newRaffle(model);

        verify(model).addAttribute(eq("raffle"), eq(new RaffleDTO()));
        verify(model).addAttribute(eq("prizes"), anyList());

        verify(this.prizeService).getPrizesForComboBox();
        assertEquals("raffle", newRaffle);
    }

    @Test
    @DisplayName("Edit Raffle")
    void editRaffle() {
        Model model = mock(Model.class);
        String newRaffle = this.raffleController.editRaffle(0L, model);

        verify(model).addAttribute(eq("raffle"), any());
        verify(model).addAttribute(eq("prizes"), anyList());

        verify(this.raffleService).getRaffleEditData(any());
        verify(this.prizeService).getPrizesForComboBox();
        assertEquals("raffle", newRaffle);
    }

    @Test
    @DisplayName("View Raffle")
    void viewRaffle() {
        Model model = mock(Model.class);
        String newRaffle = this.raffleController.viewRaffle(0L, model);

        verify(model).addAttribute(eq("raffle"), any());

        verify(this.raffleService).getRaffleViewData(any());
        assertEquals("raffle_view", newRaffle);
    }

    @Test
    @DisplayName("Save Raffle")
    void saveRaffle() {
        RaffleDTO dto = RaffleDTO.builder().name("test").build();
        String saveRaffle = this.raffleController.saveRaffle(dto);

        verify(this.raffleService).createOrUpdateRaffle(eq(dto));
        assertEquals("redirect:/", saveRaffle);
    }

    @Test
    @DisplayName("Apply to Raffle")
    void applyToRaffle() {
        String principalName = "testName";
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(principalName);

        String testRefer = "testRefer";
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader(eq("Referer"))).thenReturn(testRefer);


        String applyToRaffle = this.raffleController.applyToRaffle(0L, principal, httpServletRequest);

        verify(this.raffleService).applyToRaffle(eq(0L), eq(principalName));
        assertEquals("redirect:" + testRefer, applyToRaffle);
    }

    @Test
    @DisplayName("Close Raffle")
    void closeRaffle() {
        String testRefer = "testRefer";
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader(eq("Referer"))).thenReturn(testRefer);

        String closeRaffle = this.raffleController.closeRaffle(0L, httpServletRequest);

        verify(this.raffleService).closeRaffle(eq(0L));
        assertEquals("redirect:" + testRefer, closeRaffle);
    }

    @Test
    @DisplayName("Draw Raffle")
    void drawRaffle() {
        String testRefer = "testRefer";
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader(eq("Referer"))).thenReturn(testRefer);

        String drawRaffle = this.raffleController.drawRaffle(0L, httpServletRequest);

        verify(this.raffleService).drawRaffle(eq(0L));
        assertEquals("redirect:" + testRefer, drawRaffle);
    }
}