package viet.io.threadsbe.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import viet.io.threadsbe.repository.UserRepository;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public AuthUserDetails loadUserByUsername(String username) {
        viet.io.threadsbe.entity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username."));

        final String authUsername = user.getUsername();
        final UUID authId = user.getId();

        if (Objects.isNull(authUsername) || authUsername.isEmpty()) {
            throw new UsernameNotFoundException("Invalid username.");
        }
        return new AuthUserDetails(authId, authUsername, "");
    }

    public AuthUserDetails loadUserByUserId(UUID userId) {
        viet.io.threadsbe.entity.User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username."));

        final String authUsername = user.getUsername();
        final UUID authId = user.getId();

        if (Objects.isNull(authUsername) || authUsername.isEmpty()) {
            throw new UsernameNotFoundException("Invalid username.");
        }
        return new AuthUserDetails(authId, authUsername, "");
    }
}