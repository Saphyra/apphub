package com.github.saphyra.apphub.lib.error_handler.service.translation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated
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
