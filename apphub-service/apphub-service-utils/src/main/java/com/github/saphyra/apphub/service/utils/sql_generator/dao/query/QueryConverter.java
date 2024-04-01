package com.github.saphyra.apphub.service.utils.sql_generator.dao.query;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.utils.model.sql_generator.QueryType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption_dao.EncryptionConverter;
import com.github.saphyra.apphub.lib.encryption_dao.EncryptionService;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class QueryConverter extends EncryptionConverter<QueryEntity, Query> {
    private static final String COLUMN_QUERY_TYPE = "query-type";
    private static final String COLUMN_FAVORITE = "favorite";
    private static final String COLUMN_LABEL = "label";

    private final UuidConverter uuidConverter;
    private final EncryptionService encryptionService;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected QueryEntity processDomainConversion(Query domain) {
        return QueryEntity.builder()
            .queryId(uuidConverter.convertDomain(domain.getQueryId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .queryType(encryptionService.encrypt(
                domain.getQueryType().name(),
                COLUMN_QUERY_TYPE,
                domain.getQueryId(),
                DataType.UTILS_SQL_GENERATOR_QUERY,
                encryptionKey -> accessTokenProvider.get().getUserId().equals(domain.getUserId())
            ))
            .createdAt(domain.getCreatedAt().toString())
            .favorite(encryptionService.encrypt(
                domain.isFavorite(),
                COLUMN_FAVORITE,
                domain.getQueryId(),
                DataType.UTILS_SQL_GENERATOR_QUERY,
                encryptionKey -> accessTokenProvider.get().getUserId().equals(domain.getUserId())
            ))
            .label(encryptionService.encrypt(
                domain.getLabel(),
                COLUMN_LABEL,
                domain.getQueryId(),
                DataType.UTILS_SQL_GENERATOR_QUERY,
                encryptionKey -> accessTokenProvider.get().getUserId().equals(domain.getUserId())
            ))
            .build();
    }

    @Override
    protected Query processEntityConversion(QueryEntity entity) {
        UUID queryId = uuidConverter.convertEntity(entity.getQueryId());
        UUID userId = uuidConverter.convertEntity(entity.getUserId());
        return Query.builder()
            .queryId(queryId)
            .userId(userId)
            .queryType(QueryType.valueOf(decryptQueryType(userId, queryId, entity.getQueryType())))
            .createdAt(LocalDateTime.parse(entity.getCreatedAt()))
            .favorite(encryptionService.decryptBoolean(
                entity.getFavorite(),
                COLUMN_FAVORITE,
                queryId,
                DataType.UTILS_SQL_GENERATOR_QUERY,
                encryptionKey -> accessTokenProvider.get().getUserId().equals(userId)
            ))
            .label(encryptionService.decryptString(
                entity.getLabel(),
                COLUMN_LABEL,
                queryId,
                DataType.UTILS_SQL_GENERATOR_QUERY,
                encryptionKey -> accessTokenProvider.get().getUserId().equals(userId)
            ))
            .build();
    }

    private String decryptQueryType(UUID userId, UUID queryId, String queryType) {
        return encryptionService.decryptString(
            queryType,
            COLUMN_QUERY_TYPE,
            queryId,
            DataType.UTILS_SQL_GENERATOR_QUERY,
            encryptionKey -> accessTokenProvider.get().getUserId().equals(userId)
        );
    }
}
