package com.github.saphyra.apphub.service.community.common;

import com.github.saphyra.apphub.api.community.model.response.SearchResultItem;
import com.github.saphyra.apphub.api.user.model.response.AccountResponse;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountResponseToSearchResultItemConverterTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";

    private final AccountResponseToSearchResultItemConverter underTest = new AccountResponseToSearchResultItemConverter();

    @Test
    public void convert() {
        AccountResponse accountResponse = AccountResponse.builder()
            .userId(USER_ID)
            .username(USERNAME)
            .email(EMAIL)
            .build();

        SearchResultItem result = underTest.convert(accountResponse);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
    }
}