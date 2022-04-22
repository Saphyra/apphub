package com.github.saphyra.apphub.service.community.blacklist.service;

import com.github.saphyra.apphub.api.community.model.response.blacklist.BlacklistResponse;
import com.github.saphyra.apphub.service.community.blacklist.dao.Blacklist;
import com.github.saphyra.apphub.service.community.blacklist.dao.BlacklistDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class BlacklistQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private BlacklistDao blacklistDao;

    @Mock
    private BlacklistToResponseConverter blacklistToResponseConverter;

    @InjectMocks
    private BlacklistQueryService underTest;

    @Mock
    private BlacklistResponse response;

    @Mock
    private Blacklist blacklist;

    @Test
    public void getBlacklist() {
        given(blacklistDao.getByUserId(USER_ID)).willReturn(List.of(blacklist));
        given(blacklistToResponseConverter.convert(blacklist)).willReturn(response);

        List<BlacklistResponse> result = underTest.getBlacklist(USER_ID);

        assertThat(result).containsExactly(response);
    }
}