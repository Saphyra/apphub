package com.github.saphyra.apphub.service.admin_panel.migration_task.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "admin_panel", name = "migration_task")
class MigrationTaskEntity {
    @Id
    private String event;
    private String name;
    private Boolean completed;
    private Boolean repeatable;
}
