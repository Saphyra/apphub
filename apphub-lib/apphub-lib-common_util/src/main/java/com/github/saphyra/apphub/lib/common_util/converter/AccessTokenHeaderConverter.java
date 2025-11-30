package com.github.saphyra.apphub.lib.common_util.converter;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessTokenHeaderConverter extends ConverterBase<String, AccessTokenHeader> implements Converter<String, AccessTokenHeader> {
    private final Base64Encoder base64Encoder;
    private final ObjectMapper objectMapper;

    @Override
    public AccessTokenHeader convert(String header) {
        return objectMapper.readValue(base64Encoder.decode(header), AccessTokenHeader.class);
    }

    @Override
    protected AccessTokenHeader processEntityConversion(String entity) {
        return convert(entity);
    }

    @Override
    protected String processDomainConversion(AccessTokenHeader domain) {
        return base64Encoder.encode(objectMapper.writeValueAsString(domain));
    }
}
