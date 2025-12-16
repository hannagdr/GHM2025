package hu.nye.ghm.service.impl;

import hu.nye.ghm.domain.RaffleUser;
import hu.nye.ghm.repository.RaffleUserRepository;
import hu.nye.ghm.service.RafflePlayerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RafflePlayerServiceImpl implements RafflePlayerService {
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

    @Override
    @Transactional
    public boolean registerNewUser(RaffleUser user) {
        if (isUserNameOrEmailAlreadyRegistered(user.getUserName(), user.getEmailAddress())) {
            return false;
        } else {
            repository.save(user);
            return true;
        }
    }

    private boolean isUserNameOrEmailAlreadyRegistered(String userName, String emailAddress) {
        return repository.existsRaffleUserByUserNameOrEmailAddress(userName, emailAddress);
    }
}
