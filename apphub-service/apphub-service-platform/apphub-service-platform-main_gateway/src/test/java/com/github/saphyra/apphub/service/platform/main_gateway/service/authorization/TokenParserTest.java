package com.github.saphyra.apphub.service.platform.main_gateway.service.authorization;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.platform.main_gateway.config.AuthorizationProperties;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TokenParserTest {
    private static final String ISSUER = "test-issuer";
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final UUID REFRESH_TOKEN_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ACCESS_TOKEN_ID_STRING = ACCESS_TOKEN_ID.toString();
    private static final String REFRESH_TOKEN_ID_STRING = REFRESH_TOKEN_ID.toString();
    private static final String USER_ID_STRING = USER_ID.toString();
    private static final List<String> ROLES = List.of("ROLE_A");
    private static final String ROLES_JSON = "[\"ROLE_A\"]";

    @Mock
    private AuthorizationProperties authorizationProperties;

    private final DateTimeUtil dateTimeUtil = new DateTimeUtil();

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ObjectMapper objectMapper;

    private TokenParser underTest;

    private KeyPair keyPair;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        keyPair = gen.generateKeyPair();
        String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        underTest = new TokenParser(publicKeyBase64, authorizationProperties, dateTimeUtil, uuidConverter, objectMapper);
    }

    @Test
    void verifyAccessToken_blank() {
        Throwable ex = catchThrowable(() -> underTest.verifyAccessToken("  ").block());

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.UNAUTHORIZED, ErrorCode.NO_SESSION_AVAILABLE);
    }

    @Test
    void verifyAccessToken_invalidToken() {
        Throwable ex = catchThrowable(() -> underTest.verifyAccessToken("not-a-jwt").block());

        ExceptionValidator.validateReportedException(ex, HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_TOKEN);
    }

    @Test
    void verifyAccessToken_invalidIssuer() {
        String jwt = buildJwt("wrong-issuer", new Date(System.currentTimeMillis() + 60_000));

        given(authorizationProperties.getIssuer()).willReturn(ISSUER);

        Throwable ex = catchThrowable(() -> underTest.verifyAccessToken(jwt).block());

        ExceptionValidator.validateReportedException(ex, HttpStatus.FORBIDDEN, ErrorCode.INVALID_TOKEN);
    }

    @Test
    void verifyAccessToken_expired() {
        Date expirationDate = new Date(System.currentTimeMillis() - 60_000);
        String jwt = buildJwt(ISSUER, expirationDate);

        Throwable ex = catchThrowable(() -> underTest.verifyAccessToken(jwt).block());

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.UNAUTHORIZED, ErrorCode.NO_SESSION_AVAILABLE);
    }

    @Test
    void verifyAccessToken() {
        Date expirationDate = new Date(System.currentTimeMillis() + 60_000);
        String jwt = buildJwt(ISSUER, expirationDate);

        given(authorizationProperties.getIssuer()).willReturn(ISSUER);
        given(uuidConverter.convertEntity(ACCESS_TOKEN_ID_STRING)).willReturn(ACCESS_TOKEN_ID);
        given(uuidConverter.convertEntity(REFRESH_TOKEN_ID_STRING)).willReturn(REFRESH_TOKEN_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(objectMapper.readValue(eq(ROLES_JSON), any(TypeReference.class))).willReturn(ROLES);

        AccessToken result = underTest.verifyAccessToken(jwt).block();

        assertThat(result.getAccessTokenId()).isEqualTo(ACCESS_TOKEN_ID);
        assertThat(result.getRefreshTokenId()).isEqualTo(REFRESH_TOKEN_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getRoles()).isEqualTo(ROLES);
    }

    private String buildJwt(String issuer, Date expiration) {
        return Jwts.builder()
            .id(ACCESS_TOKEN_ID_STRING)
            .issuer(issuer)
            .subject(USER_ID_STRING)
            .expiration(expiration)
            .claim(Constants.CLAIM_REFRESH_TOKEN_ID, REFRESH_TOKEN_ID_STRING)
            .claim(Constants.CLAIM_ROLES, ROLES_JSON)
            .signWith(keyPair.getPrivate())
            .compact();
    }
}