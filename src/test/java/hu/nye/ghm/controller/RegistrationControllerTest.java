package hu.nye.ghm.controller;

import hu.nye.ghm.domain.RaffleUser;
import hu.nye.ghm.service.RafflePlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RegistrationControllerTest {
    private static final String ENC_POSTFIX = "_enc";
    private RafflePlayerService rafflePlayerService;
    private PasswordEncoder passwordEncoder;
    private RegistrationController registrationController;

    @BeforeEach
    void setUp() {
        this.rafflePlayerService = mock(RafflePlayerService.class);
        this.passwordEncoder = mock(PasswordEncoder.class);
        when(this.passwordEncoder.encode(any())).thenAnswer(ans -> ans.getArguments()[0] + ENC_POSTFIX);

        this.registrationController = spy(new RegistrationController(this.rafflePlayerService, this.passwordEncoder));
    }

    @Test
    @DisplayName("Show registration form")
    void showRegistrationForm() {
        Model model = mock(Model.class);

        String regForm = this.registrationController.showRegistrationForm(model);

        verify(model).addAttribute(eq("raffleUser"), any());
        assertEquals("register", regForm);
    }

    @Test
    @DisplayName("Register user - new user")
    void registerUserNewUser() {
        String testPassword = "testPW";
        RaffleUser user = spy(RaffleUser.builder().password(testPassword).build());
        Model model = mock(Model.class);

        when(this.rafflePlayerService.registerNewUser(any())).thenReturn(true);

        String registerUser = this.registrationController.registerUser(user, model);
        verify(model, never()).addAttribute(any(), any());
        verify(this.passwordEncoder).encode(testPassword);
        verify(user).setPassword(eq(testPassword + ENC_POSTFIX));
        assertEquals("redirect:/login?registration_successful", registerUser);
    }

    @Test
    @DisplayName("Register user - already exists")
    void registerUserAlreadyExists() {
        String testPassword = "testPW";
        RaffleUser user = spy(RaffleUser.builder().password(testPassword).build());
        Model model = mock(Model.class);

        when(this.rafflePlayerService.registerNewUser(any())).thenReturn(false);

        String registerUser = this.registrationController.registerUser(user, model);
        verify(model).addAttribute(eq("error"), any());
        verify(this.passwordEncoder).encode(testPassword);
        verify(user).setPassword(eq(testPassword + ENC_POSTFIX));
        assertEquals("register", registerUser);
    }
}