package com.github.saphyra.apphub.service.community.group.dao.group;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GroupDaoTest {
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final String OWNER_ID_STRING = "owner-id";
    private static final UUID GROUP_ID = UUID.randomUUID();
    private static final String GROUP_ID_STRING = "group-id";

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

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(GROUP_ID)).willReturn(GROUP_ID_STRING);
        given(repository.findById(GROUP_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(group));

        Optional<Group> result = underTest.findById(GROUP_ID);

        assertThat(result).contains(group);
    }

    @Test
    public void findByIdValidated() {
        given(uuidConverter.convertDomain(GROUP_ID)).willReturn(GROUP_ID_STRING);
        given(repository.findById(GROUP_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(group));

        Group result = underTest.findByIdValidated(GROUP_ID);

        assertThat(result).isEqualTo(group);
    }

    @Test
    public void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(GROUP_ID)).willReturn(GROUP_ID_STRING);
        given(repository.findById(GROUP_ID_STRING)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(GROUP_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }
}