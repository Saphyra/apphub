package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import lombok.AllArgsConstructor;

import java.util.function.Supplier;

@AllArgsConstructor
class UpdateHelper {
    private final Checker checker;
    private final Runnable updater;

    UpdateHelper(Object newValue, Supplier<Object> currentValueProvider, Runnable updater) {
        this.checker = new DefaultChecker(newValue, currentValueProvider);
        this.updater = updater;
    }

    void modify() {
        if (!checker.check()) {
            updater.run();
        }
    }
}
