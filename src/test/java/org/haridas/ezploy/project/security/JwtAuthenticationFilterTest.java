package org.haridas.ezploy.project.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(
                jwtService,
                userDetailsService
        );

        SecurityContextHolder.clearContext();
    }
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateJwt() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer valid-token");
        when(jwtService.extractUsername("valid-token"))
                .thenReturn("username");
        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("username")
                        .password("encoded-password")
                        .roles("USER")
                        .build();
        when(userDetailsService.loadUserByUsername("username"))
                .thenReturn(userDetails);
        filter.doFilter(request, response, filterChain);
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertThat(authentication).isNotNull();
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getName()).isEqualTo("username");

        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername("valid-token");
        verify(userDetailsService).loadUserByUsername("username");
    }


    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderIsMissing()
            throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }
    @Test
    void shouldClearSecurityContextWhenJwtIsInvalid()
            throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer invalid-token");

        when(jwtService.extractUsername("invalid-token"))
                .thenThrow(new JwtException("Invalid token"));

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        verify(jwtService).extractUsername("invalid-token");
        verifyNoInteractions(userDetailsService);
        verify(filterChain).doFilter(request, response);
    }
/*
* can be solved using following in the jwtAuthenticationFilter
* if (username != null && SecurityContextHolder.getContext().getAuthentication() == null)
* Add this condition in the if block after verifying username
* Removed since clashing with the shouldClearSecurityContextWhenUserDoesNotExists test
*/

//    @Test
//    void shouldSkipJwtProcessingWhenAlreadyAuthenticated()
//            throws Exception {
//
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        MockHttpServletResponse response = new MockHttpServletResponse();
//
//        Authentication existingAuthentication =
//                new UsernamePasswordAuthenticationToken(
//                        "username",
//                        null,
//                        List.of()
//                );
//
//        SecurityContextHolder.getContext()
//                .setAuthentication(existingAuthentication);
//
//        request.addHeader("Authorization", "Bearer some-token");
//
//        filter.doFilter(request, response, filterChain);
//
//        Authentication authentication =
//                SecurityContextHolder.getContext().getAuthentication();
//
//        assertThat(authentication).isSameAs(existingAuthentication);
//
//        verifyNoInteractions(jwtService, userDetailsService);
//        verify(filterChain).doFilter(request, response);
//    }
    @Test
    void shouldIgnoreNonBearerAuthorizationHeader()
            throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Basic abc123");

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        verifyNoInteractions(jwtService, userDetailsService);
        verify(filterChain).doFilter(request, response);
    }
    @Test
    void shouldClearSecurityContextWhenUserDoesNotExist() throws Exception {
        // 1. Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer valid-token");
        String username = "nonexistentuser";

        // Pre-populate context with non-null empty authorities list
        UsernamePasswordAuthenticationToken existingAuth =
                new UsernamePasswordAuthenticationToken("oldUser", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        when(jwtService.extractUsername("valid-token")).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username))
                .thenThrow(new UsernameNotFoundException("User not found"));

        // 2. Act
        filter.doFilter(request, response, filterChain);

        // 3. Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(jwtService).extractUsername("valid-token");
        verify(userDetailsService).loadUserByUsername(username);
        verify(filterChain).doFilter(request, response);
    }

}
