package com.github.saphyra.apphub.service.notebook.dao.pin_group;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PinGroupDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PIN_GROUP_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final String PIN_GROUP_ID_STRING = "pin-group-id";
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private PinGroupRepository repository;

    @Mock
    private PinGroupConverter converter;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private PinGroupDao underTest;

    @Mock
    private PinGroup domain;

    @Mock
    private PinGroupEntity entity;

    @Test
    void save() {
        given(converter.convertDomain(domain)).willReturn(entity);

        underTest.save(domain);

        then(repository).should().save(entity);
    }

    @Test
    void findByIdValidated() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(PIN_GROUP_ID)).willReturn(PIN_GROUP_ID_STRING);
        given(repository.findById(USER_ID_STRING, PIN_GROUP_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        PinGroup result = underTest.findByIdValidated(USER_ID, PIN_GROUP_ID);

        assertThat(result).isEqualTo(domain);
    }

    @Test
    void getByUserId() {
        List<PinGroupEntity> entities = List.of(entity);
        List<PinGroup> domains = List.of(domain);

        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(entities);
        given(converter.convertEntity(entities)).willReturn(domains);

        List<PinGroup> result = underTest.getByUserId(USER_ID);

        assertThat(result).isEqualTo(domains);
    }

    @Test
    void delete() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(PIN_GROUP_ID)).willReturn(PIN_GROUP_ID_STRING);

        underTest.delete(USER_ID, PIN_GROUP_ID);

        then(repository).should().delete(USER_ID_STRING, PIN_GROUP_ID_STRING);
    }

    @Test
    void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        then(repository).should().deleteByUserId(USER_ID_STRING);
    }

    @Test
    void deleteListItemId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));
        given(domain.getListItemIds()).willReturn(Set.of(LIST_ITEM_ID));
        given(converter.convertDomain(List.of(domain))).willReturn(List.of(entity));

        underTest.deleteListItemId(USER_ID, LIST_ITEM_ID);

        then(repository).should().save(List.of(entity));
    }
}