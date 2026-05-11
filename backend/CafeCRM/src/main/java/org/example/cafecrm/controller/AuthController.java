package org.example.cafecrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Аутентификация и авторизация: регистрация клиентов и сотрудников, вход, выход")
public class AuthController {

    private final ClientService clientService;
    private final StaffService staffService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private static final String JWT_COOKIE_NAME = "jwt";
    private static final int JWT_MAX_AGE_SECONDS = 3600;

    @PostMapping("/register/client")
    @Operation(
            summary = "Регистрация клиента",
            description = "Создаёт нового клиента в системе. Доступно без аутентификации. " +
                    "Возвращает данные созданного клиента со статусом 201 Created."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Клиент успешно зарегистрирован",
                    content = @Content(schema = @Schema(implementation = ClientDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе")
    })
    public ResponseEntity<@NotNull ClientDto> registerClient(
            @RequestBody @Valid
            @Parameter(description = "Данные для регистрации клиента", required = true)
            ClientCreateRequest request
    ) {
        ClientDto created = clientService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/register/staff")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Регистрация сотрудника",
            description = "Создаёт нового сотрудника в системе. Доступно только администратору. " +
                    "Возвращает данные созданного сотрудника со статусом 201 Created."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Сотрудник успешно зарегистрирован",
                    content = @Content(schema = @Schema(implementation = StaffDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён — требуется роль ADMIN")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<@NotNull StaffDto> registerStaff(
            @RequestBody @Valid
            @Parameter(description = "Данные для регистрации сотрудника", required = true)
            StaffCreateRequest request
    ) {
        StaffDto created = staffService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login/client")
    @Operation(
            summary = "Вход клиента",
            description = "Аутентифицирует клиента по номеру телефона и паролю. " +
                    "При успешной аутентификации устанавливает JWT-cookie и возвращает данные клиента. " +
                    "Cookie httpOnly, max-age = 3600 секунд."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный вход",
                    content = @Content(schema = @Schema(implementation = ClientDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "401", description = "Неверный номер телефона или пароль")
    })
    public ResponseEntity<@NotNull ClientDto> loginClient(
            @RequestBody @Valid
            @Parameter(description = "Данные для входа клиента", required = true)
            ClientLoginRequest request,

            @Parameter(description = "HTTP-ответ для установки cookie", hidden = true)
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
    @Operation(
            summary = "Вход сотрудника",
            description = "Аутентифицирует сотрудника по email и паролю. " +
                    "При успешной аутентификации устанавливает JWT-cookie и возвращает данные сотрудника. " +
                    "Cookie httpOnly, max-age = 3600 секунд."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный вход",
                    content = @Content(schema = @Schema(implementation = StaffDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "401", description = "Неверный email или пароль")
    })
    public ResponseEntity<@NotNull StaffDto> loginStaff(
            @RequestBody @Valid
            @Parameter(description = "Данные для входа сотрудника", required = true)
            StaffLoginRequest request,

            @Parameter(description = "HTTP-ответ для установки cookie", hidden = true)
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
    @Operation(
            summary = "Выход из системы",
            description = "Завершает сессию, инвалидируя JWT-cookie (устанавливает max-age = 0). " +
                    "Доступно любому аутентифицированному пользователю."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный выход"),
            @ApiResponse(responseCode = "401", description = "Требуется аутентификация")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<@NotNull Void> logout(
            @Parameter(description = "HTTP-ответ для удаления cookie", hidden = true)
            HttpServletResponse response) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @Operation(
            summary = "Обновление токена",
            description = "Возвращает новый JWT-токен для текущего аутентифицированного пользователя. " +
                    "Токен также обновляется в cookie. " +
                    "Позволяет продлить сессию без повторного входа."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Токен успешно обновлён"
            ),
            @ApiResponse(responseCode = "401", description = "Требуется аутентификация")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<@NotNull TokenResponse> refresh(
            Authentication authentication,
            @Parameter(description = "HTTP-ответ для обновления cookie", hidden = true)
            HttpServletResponse response) {

        String newToken = jwtService.generateJwtToken((UserDetails) authentication.getPrincipal());
        addJwtCookie(response, newToken);

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