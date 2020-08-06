package com.github.saphyra.apphub.lib.common_domain;

import java.util.UUID;

public interface DeleteByUserIdDao {
    void deleteByUserId(UUID userId);
}
