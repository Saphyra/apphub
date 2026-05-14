package com.github.saphyra.apphub.service.user.data.dao.user_deprecated;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Deprecated(forRemoval = true)
class UserConverter extends ConverterBase<UserEntity, DeprecatedUser> {
    private final UuidConverter uuidConverter;

    @Override
    protected DeprecatedUser processEntityConversion(UserEntity entity) {
        return DeprecatedUser.builder()
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .email(entity.getEmail())
            .username(entity.getUsername())
            .password(entity.getPassword())
            .language(entity.getLanguage())
            .markedForDeletion(Optional.ofNullable(entity.getMarkedForDeletion()).orElse(false))
            .markedForDeletionAt(entity.getMarkedForDeletionAt())
            .passwordFailureCount(entity.getPasswordFailureCount())
            .lockedUntil(entity.getLockedUntil())
            .build();
    }

    @Override
    protected UserEntity processDomainConversion(DeprecatedUser user) {
        return UserEntity.builder()
            .userId(uuidConverter.convertDomain(user.getUserId()))
            .email(user.getEmail())
            .username(user.getUsername())
            .password(user.getPassword())
            .language(user.getLanguage())
            .markedForDeletion(user.isMarkedForDeletion())
            .markedForDeletionAt(user.getMarkedForDeletionAt())
            .passwordFailureCount(user.getPasswordFailureCount())
            .lockedUntil(user.getLockedUntil())
            .build();
    }
}
