package com.taskflow.userservice.service;

import com.taskflow.userservice.dto.AuthResponse;
import com.taskflow.userservice.dto.LoginRequest;
import com.taskflow.userservice.dto.RegisterRequest;
import com.taskflow.userservice.exception.InvalidCredentialsException;
import com.taskflow.userservice.exception.UserAlreadyExistsException;
import com.taskflow.userservice.model.Role;
import com.taskflow.userservice.model.User;
import com.taskflow.userservice.repository.UserRepository;
import com.taskflow.userservice.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_whenUsernameAndEmailFree_createsUserAndReturnsToken() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("alice");
        request.setEmail("alice@example.com");
        request.setPassword("plaintextPassword");

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plaintextPassword")).thenReturn("hashedPassword");
        when(jwtService.generateToken(any(User.class))).thenReturn("fake-jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("fake-jwt-token");
        assertThat(response.getUsername()).isEqualTo("alice");
        assertThat(response.getRole()).isEqualTo(Role.USER.name());

        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("alice") &&
                user.getPassword().equals("hashedPassword")
        ));
    }

    @Test
    void register_whenUsernameTaken_throwsException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("alice");
        request.setEmail("alice@example.com");
        request.setPassword("plaintextPassword");

        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("alice");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_withCorrectPassword_returnsToken() {
        User existingUser = new User("alice", "alice@example.com", "hashedPassword", Role.USER);

        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("plaintextPassword");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("plaintextPassword", "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(existingUser)).thenReturn("fake-jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("fake-jwt-token");
        assertThat(response.getUsername()).isEqualTo("alice");
    }

    @Test
    void login_withWrongPassword_throwsException() {
        User existingUser = new User("alice", "alice@example.com", "hashedPassword", Role.USER);

        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("wrongPassword");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void login_withUnknownUsername_throwsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("ghost");
        request.setPassword("whatever");

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
