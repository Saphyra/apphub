package com.github.saphyra.apphub.service.user.data.dao.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

    @Query("SELECT u from UserEntity u WHERE LOWER(u.username) LIKE CONCAT('%', LOWER(:queryString), '%') OR LOWER(u.email) LIKE CONCAT('%', LOWER(:queryString), '%')")
    List<UserEntity> getByUsernameOrEmailContainingIgnoreCase(@Param("queryString") String queryString);
}
