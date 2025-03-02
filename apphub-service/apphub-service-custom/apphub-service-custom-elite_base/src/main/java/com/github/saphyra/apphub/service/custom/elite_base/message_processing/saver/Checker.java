package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

public interface Checker {
    /**
     * @return true, if parameters match. False if update is needed.
     */
    boolean check();
}
