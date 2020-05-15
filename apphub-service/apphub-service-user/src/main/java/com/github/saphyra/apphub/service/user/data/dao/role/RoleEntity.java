package com.github.saphyra.apphub.service.user.data.dao.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema = "apphub_user", name = "apphub_role")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RoleEntity {
    @Id
    private String roleId;
    private String userId;

    @Column(name = "apphub_role")
    private String role;
}
