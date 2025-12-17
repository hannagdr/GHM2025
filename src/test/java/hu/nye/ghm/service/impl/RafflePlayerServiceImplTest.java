package hu.nye.ghm.service.impl;

import hu.nye.ghm.domain.RaffleUser;
import hu.nye.ghm.repository.RaffleUserRepository;
import hu.nye.ghm.service.RafflePlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RafflePlayerServiceImplTest {

    private RafflePlayerService rafflePlayerService;
    private RaffleUserRepository raffleUserRepository;

    @BeforeEach
    void setUp() {
        this.raffleUserRepository = mock(RaffleUserRepository.class);
        this.rafflePlayerService = spy(new RafflePlayerServiceImpl(this.raffleUserRepository));
    }

    @Test
    @DisplayName("Load user - RuntimeException")
    void loadUserRuntimeException() {
        when(this.raffleUserRepository.findByUserName(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> this.rafflePlayerService.loadUserByUsername("username"));
    }

    @Test
    @DisplayName("Load user")
    void loadUser() {
        String username = "TU";
        String password = "PW";
        String roleName = "TEST_ROLE";
        Set<String> roles = Set.of(roleName);

        RaffleUser user = spy(RaffleUser.builder()
                .userName(username)
                .password(password)
                .roles(roles)
                .build());
        when(this.raffleUserRepository.findByUserName(eq(username))).thenReturn(Optional.of(user));

        UserDetails userDetails = this.rafflePlayerService.loadUserByUsername(username);

        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertEquals("ROLE_" + roleName, ((SimpleGrantedAuthority) userDetails.getAuthorities().toArray()[0]).getAuthority());
    }

    @Test
    @DisplayName("Register new user")
    void registerNewUser() {
        when(this.raffleUserRepository.existsRaffleUserByUserNameOrEmailAddress(any(), any())).thenReturn(false);

        boolean registered = this.rafflePlayerService.registerNewUser(RaffleUser.builder().build());

        verify(this.raffleUserRepository).save(any());
        assertTrue(registered);
    }

    @Test
    @DisplayName("Register new user - already exists")
    void registerNewUserAlreadyExists() {
        when(this.raffleUserRepository.existsRaffleUserByUserNameOrEmailAddress(any(), any())).thenReturn(true);

        boolean registered = this.rafflePlayerService.registerNewUser(RaffleUser.builder().build());

        verify(this.raffleUserRepository, never()).save(any());
        assertFalse(registered);
    }
}