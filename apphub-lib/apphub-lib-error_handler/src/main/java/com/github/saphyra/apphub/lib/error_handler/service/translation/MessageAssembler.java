package com.github.saphyra.apphub.lib.error_handler.service.translation;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class MessageAssembler {
    String assembleMessage(String localizedMessage, Map<String, String> params) {
        String result = localizedMessage;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = String.format("${%s}", entry.getKey());
            result = result.replace(key, entry.getValue());
        }
        log.debug("Result: {}", result);
        return result;
    }
}
