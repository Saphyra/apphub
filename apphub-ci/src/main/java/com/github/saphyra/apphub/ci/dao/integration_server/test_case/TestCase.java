package com.github.saphyra.apphub.ci.dao.integration_server.test_case;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

import static com.github.saphyra.apphub.ci.dao.DaoConstants.SCHEMA_INTEGRATION_SERVER;
import static com.github.saphyra.apphub.ci.dao.DaoConstants.TABLE_TEST_CASE;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(schema = SCHEMA_INTEGRATION_SERVER, name = TABLE_TEST_CASE)
public class TestCase {
    @Id
    private String id;

    private String name;

    @CreationTimestamp
    private OffsetDateTime firstRun;

    @UpdateTimestamp
    private OffsetDateTime lastRun;

    private String groups;
}
