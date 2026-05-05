package com.github.saphrya.apphub.service.platform.authorization.service;

import com.github.saphrya.apphub.service.platform.authorization.config.AuthorizationProperties;
import com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token.RefreshToken;
import com.github.saphrya.apphub.service.platform.authorization.etc.AccessTokenDto;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import tools.jackson.databind.ObjectMapper;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID REFRESH_TOKEN_ID = UUID.randomUUID();
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = USER_ID.toString();
    private static final String REFRESH_TOKEN_ID_STRING = REFRESH_TOKEN_ID.toString();
    private static final String ACCESS_TOKEN_ID_STRING = ACCESS_TOKEN_ID.toString();
    private static final String ISSUER = "test-issuer";
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final LocalDateTime ISSUED_AT = CURRENT_TIME.minusHours(5).withNano(0);
    private static final List<String> ROLES = List.of("ROLE_A");
    private static final long ISSUED_AT_EPOCH = 1000000L;
    private static final long EXPIRATION_EPOCH = 2000000L;
    private static final LocalDateTime EXPIRATION_TIME = CURRENT_TIME.plusHours(1).withNano(0);

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private KeyService keyService;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private AuthorizationProperties authorizationProperties;

    @InjectMocks
    private TokenService underTest;

    private KeyPair keyPair;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        keyPair = gen.generateKeyPair();
    }

    @Test
    void createRefreshToken_default() {
        LocalDateTime expiration = CURRENT_TIME.plus(Duration.ofHours(1));

        given(idGenerator.randomUuid()).willReturn(REFRESH_TOKEN_ID);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(authorizationProperties.getRefreshTokenExpirationDefault()).willReturn(Duration.ofHours(1));
        given(authorizationProperties.getIssuer()).willReturn(ISSUER);
        given(uuidConverter.convertDomain(REFRESH_TOKEN_ID)).willReturn(REFRESH_TOKEN_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(dateTimeUtil.toEpochMillis(CURRENT_TIME)).willReturn(ISSUED_AT_EPOCH);
        given(dateTimeUtil.toEpochMillis(expiration)).willReturn(EXPIRATION_EPOCH);
        given(keyService.getPrivateKey()).willReturn(keyPair.getPrivate());

        BiWrapper<String, RefreshToken> result = underTest.createRefreshToken(USER_ID, false);

        assertThat(result.getEntity1()).isNotBlank();
        RefreshToken refreshToken = result.getEntity2();
        assertThat(refreshToken.getUserId()).isEqualTo(USER_ID);
        assertThat(refreshToken.getRefreshTokenId()).isEqualTo(REFRESH_TOKEN_ID);
        assertThat(refreshToken.getIssuedAt()).isEqualTo(CURRENT_TIME);
        assertThat(refreshToken.getExpiration()).isEqualTo(expiration);
        assertThat(refreshToken.isRememberMe()).isFalse();
    }

    @Test
    void createRefreshToken_rememberMe() {
        LocalDateTime expiration = CURRENT_TIME.plus(Duration.ofDays(30));

        given(idGenerator.randomUuid()).willReturn(REFRESH_TOKEN_ID);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(authorizationProperties.getRefreshTokenExpirationRememberMe()).willReturn(Duration.ofDays(30));
        given(authorizationProperties.getIssuer()).willReturn(ISSUER);
        given(uuidConverter.convertDomain(REFRESH_TOKEN_ID)).willReturn(REFRESH_TOKEN_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(dateTimeUtil.toEpochMillis(CURRENT_TIME)).willReturn(ISSUED_AT_EPOCH);
        given(dateTimeUtil.toEpochMillis(expiration)).willReturn(EXPIRATION_EPOCH);
        given(keyService.getPrivateKey()).willReturn(keyPair.getPrivate());

        BiWrapper<String, RefreshToken> result = underTest.createRefreshToken(USER_ID, true);

        assertThat(result.getEntity1()).isNotBlank();
        RefreshToken refreshToken = result.getEntity2();
        assertThat(refreshToken.isRememberMe()).isTrue();
        assertThat(refreshToken.getExpiration()).isEqualTo(expiration);
    }

    @Test
    void verifyRefreshToken_invalidIssuer() {
        String jwt = buildRefreshTokenJwt("wrong-issuer", EXPIRATION_TIME, true);

        given(keyService.getPublicKey()).willReturn(keyPair.getPublic());
        given(authorizationProperties.getIssuer()).willReturn(ISSUER);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(dateTimeUtil.fromDate(any())).willCallRealMethod();

        Throwable ex = catchThrowable(() -> underTest.verifyRefreshToken(jwt));

        ExceptionValidator.validateReportedException(ex, HttpStatus.FORBIDDEN, ErrorCode.INVALID_TOKEN);
    }

    @Test
    void verifyRefreshToken_expired() {
        String jwt = buildRefreshTokenJwt(ISSUER, CURRENT_TIME.minusMinutes(1), false);

        given(keyService.getPublicKey()).willReturn(keyPair.getPublic());
        given(dateTimeUtil.fromDate(any())).willCallRealMethod();
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);

        Throwable ex = catchThrowable(() -> underTest.verifyRefreshToken(jwt));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.UNAUTHORIZED, ErrorCode.NO_SESSION_AVAILABLE);
    }

    @Test
    void verifyRefreshToken() {
        String jwt = buildRefreshTokenJwt(ISSUER, EXPIRATION_TIME, true);

        given(keyService.getPublicKey()).willReturn(keyPair.getPublic());
        given(authorizationProperties.getIssuer()).willReturn(ISSUER);
        given(dateTimeUtil.fromDate(any())).willCallRealMethod();
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(REFRESH_TOKEN_ID_STRING)).willReturn(REFRESH_TOKEN_ID);

        RefreshToken result = underTest.verifyRefreshToken(jwt);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getRefreshTokenId()).isEqualTo(REFRESH_TOKEN_ID);
        assertThat(result.getIssuedAt()).isEqualTo(ISSUED_AT);
        assertThat(result.getExpiration()).isEqualTo(EXPIRATION_TIME);
        assertThat(result.isRememberMe()).isTrue();
    }

    @Test
    void createAccessToken() {
        LocalDateTime expiration = CURRENT_TIME.plus(Duration.ofMinutes(30));

        given(idGenerator.randomUuid()).willReturn(ACCESS_TOKEN_ID);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(authorizationProperties.getAccessTokenExpiration()).willReturn(Duration.ofMinutes(30));
        given(authorizationProperties.getIssuer()).willReturn(ISSUER);
        given(uuidConverter.convertDomain(ACCESS_TOKEN_ID)).willReturn(ACCESS_TOKEN_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(REFRESH_TOKEN_ID)).willReturn(REFRESH_TOKEN_ID_STRING);
        given(dateTimeUtil.toEpochMillis(CURRENT_TIME)).willReturn(ISSUED_AT_EPOCH);
        given(dateTimeUtil.toEpochMillis(expiration)).willReturn(EXPIRATION_EPOCH);
        given(objectMapper.writeValueAsString(ROLES)).willReturn("[\"ROLE_A\"]");
        given(keyService.getPrivateKey()).willReturn(keyPair.getPrivate());

        AccessTokenDto result = underTest.createAccessToken(USER_ID, REFRESH_TOKEN_ID, ROLES);

        assertThat(result.getJwt()).isNotBlank();
        assertThat(result.getExpiration()).isEqualTo(expiration);
    }

    @Test
    void parseAccessToken_expired() {
        String jwt = Jwts.builder()
            .id(ACCESS_TOKEN_ID_STRING)
            .subject(USER_ID_STRING)
            .claim("roles", "[\"ROLE_A\"]")
            .expiration(new Date(System.currentTimeMillis() - 60_000))
            .signWith(keyPair.getPrivate())
            .compact();

        given(keyService.getPublicKey()).willReturn(keyPair.getPublic());

        assertThat(underTest.parseAccessToken(jwt)).isEmpty();
    }

    @Test
    void parseAccessToken() {
        String rolesJson = "[\"ROLE_A\"]";
        String jwt = Jwts.builder()
            .id(ACCESS_TOKEN_ID_STRING)
            .subject(USER_ID_STRING)
            .claim("roles", rolesJson)
            .signWith(keyPair.getPrivate())
            .compact();

        given(keyService.getPublicKey()).willReturn(keyPair.getPublic());
        given(uuidConverter.convertEntity(ACCESS_TOKEN_ID_STRING)).willReturn(ACCESS_TOKEN_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(objectMapper.readValue(rolesJson, List.class)).willReturn(ROLES);

        Optional<AccessToken> result = underTest.parseAccessToken(jwt);

        AccessToken accessToken = result.get();
        assertThat(accessToken.getAccessTokenId()).isEqualTo(ACCESS_TOKEN_ID);
        assertThat(accessToken.getUserId()).isEqualTo(USER_ID);
        assertThat(accessToken.getRoles()).isEqualTo(ROLES);
    }

    private String buildRefreshTokenJwt(String issuer, LocalDateTime expiration, boolean rememberMe) {
        return Jwts.builder()
            .id(REFRESH_TOKEN_ID_STRING)
            .issuer(issuer)
            .subject(USER_ID_STRING)
            .issuedAt(new Date(ISSUED_AT.toInstant(ZoneOffset.UTC).toEpochMilli()))
            .expiration(new Date(expiration.toInstant(ZoneOffset.UTC).toEpochMilli()))
            .claim("remember_me", rememberMe)
            .signWith(keyPair.getPrivate())
            .compact();
    }
}

