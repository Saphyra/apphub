package com.github.saphyra.apphub.lib.common_util.converter;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessTokenHeaderConverter extends ConverterBase<String, AccessToken> implements Converter<String, AccessToken> {
    private final Base64Encoder base64Encoder;
    private final ObjectMapper objectMapper;

    @Override
    public AccessToken convert(String header) {
        return objectMapper.readValue(base64Encoder.decode(header), AccessToken.class);
    }

    @Override
    protected AccessToken processEntityConversion(String entity) {
        return convert(entity);
    }

    @Override
    protected String processDomainConversion(AccessToken domain) {
        return base64Encoder.encode(objectMapper.writeValueAsString(domain));
    }
}
