package com.mindfulspending.service.impl;

import com.mindfulspending.dto.user.LoginResponse;
import com.mindfulspending.dto.user.UserLoginRequest;
import com.mindfulspending.dto.user.UserRegisterRequest;
import com.mindfulspending.dto.user.UserResponse;
import com.mindfulspending.entity.User;
import com.mindfulspending.exception.EmailAlreadyExistsException;
import com.mindfulspending.exception.UserNotFoundException;
import com.mindfulspending.repository.UserRepository;
import com.mindfulspending.security.JwtService;
import com.mindfulspending.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserServiceImpl userService;

    private User existingUser;

    @BeforeEach
    void setUp(){
        existingUser = User.builder()
                .userId(1L)
                .email("test@example.com")
                .passwordHash("!2345678")
                .build();
    }

    @Nested
    class registerUser{

        @Test
        void shouldRegisterUserSuccessfully() {

            // Arrange
            UserRegisterRequest request = new UserRegisterRequest(
            "test@example.com",
            "password"
    );

            when(userRepository.existsByEmail("test@example.com"))
            .thenReturn(false);

            when(passwordEncoder.encode("password"))
            .thenReturn("encodedPassword");

            when(userRepository.save(any(User.class)))
                    .thenReturn(existingUser);

            // Act
            UserResponse response = userService.registerUser(request);

            // Assert
            assertEquals(1L, response.id());
            assertEquals("test@example.com", response.email());

            // Verify Did my service actually call this method/dependency?
            verify(userRepository).existsByEmail("test@example.com");
            verify(passwordEncoder).encode("password");
            verify(userRepository).save(any(User.class));
        }
        @Test
        void shouldThrowExceptionWhenEmailAlreadyExists(){
            //Arrange
            UserRegisterRequest request = new UserRegisterRequest(
                    "test@example.com",
                    "password"
            );
            when(userRepository.existsByEmail("test@example.com"))
                    .thenReturn(true);
            //Act & Assert
            assertThrows(EmailAlreadyExistsException.class,
                    () -> userService.registerUser(request)
            );
            // Verify
            verify(userRepository).existsByEmail("test@example.com");
            verifyNoInteractions(passwordEncoder);
            verify(userRepository, never()).save(any());

        }

    }

    @Nested
    class login{

        @Test
        void shouldLoginSuccessfully() {
            //Arrange
            UserLoginRequest request = new UserLoginRequest(
                    "test@example.com",
                    "password"
            );

            UserDetails userDetails = new UserDetailsImpl(existingUser);

            when(authenticationManager.authenticate(any(
                    UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal())
                    .thenReturn(userDetails);
            when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));
            when(jwtService.generateToken(userDetails)).thenReturn("test-jwt-token");

            //Act
            LoginResponse response = userService.login(request);

            //Assert
            assertEquals("test-jwt-token", response.token());
            assertEquals(existingUser.getUserId(), response.user().id());
            assertEquals(existingUser.getEmail(), response.user().email());

            //verify
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(authentication).getPrincipal();
            verify(userRepository).findByEmail(existingUser.getEmail());
            verify(jwtService).generateToken(userDetails);

        }
        @Test
        void shouldThrowExceptionWhenUserNotFoundAfterAuthentication(){
            //Arrange
            UserLoginRequest request = new UserLoginRequest(
                    "test@example.com",
                    "password"
            );
            UserDetails userDetails = new UserDetailsImpl(existingUser);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(UserNotFoundException.class, ()-> userService.login(request));

            //Verify
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(authentication).getPrincipal();
            verify(userRepository).findByEmail(existingUser.getEmail());
            verifyNoInteractions(jwtService);

        }

        @Test
        void shouldPropagateAuthenticationExceptionWhenCredentialsAreInvalid(){
            //Arrange
            UserLoginRequest request = new UserLoginRequest(
                    "test@example.com",
                    "password"
            );
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Invalid email or password"));
            //Act & Assert
            assertThrows(AuthenticationException.class, ()->userService.login(request));
            //Verify
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verifyNoInteractions(userRepository);
            verifyNoInteractions(jwtService);
        }

    }

    @Nested
    class getCurrentUser{

        @Test
        void shouldReturnCurrentUser() {

            // Arrange

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                         mockStatic(SecurityContextHolder.class)) {

                mockedSecurityContextHolder
                        .when(SecurityContextHolder::getContext)
                        .thenReturn(securityContext);

                when(securityContext.getAuthentication())
                        .thenReturn(authentication);

                when(authentication.getName())
                        .thenReturn(existingUser.getEmail());

                when(userRepository.findByEmail(existingUser.getEmail()))
                    .thenReturn(Optional.of(existingUser));

                // Act

                UserResponse response = userService.getCurrentUser();

                // Assert

                assertEquals(existingUser.getUserId(), response.id());
                assertEquals(existingUser.getEmail(), response.email());

                // Verify

                verify(securityContext).getAuthentication();
                verify(authentication).getName();
                verify(userRepository).findByEmail(existingUser.getEmail());
            }
            }

            @Test
            void shouldThrowExceptionWhenCurrentUserNotFound() {

                // Arrange

                try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                             mockStatic(SecurityContextHolder.class)) {

                    mockedSecurityContextHolder
                            .when(SecurityContextHolder::getContext)
                            .thenReturn(securityContext);

                    when(securityContext.getAuthentication())
                            .thenReturn(authentication);

                    when(authentication.getName())
                            .thenReturn(existingUser.getEmail());

                    when(userRepository.findByEmail(existingUser.getEmail()))
                .thenReturn(Optional.empty());

                    // Act & Assert

                    assertThrows(
                            UserNotFoundException.class,
                            () -> userService.getCurrentUser()
                    );

                    // Verify

                    verify(securityContext).getAuthentication();
                    verify(authentication).getName();
                    verify(userRepository).findByEmail(existingUser.getEmail());
                }
            }

    }

    @Nested
    class deleteUser{
        @Test
        void shouldDeleteCurrentUser() {

            // Arrange

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                         mockStatic(SecurityContextHolder.class)) {

                mockedSecurityContextHolder
                        .when(SecurityContextHolder::getContext)
                        .thenReturn(securityContext);

                when(securityContext.getAuthentication())
                        .thenReturn(authentication);

                when(authentication.getName())
                        .thenReturn(existingUser.getEmail());

                when(userRepository.findByEmail(existingUser.getEmail()))
                    .thenReturn(Optional.of(existingUser));

                // Act

                userService.deleteUser();

                // Verify

                verify(securityContext).getAuthentication();
                verify(authentication).getName();
                verify(userRepository).findByEmail(existingUser.getEmail());
                verify(userRepository).deleteById(existingUser.getUserId());
            }
        }

        @Test
        void shouldThrowExceptionWhenDeletingNonExistingUser() {

            // Arrange

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                         mockStatic(SecurityContextHolder.class)) {

                mockedSecurityContextHolder
                        .when(SecurityContextHolder::getContext)
                        .thenReturn(securityContext);

                when(securityContext.getAuthentication())
                        .thenReturn(authentication);

                when(authentication.getName())
                        .thenReturn(existingUser.getEmail());

                when(userRepository.findByEmail(existingUser.getEmail()))
                .thenReturn(Optional.empty());

                // Act & Assert

                assertThrows(
                        UserNotFoundException.class,
                        () -> userService.deleteUser()
                );

                // Verify

                verify(userRepository).findByEmail(existingUser.getEmail());

                verify(userRepository, never())
                        .deleteById(any());
            }
        }
    }

}