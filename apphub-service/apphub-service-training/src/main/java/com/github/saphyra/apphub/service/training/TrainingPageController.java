package com.github.saphyra.apphub.service.training;

import com.github.saphyra.apphub.lib.config.Endpoints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Slf4j
class TrainingPageController {
    @GetMapping(Endpoints.TRAINING_INDEX_PAGE)
    String index() {
        return "index";
    }

    @GetMapping(Endpoints.TRAINING_BOOK_PAGE)
    String book(@PathVariable("book") String book, @PathVariable("chapter") String chapter) {
        log.info("Querying chapter {} of book {}", chapter, book);
        return String.format("book/%s/%s", book, chapter);
    }

    @GetMapping(Endpoints.TRAINING_SAMPLE_PAGE)
    String sample(@PathVariable("book") String book, @PathVariable("sample") String sample) {
        log.info("Querying sample {} of book {}", sample, book);
        return String.format("book/%s/sample/%s", book, sample);
    }
}
