package com.github.saphyra.apphub.service.user.settings.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserSettingDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String CATEGORY = "category";
    private static final String USER_ID_STRING = "user-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private UserSettingConverter converter;

    @Mock
    private UserSettingRepository repository;

    @InjectMocks
    private UserSettingDao underTest;

    @Mock
    private UserSetting userSetting;

    @Mock
    private UserSettingEntity entity;

    @Test
    public void getByUserIdAndCategory() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserIdAndCategory(USER_ID_STRING, CATEGORY)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(userSetting));

        List<UserSetting> result = underTest.getByUserIdAndCategory(USER_ID, CATEGORY);

        assertThat(result).containsExactly(userSetting);
    }
}