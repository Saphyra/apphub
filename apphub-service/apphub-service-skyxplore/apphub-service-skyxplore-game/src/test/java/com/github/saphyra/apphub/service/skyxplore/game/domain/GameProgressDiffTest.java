package com.github.saphyra.apphub.service.skyxplore.game.domain;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class GameProgressDiffTest {
    private static final UUID ITEM_ID_1 = UUID.randomUUID();
    private static final UUID ITEM_ID_2 = UUID.randomUUID();

    private final GameProgressDiff underTest = new GameProgressDiff();

    @Mock
    private GameItem gameItem1;

    @Mock
    private GameItem gameItem2;

    @Mock
    private GameDataProxy gameDataProxy;

    @Captor
    private ArgumentCaptor<List<GameItem>> argumentCaptor;

    @Test
    void save() throws IllegalAccessException {
        given(gameItem1.getId()).willReturn(ITEM_ID_1);

        underTest.save(gameItem1);

        assertThat((Map<UUID, GameItem>) FieldUtils.readDeclaredField(underTest, "items", true)).containsEntry(ITEM_ID_1, gameItem1);
    }

    @Test
    void delete() throws IllegalAccessException {
        given(gameItem1.getId()).willReturn(ITEM_ID_1);

        underTest.save(gameItem1);
        underTest.delete(ITEM_ID_1, GameItemType.CONSTRUCTION);

        assertThat((Map<UUID, GameItem>) FieldUtils.readDeclaredField(underTest, "items", true)).isEmpty();
        assertThat((List<BiWrapper<UUID, GameItemType>>) FieldUtils.readDeclaredField(underTest, "deletedItems", true)).containsExactly(new BiWrapper<>(ITEM_ID_1, GameItemType.CONSTRUCTION));
    }

    @Test
    void process() throws IllegalAccessException {
        given(gameItem1.getId()).willReturn(ITEM_ID_1);
        given(gameItem2.getId()).willReturn(ITEM_ID_2);

        underTest.save(gameItem1);
        underTest.save(gameItem2);
        underTest.delete(ITEM_ID_1, GameItemType.CONSTRUCTION);

        underTest.process(gameDataProxy);

        then(gameDataProxy).should().saveItems(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).containsExactly(gameItem2);
        then(gameDataProxy).should().deleteItems(List.of(new BiWrapper<>(ITEM_ID_1, GameItemType.CONSTRUCTION)));
        assertThat((Map<UUID, GameItem>) FieldUtils.readDeclaredField(underTest, "items", true)).isEmpty();
        assertThat((List<BiWrapper<UUID, GameItemType>>) FieldUtils.readDeclaredField(underTest, "deletedItems", true)).isEmpty();
    }
}