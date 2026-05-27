package com.Esteban.cinema.Service;

import com.Esteban.cinema.DTO.Request.LoginRequest;
import com.Esteban.cinema.Model.Users;
import com.Esteban.cinema.Repository.UserRepository;
import com.Esteban.cinema.exceptions.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void register_whenEmailIsNew_savesUserWithEncodedPassword() {
        Users request = new Users();
        request.setFullName("Test User");
        request.setEmail("user@test.com");
        request.setPassword("Password123");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123")).thenReturn("encoded-password");
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Users result = userService.register(request);

        assertEquals("Test User", result.getFullName());
        assertEquals("user@test.com", result.getEmail());
        assertEquals("USER", result.getRole());
        assertEquals("encoded-password", result.getPassword());
        verify(userRepository).save(any(Users.class));
    }

    @Test
    void register_whenEmailAlreadyExists_throwsBusinessException() {
        Users existing = new Users();
        existing.setEmail("user@test.com");

        Users request = new Users();
        request.setFullName("Test User");
        request.setEmail("user@test.com");
        request.setPassword("Password123");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(existing));

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.register(request));

        assertEquals("Users with email user@test.com already exists.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_whenCredentialsAreValid_returnsUser() {
        Users user = new Users();
        user.setEmail("user@test.com");
        user.setPassword("encoded-password");

        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("Password123");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123", "encoded-password")).thenReturn(true);

        Users result = userService.login(request);

        assertEquals(user, result);
    }

    @Test
    void login_whenPasswordIsInvalid_throwsBusinessException() {
        Users user = new Users();
        user.setEmail("user@test.com");
        user.setPassword("encoded-password");

        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("wrong-password");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.login(request));

        assertEquals("Invalid password.", exception.getMessage());
    }

    @Test
    void login_whenEmailDoesNotExist_throwsBusinessException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("missing@test.com");
        request.setPassword("Password123");

        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.login(request));

        assertEquals("Users with email missing@test.com not found.", exception.getMessage());
    }

    @Test
    void updateUser_whenPasswordIsProvided_updatesAndEncodesPassword() {
        Users existing = new Users();
        existing.setIdUser(1L);
        existing.setFullName("Old Name");
        existing.setEmail("old@test.com");
        existing.setPassword("old-password");

        Users request = new Users();
        request.setFullName("New Name");
        request.setEmail("new@test.com");
        request.setPassword("NewPassword123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("NewPassword123")).thenReturn("encoded-new-password");
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Users result = userService.updateUser(request, 1L);

        assertEquals("New Name", result.getFullName());
        assertEquals("new@test.com", result.getEmail());
        assertEquals("encoded-new-password", result.getPassword());
        verify(userRepository).save(existing);
    }

    @Test
    void updateUser_whenPasswordIsEmpty_keepsExistingPassword() {
        Users existing = new Users();
        existing.setIdUser(2L);
        existing.setFullName("Old Name");
        existing.setEmail("old@test.com");
        existing.setPassword("old-password");

        Users request = new Users();
        request.setFullName("New Name");
        request.setEmail("new@test.com");
        request.setPassword("");

        when(userRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Users result = userService.updateUser(request, 2L);

        assertEquals("New Name", result.getFullName());
        assertEquals("new@test.com", result.getEmail());
        assertEquals("old-password", result.getPassword());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void updateUser_whenUserDoesNotExist_throwsBusinessException() {
        Users request = new Users();
        request.setFullName("New Name");
        request.setEmail("new@test.com");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.updateUser(request, 99L));

        assertEquals("Users with id 99 not found.", exception.getMessage());
    }

    @Test
    void getUserDetails_whenUserExists_returnsUser() {
        Users user = new Users();
        user.setIdUser(4L);
        user.setEmail("user@test.com");

        when(userRepository.findById(4L)).thenReturn(Optional.of(user));

        Users result = userService.getUserDetails(4L);

        assertEquals(user, result);
    }

    @Test
    void getUserDetails_whenUserDoesNotExist_throwsBusinessException() {
        when(userRepository.findById(123L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.getUserDetails(123L));

        assertEquals("Users with id 123 not found.", exception.getMessage());
    }
}
