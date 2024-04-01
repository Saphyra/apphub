package com.github.saphyra.apphub.service.utils.sql_generator.dao.query;

import com.github.saphyra.apphub.lib.encryption_dao.EncryptedEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(schema = "utils", name = "sql_generator_query")
class QueryEntity implements EncryptedEntity {
    @Id
    private String queryId;
    private String userId;
    private String queryType;
    private String createdAt;
    private String favorite;
    private String label;

    @Override
    public String getId() {
        return queryId;
    }
}
