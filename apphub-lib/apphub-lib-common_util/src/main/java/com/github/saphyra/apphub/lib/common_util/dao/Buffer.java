package com.github.saphyra.apphub.lib.common_util.dao;

import java.time.LocalDateTime;

public interface Buffer {
    void synchronize();

    LocalDateTime getLastSynchronized();

    int getSize();
}
