package org.example.cafecrm.util;

import lombok.RequiredArgsConstructor;
import org.example.cafecrm.service.ClientService;
import org.example.cafecrm.service.StaffService;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomJwtAuthenticationConverter implements Converter<@NotNull Jwt, AbstractAuthenticationToken> {

    private final StaffService staffService;
    private final ClientService clientService;

    @Override
    public AbstractAuthenticationToken convert(final Jwt jwt) {

        Long id = Long.parseLong(jwt.getSubject());
        String role = jwt.getClaimAsString("role");

        if (id == null || role == null) {
            throw new BadCredentialsException("Invalid JWT: missing sub or role");
        }

        UserDetails user = switch (role) {
            case "ROLE_ADMIN", "ROLE_WAITER", "ROLE_COOK", "ROLE_BARTENDER" -> staffService.getEntityById(id);
            case "ROLE_CLIENT" -> clientService.getEntityById(id);
            default -> throw new BadCredentialsException("Unknown role: " + role);
        };

        return new UsernamePasswordAuthenticationToken(user, jwt, user.getAuthorities());
    }
}