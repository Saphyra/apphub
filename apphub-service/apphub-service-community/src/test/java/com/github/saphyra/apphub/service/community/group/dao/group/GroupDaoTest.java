package com.github.saphyra.apphub.service.community.group.dao.group;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
public class GroupDaoTest {
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final String OWNER_ID_STRING = "owner-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private GroupConverter converter;

    @Mock
    private GroupRepository repository;

    @InjectMocks
    private GroupDao underTest;

    @Mock
    private GroupEntity entity;

    @Mock
    private Group group;

    @Test
    public void getByOwnerId() {
        given(uuidConverter.convertDomain(OWNER_ID)).willReturn(OWNER_ID_STRING);
        given(repository.getByOwnerId(OWNER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(group));

        List<Group> result = underTest.getByOwnerId(OWNER_ID);

        assertThat(result).containsExactly(group);
    }
}