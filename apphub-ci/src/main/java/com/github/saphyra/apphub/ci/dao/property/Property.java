package com.github.saphyra.apphub.ci.dao.property;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.saphyra.apphub.ci.dao.DaoConstants.SCHEMA_CI;
import static com.github.saphyra.apphub.ci.dao.DaoConstants.TABLE_PROPERTY;

@Entity
@Table(schema = SCHEMA_CI, name = TABLE_PROPERTY)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Property {
    @Id
    @Enumerated(EnumType.STRING)
    private PropertyName name;
    private String value;
}
