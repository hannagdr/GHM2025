package hu.nye.ghm.service.impl;

import hu.nye.ghm.domain.RaffleUser;
import hu.nye.ghm.repository.RaffleUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RaffleUserDetailsService implements UserDetailsService {
    private final RaffleUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        RaffleUser raffleUser = repository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User cannot be found with this username: " + username));

        return User.builder()
                .username(raffleUser.getUserName())
                .password(raffleUser.getPassword())
                .roles(raffleUser.getRoles().toArray(new String[0]))
                .build();
    }
}
