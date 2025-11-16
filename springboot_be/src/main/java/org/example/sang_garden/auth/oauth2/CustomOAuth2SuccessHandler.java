package org.example.sang_garden.auth.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.sang_garden.auth.CustomUserDetails;
import org.example.sang_garden.auth.jwt.JwtUtil;
import org.example.sang_garden.entity.User;
import org.example.sang_garden.service.RefreshTokenService;
import org.example.sang_garden.service.UserService;
import org.example.sang_garden.util.AppConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Value("${fe.baseUrl}")
    private String FRONTEND_URL;


    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public CustomOAuth2SuccessHandler(JwtUtil jwtUtil, UserService userService, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException{

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();

        String email = oauthUser.getAttribute("email");

        User user = userService.getUserByEmail(email);
        if (user == null) {
            user = userService.saveUser(email);

        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String refreshToken = refreshTokenService.createRefreshToken(userDetails.getUser());

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(AppConstant.REFRESH_TOKEN_AGE)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        String redirectUrl = FRONTEND_URL + "/auth/callback?success=true";
        response.sendRedirect(redirectUrl);
    }

}

