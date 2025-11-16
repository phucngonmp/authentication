package org.example.sang_garden.controller;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.sang_garden.auth.CustomUserDetails;
import org.example.sang_garden.auth.CustomUserDetailsService;
import org.example.sang_garden.auth.jwt.JwtUtil;
import org.example.sang_garden.dto.UserDTO;
import org.example.sang_garden.dto.request.LoginRequest;
import org.example.sang_garden.dto.request.RegisterRequest;
import org.example.sang_garden.dto.response.ApiResponse;
import org.example.sang_garden.dto.response.AuthDataResponse;
import org.example.sang_garden.entity.User;
import org.example.sang_garden.mapper.Mapper;
import org.example.sang_garden.service.RefreshTokenService;
import org.example.sang_garden.service.UserService;
import org.example.sang_garden.util.AppConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final Mapper mapper;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService,
                          JwtUtil jwtUtil, UserService userService, Mapper mapper, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
        this.refreshTokenService = refreshTokenService;
    }


    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            UserDTO userDTO = userService.getUserDTOByIdentifier(loginRequest.identifier());
            String username = userDTO.username();
            if(!userDTO.isPasswordLoginAllowed()){
                throw new BadCredentialsException("Tài khoản chưa tạo mật khẩu, vui lòng đăng nhập bằng Google");
            }
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

            String accessToken = generateAccessTokenWithPassword(userDetails, loginRequest.password());
            String refreshToken = refreshTokenService.createRefreshToken(userDetails.getUser());
            addRefreshTokenToCookie(response, refreshToken);
            return ApiResponse.success(new AuthDataResponse(accessToken, userDetails.getUser().getUserRole()));

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("sai tên đăng nhập hoặc mật khẩu");
        }
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody @Valid RegisterRequest request, HttpServletResponse response) {
        // save user
        userService.saveUser(request);

        // issue tokens
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(request.username());
        String accessToken = generateAccessTokenWithPassword(userDetails, request.password());
        String refreshToken = refreshTokenService.createRefreshToken(userDetails.getUser());
        // add refresh cookie to response
        addRefreshTokenToCookie(response, refreshToken);
        return ApiResponse.success(new AuthDataResponse(accessToken, userDetails.getUser().getUserRole()));
    }
    @GetMapping("/status")
    public ApiResponse<?> isUserLogged(@CookieValue(value = "refreshToken", required = false) UUID refreshToken) {
        logger.info("isUserLogged refreshToken: {}", refreshToken);
        boolean isRefreshTokenValid = refreshToken != null && refreshTokenService.isValidRefreshToken(refreshToken);
        String responseMessage = isRefreshTokenValid ? "logged in" : "not logged in";
        return ApiResponse.success(responseMessage, responseMessage);
    }
    @PostMapping("/new-access-token")
    public ApiResponse<?> refreshAccessToken(@CookieValue(value = "refreshToken", required = false) UUID refreshToken) {
        logger.info("refreshAccessToken refreshToken: {}", refreshToken);
        if(refreshToken == null){
            return ApiResponse.error("refresh token is null");
        }
        if(!refreshTokenService.isValidRefreshToken(refreshToken)){
            return ApiResponse.error("refresh token is invalid");
        }
        User user = refreshTokenService.getUser(refreshToken);
        String accessToken = jwtUtil.generateAccessToken(user);
        return ApiResponse.success(new AuthDataResponse(accessToken, user.getUserRole()));
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout(@CookieValue(value = "refreshToken", required = false) UUID refreshToken,
                                 HttpServletResponse response) {

        // clear the refresh token from client
        try{
            refreshTokenService.invalidateRefreshToken(refreshToken);
        } catch (Exception e){
            logger.error(e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
        // return an empty refresh token cookie for client
        addRefreshTokenToCookie(response, null);
        return ApiResponse.success("logged out");
    }


    private String generateAccessTokenWithPassword(CustomUserDetails userDetails, String rawPassword) {
        // Authenticate user to make sure correct credential
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDetails.getUsername(), rawPassword)
        );
        // Generate JWT token
        return jwtUtil.generateAccessToken(userDetails);
    }


    /*
        if refresh token pass to this function is null then this mean clear the token in cookie
     */
    private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        long age = AppConstant.REFRESH_TOKEN_AGE;
        if (refreshToken == null){
            refreshToken = "";
            age = 0;
        }
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(age)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}


