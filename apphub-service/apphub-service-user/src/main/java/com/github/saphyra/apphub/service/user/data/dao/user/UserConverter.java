package com.github.saphyra.apphub.service.user.data.dao.user;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter extends ConverterBase<UserEntity, User> {
    private final UuidConverter uuidConverter;

    @Override
    protected User processEntityConversion(UserEntity entity) {
        return User.builder()
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .email(entity.getEmail())
            .username(entity.getUsername())
            .password(entity.getPassword())
            .language(entity.getLanguage())
            .markedForDeletion(entity.isMarkedForDeletion())
            .markedForDeletionAt(entity.getMarkedForDeletionAt())
            .build();
    }

    @Override
    protected UserEntity processDomainConversion(User user) {
        return UserEntity.builder()
            .userId(uuidConverter.convertDomain(user.getUserId()))
            .email(user.getEmail())
            .username(user.getUsername())
            .password(user.getPassword())
            .language(user.getLanguage())
            .markedForDeletion(user.isMarkedForDeletion())
            .markedForDeletionAt(user.getMarkedForDeletionAt())
            .build();
    }
}
