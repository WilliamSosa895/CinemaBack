package com.Esteban.cinema.Controller;

import com.Esteban.cinema.Configuration.JwtAuthFilter;
import com.Esteban.cinema.Configuration.JwtService;
import com.Esteban.cinema.Configuration.SecurityConfig;
import com.Esteban.cinema.DTO.Request.LoginRequest;
import com.Esteban.cinema.DTO.Request.PurchaseRequest;
import com.Esteban.cinema.Model.Users;
import com.Esteban.cinema.Service.MovieService;
import com.Esteban.cinema.Service.PurchaseService;
import com.Esteban.cinema.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class, MoviesController.class, PurchasesController.class})
@Import({SecurityConfig.class, JwtAuthFilter.class})
@TestPropertySource(properties = "url.frontend=http://localhost:3000")
class AuthJwtProtectedRoutesWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private MovieService movieService;

    @MockBean
    private PurchaseService purchaseService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void signup_withoutToken_isPublicAndReturnsCreated() throws Exception {
        Users request = new Users();
        request.setFullName("Test User");
        request.setEmail("user@test.com");
        request.setPassword("Password123");

        Users created = new Users();
        created.setIdUser(1L);
        created.setFullName("Test User");
        created.setEmail("user@test.com");
        created.setRole("USER");

        when(userService.register(any(Users.class))).thenReturn(created);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUser").value(1))
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void signin_withoutToken_isPublicAndReturnsJwt() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("Password123");

        Users authenticated = new Users();
        authenticated.setIdUser(1L);
        authenticated.setEmail("user@test.com");
        authenticated.setRole("USER");

        when(userService.login(any(LoginRequest.class))).thenReturn(authenticated);
        when(jwtService.generateToken("user@test.com", 1L, "USER")).thenReturn("jwt-token");

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void protectedRoutes_withoutToken_areRejected() throws Exception {
        mockMvc.perform(get("/movies/Matrix"))
                .andExpect(status().is4xxClientError());

        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setIdShowtime(10L);
        purchaseRequest.setSeats(List.of(List.of(0, 0)));

        mockMvc.perform(post("/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purchaseRequest)))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(put("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void purchases_withValidJwt_areAllowedAndReceiveUserIdFromToken() throws Exception {
        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setIdShowtime(10L);
        purchaseRequest.setSeats(List.of(List.of(0, 0)));

        UserDetails userDetails = User.withUsername("user@test.com")
                .password("ignored")
                .authorities("ROLE_USER")
                .build();

        when(jwtService.extractUsername("valid-token")).thenReturn("user@test.com");
        when(jwtService.extractUserId("valid-token")).thenReturn(9L);
        when(jwtService.isTokenValid("valid-token", "user@test.com")).thenReturn(true);
        when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(userDetails);
        doNothing().when(purchaseService).savePurchase(any(PurchaseRequest.class), eq(9L));

        mockMvc.perform(post("/purchases")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purchaseRequest)))
                .andExpect(status().isCreated());

        verify(purchaseService).savePurchase(any(PurchaseRequest.class), eq(9L));
    }
}
