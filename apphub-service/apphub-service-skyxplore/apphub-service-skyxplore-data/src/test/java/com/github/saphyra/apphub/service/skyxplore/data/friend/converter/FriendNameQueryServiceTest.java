package com.github.saphyra.apphub.service.skyxplore.data.friend.converter;

import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class FriendNameQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID = UUID.randomUUID();
    private static final String CHARACTER_NAME = "character-name";

    @Mock
    private CharacterDao characterDao;

    @InjectMocks
    private FriendNameQueryService underTest;

    @Mock
    private Friendship friendship;

    @Mock
    private SkyXploreCharacter character;

    @Test
    public void getFriendName() {
        given(friendship.getOtherId(USER_ID)).willReturn(FRIEND_ID);
        given(characterDao.findByIdValidated(FRIEND_ID)).willReturn(character);
        given(character.getName()).willReturn(CHARACTER_NAME);

        String result = underTest.getFriendName(friendship, USER_ID);

        assertThat(result).isEqualTo(CHARACTER_NAME);
    }
}