package org.example.sang_garden;

import jakarta.servlet.http.HttpServletResponse;
import org.example.sang_garden.auth.CustomUserDetails;
import org.example.sang_garden.auth.CustomUserDetailsService;
import org.example.sang_garden.auth.jwt.JwtUtil;
import org.example.sang_garden.controller.AuthController;
import org.example.sang_garden.dto.UserDTO;
import org.example.sang_garden.dto.request.LoginRequest;
import org.example.sang_garden.dto.request.RegisterRequest;
import org.example.sang_garden.dto.response.ApiResponse;
import org.example.sang_garden.dto.response.AuthDataResponse;
import org.example.sang_garden.entity.User;
import org.example.sang_garden.mapper.Mapper;
import org.example.sang_garden.service.RefreshTokenService;
import org.example.sang_garden.service.UserService;
import org.example.sang_garden.util.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthTest {
    @Mock
    private UserService userService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Mapper mapper;

    @Mock
    private HttpServletResponse response;

    private AuthController authController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        authController = new AuthController(
                authenticationManager,
                userDetailsService,
                jwtUtil,
                userService,
                mapper,
                refreshTokenService
        );
    }

    // ==================== LOGIN TESTS ====================

    @Test
    void testLoginSuccess() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("user1", "password123");
        UserDTO userDTO = new UserDTO("user1", "user1@gmail.com", UserRole.USER, true, true);

        User mockUser = User.builder()
                .username("user1")
                .email("user1@gmail.com")
                .password("password123")
                .userRole(UserRole.USER)
                .build();

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        Authentication mockAuthentication = mock(Authentication.class);

        when(userService.getUserDTOByIdentifier("user1")).thenReturn(userDTO);
        when(userDetailsService.loadUserByUsername("user1")).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(mockUser);
        when(userDetails.getUsername()).thenReturn("user1");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(jwtUtil.generateAccessToken(any(CustomUserDetails.class)))
                .thenReturn("mockAccessToken");
        when(refreshTokenService.createRefreshToken(any(User.class)))
                .thenReturn("mockRefreshToken");

        // Act
        ApiResponse<?> responseEntity = authController.login(loginRequest, response);

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.isSuccess());

        AuthDataResponse authData = (AuthDataResponse) responseEntity.getData();
        assertNotNull(authData);
        assertEquals("mockAccessToken", authData.accessToken());
        assertEquals(UserRole.USER, authData.userRole());

        verify(response, times(1)).addHeader(anyString(), anyString());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void testLoginFailure_WrongPassword() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("user1", "wrongPassword");
        UserDTO userDTO = new UserDTO("user1", "user1@gmail.com", UserRole.USER, true, true);

        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        when(userService.getUserDTOByIdentifier("user1")).thenReturn(userDTO);
        when(userDetailsService.loadUserByUsername("user1")).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("user1");
        when(userDetails.getUser()).thenReturn(User.builder().username("user1").userRole(UserRole.USER).build());

        // Simulate authentication failure
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authController.login(loginRequest, response);
        });

        assertEquals("sai tên đăng nhập hoặc mật khẩu", exception.getMessage());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtUtil, never()).generateAccessToken(userDetails);
    }

    @Test
    void testLoginFailure_UserNotFound() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("nonexistent", "password123");

        when(userService.getUserDTOByIdentifier("nonexistent"))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authController.login(loginRequest, response);
        });

        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void testLoginWithEmail() throws Exception {
        // Arrange - Login using email instead of username
        LoginRequest loginRequest = new LoginRequest("user1@gmail.com", "password123");
        UserDTO userDTO = new UserDTO("user1", "user1@gmail.com", UserRole.USER, true, true);

        User mockUser = User.builder()
                .username("user1")
                .email("user1@gmail.com")
                .password("password123")
                .userRole(UserRole.USER)
                .build();

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        Authentication mockAuthentication = mock(Authentication.class);

        when(userService.getUserDTOByIdentifier("user1@gmail.com")).thenReturn(userDTO);
        when(userDetailsService.loadUserByUsername("user1")).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(mockUser);
        when(userDetails.getUsername()).thenReturn("user1");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(jwtUtil.generateAccessToken(any(CustomUserDetails.class)))
                .thenReturn("mockAccessToken");
        when(refreshTokenService.createRefreshToken(any(User.class)))
                .thenReturn("mockRefreshToken");

        // Act
        ApiResponse<?> responseEntity = authController.login(loginRequest, response);

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.isSuccess());
    }


    // ==================== REGISTER TESTS ====================

    @Test
    void testRegisterSuccess() throws Exception {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest(
                "newuser",
                "password123",
                "newuser@gmail.com"
        );

        User mockUser = User.builder()
                .username("newuser")
                .email("newuser@gmail.com")
                .password("hashedPassword")
                .userRole(UserRole.USER)
                .build();

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        Authentication mockAuthentication = mock(Authentication.class);

        when(userService.saveUser(mockUser.getEmail())).thenReturn(mockUser);
        when(userDetailsService.loadUserByUsername("newuser")).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(mockUser);
        when(userDetails.getUsername()).thenReturn("newuser");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(jwtUtil.generateAccessToken(any(CustomUserDetails.class)))
                .thenReturn("mockAccessToken");
        when(refreshTokenService.createRefreshToken(any(User.class)))
                .thenReturn("mockRefreshToken");

        // Act
        ApiResponse<?> responseEntity = authController.register(registerRequest, response);

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.isSuccess());

        AuthDataResponse authData = (AuthDataResponse) responseEntity.getData();
        assertNotNull(authData);
        assertEquals("mockAccessToken", authData.accessToken());
        assertEquals(UserRole.USER, authData.userRole());

        verify(userService, times(1)).saveUser(registerRequest);
        verify(response, times(1)).addHeader(anyString(), anyString());
    }

    @Test
    void testRegisterFailure_DuplicateUsername() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest(
                "existinguser",
                "password123",
                "new@gmail.com"
        );

        doThrow(new RuntimeException("Username already exists"))
                .when(userService).saveUser(registerRequest);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authController.register(registerRequest, response);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userService, times(1)).saveUser(registerRequest);
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

    @Test
    void testRegisterFailure_DuplicateEmail() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest(
                "newuser",
                "password123",
                "existing@gmail.com"
        );

        doThrow(new RuntimeException("Email already exists"))
                .when(userService).saveUser(registerRequest);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authController.register(registerRequest, response);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userService, times(1)).saveUser(registerRequest);
    }

    @Test
    void testRegisterFailure_InvalidPassword() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest(
                "newuser",
                "weakpassword",
                "new@gmail.com"
        );

        doThrow(new IllegalArgumentException("Password too weak"))
                .when(userService).saveUser(registerRequest);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authController.register(registerRequest, response);
        });

        assertEquals("Password too weak", exception.getMessage());
        verify(userService, times(1)).saveUser(registerRequest);
    }


}