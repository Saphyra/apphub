package com.github.saphyra.integration.server.domain.test_case;

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

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "test_case")
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
