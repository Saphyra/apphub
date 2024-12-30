package com.github.saphyra.apphub.service.elite_base.message_processing.saver;

import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Objects.isNull;

@AllArgsConstructor
//TODO unit test
class SearchHelper<R> {
    private final Supplier<Boolean> searchParam; //Return false to skip search
    private final Supplier<Optional<R>> supplier;

    SearchHelper(Object searchParam, Supplier<Optional<R>> supplier){
        this.searchParam = () -> !isNull(searchParam);
        this.supplier = supplier;
    }

    Optional<R> search() {
        if (!searchParam.get()) {
            return Optional.empty();
        }

        return supplier.get();
    }
}
