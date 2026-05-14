package com.github.saphyra.apphub.service.platform.main_gateway.service.authorization;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.Role;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.platform.main_gateway.config.AuthorizationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@Slf4j
public class TokenParser {
    private final PublicKey publicKey;
    private final AuthorizationProperties authorizationProperties;
    private final DateTimeUtil dateTimeUtil;
    private final UuidConverter uuidConverter;
    private final ObjectMapper objectMapper;

    public TokenParser(
        @Value("${authorization.publicKey}") String publicKey,
        AuthorizationProperties authorizationProperties,
        DateTimeUtil dateTimeUtil,
        UuidConverter uuidConverter,
        ObjectMapper objectMapper
    ) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.authorizationProperties = authorizationProperties;
        this.dateTimeUtil = dateTimeUtil;
        this.uuidConverter = uuidConverter;
        this.objectMapper = objectMapper;
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);

        this.publicKey = KeyFactory.getInstance("RSA")
            .generatePublic(new X509EncodedKeySpec(publicKeyBytes));
    }

    public Mono<AccessToken> verifyAccessToken(String accessTokenString) {
        if (isBlank(accessTokenString)) {
            return Mono.error(() -> ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.NO_SESSION_AVAILABLE, "Blank accessToken"));
        }

        Claims claims;
        try {
            claims = Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(accessTokenString)
                .getPayload();
        } catch (ExpiredJwtException e) {
            return Mono.error(() -> ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.NO_SESSION_AVAILABLE, "AccessToken expired.", e));
        } catch (MalformedJwtException e) {
            return Mono.error(() -> ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_TOKEN, "Malformed JWT: " + accessTokenString, e));
        } catch (Exception e) {
            return Mono.error(() -> ExceptionFactory.reportedException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_TOKEN, "Invalid token: " + accessTokenString, e));
        }

        if (!claims.getIssuer().equals(authorizationProperties.getIssuer())) {
            return Mono.error(() -> ExceptionFactory.reportedException(HttpStatus.FORBIDDEN, ErrorCode.INVALID_TOKEN, "Invalid token issuer: " + claims.getIssuer() + " in token " + accessTokenString));
        }

        LocalDateTime expiration = dateTimeUtil.fromDate(claims.getExpiration());
        if (dateTimeUtil.getCurrentDateTime().isAfter(expiration)) {
            return Mono.error(() -> ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.NO_SESSION_AVAILABLE, "Token expired."));
        }

        TypeReference<List<Role>> typeReference = new TypeReference<>() {
        };
        AccessToken accessToken = AccessToken.builder()
            .accessTokenId(uuidConverter.convertEntity(claims.getId()))
            .refreshTokenId(uuidConverter.convertEntity(claims.get(Constants.CLAIM_REFRESH_TOKEN_ID, String.class)))
            .userId(uuidConverter.convertEntity(claims.getSubject()))
            .roles(objectMapper.readValue(claims.get(Constants.CLAIM_ROLES, String.class), typeReference))
            .build();

        return Mono.just(accessToken);
    }
}
