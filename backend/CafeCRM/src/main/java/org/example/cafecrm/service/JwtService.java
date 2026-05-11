package org.example.cafecrm.service;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

/**
 * Сервис генерации JWT-токенов.
 * <p>
 * Обеспечивает создание подписанных JWT-токенов доступа на основе
 * приватного RSA-ключа. Токен содержит имя пользователя, роль,
 * время выдачи (iat) и время истечения (exp).
 * <p>
 * Приватный ключ загружается из файла по пути, указанному в
 * конфигурации {@code application.private-key-path}.
 * <p>
 * Используется в процессе аутентификации ({@code Auth}) для выдачи
 * токена после успешной проверки учётных данных.
 *
 * @author AinLume
 * @see UserDetails
 * @see org.springframework.security.core.userdetails.UserDetailsService
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    private final ResourceLoader resourceLoader;

    @Value("${application.private-key-path}")
    private String privateKeyPath;

    /**
     * Генерирует JWT-токен доступа для указанного пользователя.
     * <p>
     * Загружает приватный RSA-ключ из файла, формирует токен с claims:
     * <ul>
     *   <li>{@code subject} — имя пользователя ({@link UserDetails#getUsername()})</li>
     *   <li>{@code role} — первая роль из списка авторитетов</li>
     *   <li>{@code iat} — время выдачи токена</li>
     *   <li>{@code exp} — время истечения (1 час от момента выдачи)</li>
     * </ul>
     * <p>
     * Токен подписывается приватным ключом алгоритмом RSA.
     *
     * @param user данные пользователя ({@link UserDetails})
     * @return строка с сгенерированным JWT-токеном
     * @throws RuntimeException если не удалось загрузить ключ или создать токен
     */
    public String generateJwtToken(final UserDetails user) {

        try {
            Resource resource = resourceLoader.getResource(privateKeyPath);
            String keyContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            PrivateKey privateKey = loadPrivateKey(keyContent);

            return Jwts
                    .builder()
                    .subject(user.getUsername())
                    .claim("role", user.getAuthorities().iterator().next().getAuthority())
                    .claim("iat", new Date())
                    .claim("exp", new Date(System.currentTimeMillis() + 3600000))
                    .signWith(privateKey)
                    .compact();
        } catch (final Exception e) {
            throw new RuntimeException("Не удалось создать токен доступа", e);
        }
    }

    /**
     * Загружает приватный RSA-ключ из PEM-строки.
     * <p>
     * Удаляет PEM-заголовки, декодирует Base64 и формирует
     * {@link PrivateKey} через {@link PKCS8EncodedKeySpec}.
     *
     * @param key PEM-закодированная строка с приватным ключом
     * @return сформированный {@link PrivateKey}
     * @throws Exception если формат ключа некорректен или алгоритм не поддерживается
     */
    private PrivateKey loadPrivateKey(String key) throws Exception {
        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}