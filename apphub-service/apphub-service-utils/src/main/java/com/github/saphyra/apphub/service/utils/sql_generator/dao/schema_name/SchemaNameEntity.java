package com.github.saphyra.apphub.service.utils.sql_generator.dao.schema_name;

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
@Table(schema = "utils", name = "sql_generator_schema_name")
public class SchemaNameEntity implements EncryptedEntity {
    @Id
    private String schemaNameId;
    private String userId;
    private String externalReference;
    private String schemaName;

    @Override
    public String getId() {
        return schemaNameId;
    }
}
