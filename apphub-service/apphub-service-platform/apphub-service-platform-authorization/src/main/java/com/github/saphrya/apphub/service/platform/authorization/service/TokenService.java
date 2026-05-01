package com.github.saphrya.apphub.service.platform.authorization.service;

import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshToken;
import com.github.saphrya.apphub.service.platform.authorization.config.AuthorizationProperties;
import com.github.saphrya.apphub.service.platform.authorization.etc.AccessTokenDto;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class TokenService {
    private final IdGenerator idGenerator;
    private final KeyService keyService;
    private final UuidConverter uuidConverter;
    private final DateTimeUtil dateTimeUtil;
    private final ObjectMapper objectMapper;
    private final AuthorizationProperties authorizationProperties;

    RefreshToken createRefreshToken(UUID userId, boolean rememberMe) {
        UUID refreshTokenId = idGenerator.randomUuid();

        LocalDateTime issuedAt = dateTimeUtil.getCurrentDateTime();
        LocalDateTime expiration = issuedAt.plus(rememberMe ? authorizationProperties.getRefreshTokenExpirationRememberMe() : authorizationProperties.getRefreshTokenExpirationDefault());

        String token = Jwts.builder()
            .id(uuidConverter.convertDomain(refreshTokenId))
            .issuer(authorizationProperties.getIssuer())
            .subject(uuidConverter.convertDomain(userId))
            .issuedAt(new Date(dateTimeUtil.toEpochMillis(issuedAt)))
            .expiration(new Date(dateTimeUtil.toEpochMillis(expiration)))
            .claim(Constants.CLAIM_REMEMBER_ME, rememberMe)
            .signWith(keyService.getPrivateKey())
            .compact();

        return RefreshToken.builder()
            .userId(userId)
            .refreshTokenId(refreshTokenId)
            .issuedAt(issuedAt)
            .expiration(expiration)
            .rememberMe(rememberMe)
            .jwt(token)
            .build();
    }

    public RefreshToken verifyRefreshToken(String refreshToken) {
        Claims claims = Jwts.parser()
            .verifyWith(keyService.getPublicKey())
            .build()
            .parseSignedClaims(refreshToken)
            .getPayload();

        if (!claims.getIssuer().equals(authorizationProperties.getIssuer())) {
            throw ExceptionFactory.reportedException(HttpStatus.FORBIDDEN, ErrorCode.INVALID_TOKEN, "Invalid token issuer: " + claims.getIssuer() + " in token " + refreshToken);
        }

        LocalDateTime expiration = dateTimeUtil.fromDate(claims.getExpiration());
        if (dateTimeUtil.getCurrentDateTime().isAfter(expiration)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.NO_SESSION_AVAILABLE, "Token expired.");
        }

        return RefreshToken.builder()
            .userId(uuidConverter.convertEntity(claims.getSubject()))
            .refreshTokenId(uuidConverter.convertEntity(claims.getId()))
            .issuedAt(dateTimeUtil.fromDate(claims.getIssuedAt()))
            .expiration(expiration)
            .rememberMe(claims.get(Constants.CLAIM_REMEMBER_ME, Boolean.class))
            .jwt(refreshToken)
            .build();
    }

    public AccessTokenDto createAccessToken(UUID userId, List<String> roles) {
        UUID accessTokenId = idGenerator.randomUuid();

        LocalDateTime currentTime = dateTimeUtil.getCurrentDateTime();
        LocalDateTime expiration = currentTime.plus(authorizationProperties.getAccessTokenExpiration());

        String jwt = Jwts.builder()
            .id(uuidConverter.convertDomain(accessTokenId))
            .issuer(authorizationProperties.getIssuer())
            .subject(uuidConverter.convertDomain(userId))
            .issuedAt(new Date(dateTimeUtil.toEpochMillis(currentTime)))
            .expiration(new Date(dateTimeUtil.toEpochMillis(expiration)))
            .claim(Constants.CLAIM_ROLES, objectMapper.writeValueAsString(roles))
            .signWith(keyService.getPrivateKey())
            .compact();

        return AccessTokenDto.builder()
            .jwt(jwt)
            .expiration(expiration)
            .build();
    }

    public AccessToken parseAccessToken(String accessToken) {
        Claims claims = Jwts.parser()
            .verifyWith(keyService.getPublicKey())
            .build()
            .parseSignedClaims(accessToken)
            .getPayload();

        return AccessToken.builder()
            .accessTokenId(uuidConverter.convertEntity(claims.getId()))
            .userId(uuidConverter.convertEntity(claims.getSubject()))
            .roles(objectMapper.readValue(claims.get(Constants.CLAIM_ROLES, String.class), List.class))
            .build();
    }
}
