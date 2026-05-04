package org.example.cafecrm.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.auth.ClientLoginRequest;
import org.example.cafecrm.domain.dto.auth.StaffLoginRequest;
import org.example.cafecrm.domain.dto.auth.TokenResponse;
import org.example.cafecrm.domain.dto.client.ClientCreateRequest;
import org.example.cafecrm.domain.dto.client.ClientDto;
import org.example.cafecrm.domain.dto.staff.StaffCreateRequest;
import org.example.cafecrm.domain.dto.staff.StaffDto;
import org.example.cafecrm.domain.entity.Client;
import org.example.cafecrm.domain.entity.Staff;
import org.example.cafecrm.service.ClientService;
import org.example.cafecrm.service.JwtService;
import org.example.cafecrm.service.StaffService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ClientService clientService;
    private final StaffService staffService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private static final String JWT_COOKIE_NAME = "jwt";
    private static final int JWT_MAX_AGE_SECONDS = 3600;

    @PostMapping("/register/client")
    public ResponseEntity<@NotNull ClientDto> registerClient(@RequestBody @Valid ClientCreateRequest request) {
        ClientDto created = clientService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/register/staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<@NotNull StaffDto> registerStaff(@RequestBody @Valid StaffCreateRequest request) {
        StaffDto created = staffService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login/client")
    public ResponseEntity<@NotNull ClientDto> loginClient(
            @RequestBody @Valid ClientLoginRequest request,
            HttpServletResponse response
    ) {

        Client client = clientService.findByPhoneNumber(request.phone());
        if (client == null || !passwordEncoder.matches(request.password(), client.getPassword())) {
            throw new BadCredentialsException("Invalid phone or password");
        }

        String token = jwtService.generateJwtToken(client);
        addJwtCookie(response, token);

        return ResponseEntity.ok(clientService.findById(client.getId()));
    }

    @PostMapping("/login/staff")
    public ResponseEntity<@NotNull StaffDto> loginStaff(
            @RequestBody @Valid StaffLoginRequest request,
            HttpServletResponse response) {

        Staff staff = staffService.getStaffByEmail(request.email());
        if (staff == null || !passwordEncoder.matches(request.password(), staff.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateJwtToken(staff);
        addJwtCookie(response, token);

        return ResponseEntity.ok(staffService.findById(staff.getId()));
    }

    @PostMapping("/logout")
    public ResponseEntity<@NotNull Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    private void addJwtCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true для https://
        cookie.setPath("/");
        cookie.setMaxAge(JWT_MAX_AGE_SECONDS);
        response.addCookie(cookie);
    }
}