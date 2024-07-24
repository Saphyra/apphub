package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.LocalDateEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.ToolConverter.COLUMN_ACQUIRED_AT;
import static com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.ToolConverter.COLUMN_BRAND;
import static com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.ToolConverter.COLUMN_COST;
import static com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.ToolConverter.COLUMN_NAME;
import static com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.ToolConverter.COLUMN_SCRAPPED_AT;
import static com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.ToolConverter.COLUMN_WARRANTY_EXPIRES_AT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ToolConverterTest {
    private static final UUID TOOL_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String BRAND = "brand";
    private static final String NAME = "name";
    private static final Integer COST = 243;
    private static final LocalDate ACQUIRED_AT = LocalDate.now();
    private static final LocalDate WARRANTY_EXPIRES_AT = ACQUIRED_AT.minusDays(1);
    private static final LocalDate SCRAPPED_AT = WARRANTY_EXPIRES_AT.minusDays(1);
    private static final String USER_ID_FROM_ACCESS_TOKEN = "user-id-from-access-token";
    private static final String TOOL_ID_STRING = "tool-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String ENCRYPTED_BRAND = "encrypted-brand";
    private static final String ENCRYPTED_NAME = "encrypted-name";
    private static final String ENCRYPTED_COST = "encrypted-cost";
    private static final String ENCRYPTED_ACQUIRED_AT = "encrypted-acquired-at";
    private static final String ENCRYPTED_WARRANTY_EXPIRES_AT = "encrypted-warranty-expires-at";
    private static final String ENCRYPTED_SCRAPPED_AT = "encrypted-scrapped-at";

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private IntegerEncryptor integerEncryptor;

    @Mock
    private LocalDateEncryptor localDateEncryptor;

    @InjectMocks
    private ToolConverter underTest;

    @Test
    void convertDomain() {
        Tool domain = Tool.builder()
            .toolId(TOOL_ID)
            .userId(USER_ID)
            .brand(BRAND)
            .name(NAME)
            .cost(COST)
            .acquiredAt(ACQUIRED_AT)
            .warrantyExpiresAt(WARRANTY_EXPIRES_AT)
            .status(ToolStatus.DAMAGED)
            .scrappedAt(SCRAPPED_AT)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);

        given(uuidConverter.convertDomain(TOOL_ID)).willReturn(TOOL_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(stringEncryptor.encrypt(BRAND, USER_ID_FROM_ACCESS_TOKEN, TOOL_ID_STRING, COLUMN_BRAND)).willReturn(ENCRYPTED_BRAND);
        given(stringEncryptor.encrypt(NAME, USER_ID_FROM_ACCESS_TOKEN, TOOL_ID_STRING, COLUMN_NAME)).willReturn(ENCRYPTED_NAME);
        given(integerEncryptor.encrypt(COST, USER_ID_FROM_ACCESS_TOKEN, TOOL_ID_STRING, COLUMN_COST)).willReturn(ENCRYPTED_COST);
        given(localDateEncryptor.encrypt(ACQUIRED_AT, USER_ID_FROM_ACCESS_TOKEN, TOOL_ID_STRING, COLUMN_ACQUIRED_AT)).willReturn(ENCRYPTED_ACQUIRED_AT);
        given(localDateEncryptor.encrypt(WARRANTY_EXPIRES_AT, USER_ID_FROM_ACCESS_TOKEN, TOOL_ID_STRING, COLUMN_WARRANTY_EXPIRES_AT)).willReturn(ENCRYPTED_WARRANTY_EXPIRES_AT);
        given(localDateEncryptor.encrypt(SCRAPPED_AT, USER_ID_FROM_ACCESS_TOKEN, TOOL_ID_STRING, COLUMN_SCRAPPED_AT)).willReturn(ENCRYPTED_SCRAPPED_AT);

        assertThat(underTest.convertDomain(domain))
            .returns(TOOL_ID_STRING, ToolEntity::getToolId)
            .returns(USER_ID_STRING, ToolEntity::getUserId)
            .returns(ENCRYPTED_BRAND, ToolEntity::getBrand)
            .returns(ENCRYPTED_NAME, ToolEntity::getName)
            .returns(ENCRYPTED_COST, ToolEntity::getCost)
            .returns(ENCRYPTED_ACQUIRED_AT, ToolEntity::getAcquiredAt)
            .returns(ENCRYPTED_WARRANTY_EXPIRES_AT, ToolEntity::getWarrantyExpiresAt)
            .returns(ToolStatus.DAMAGED, ToolEntity::getStatus)
            .returns(ENCRYPTED_SCRAPPED_AT, ToolEntity::getScrappedAt);
    }

    @Test
    void convertEntity() {
        ToolEntity domain = ToolEntity.builder()
            .toolId(TOOL_ID_STRING)
            .userId(USER_ID_STRING)
            .brand(ENCRYPTED_BRAND)
            .name(ENCRYPTED_NAME)
            .cost(ENCRYPTED_COST)
            .acquiredAt(ENCRYPTED_ACQUIRED_AT)
            .warrantyExpiresAt(ENCRYPTED_WARRANTY_EXPIRES_AT)
            .status(ToolStatus.DAMAGED)
            .scrappedAt(ENCRYPTED_SCRAPPED_AT)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);

        given(uuidConverter.convertEntity(TOOL_ID_STRING)).willReturn(TOOL_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(stringEncryptor.decrypt(ENCRYPTED_BRAND, USER_ID_FROM_ACCESS_TOKEN, TOOL_ID_STRING, COLUMN_BRAND)).willReturn(BRAND);
        given(stringEncryptor.decrypt(ENCRYPTED_NAME, USER_ID_FROM_ACCESS_TOKEN, TOOL_ID_STRING, COLUMN_NAME)).willReturn(NAME);
        given(integerEncryptor.decrypt(ENCRYPTED_COST, USER_ID_FROM_ACCESS_TOKEN, TOOL_ID_STRING, COLUMN_COST)).willReturn(COST);
        given(localDateEncryptor.decrypt(ENCRYPTED_ACQUIRED_AT, USER_ID_FROM_ACCESS_TOKEN, TOOL_ID_STRING, COLUMN_ACQUIRED_AT)).willReturn(ACQUIRED_AT);
        given(localDateEncryptor.decrypt(ENCRYPTED_WARRANTY_EXPIRES_AT, USER_ID_FROM_ACCESS_TOKEN, TOOL_ID_STRING, COLUMN_WARRANTY_EXPIRES_AT)).willReturn(WARRANTY_EXPIRES_AT);
        given(localDateEncryptor.decrypt(ENCRYPTED_SCRAPPED_AT, USER_ID_FROM_ACCESS_TOKEN, TOOL_ID_STRING, COLUMN_SCRAPPED_AT)).willReturn(SCRAPPED_AT);

        assertThat(underTest.convertEntity(domain))
            .returns(TOOL_ID, Tool::getToolId)
            .returns(USER_ID, Tool::getUserId)
            .returns(BRAND, Tool::getBrand)
            .returns(NAME, Tool::getName)
            .returns(COST, Tool::getCost)
            .returns(ACQUIRED_AT, Tool::getAcquiredAt)
            .returns(WARRANTY_EXPIRES_AT, Tool::getWarrantyExpiresAt)
            .returns(ToolStatus.DAMAGED, Tool::getStatus)
            .returns(SCRAPPED_AT, Tool::getScrappedAt);
    }
}