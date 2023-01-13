package com.github.saphyra.apphub.lib.encryption.configuration;

import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.LongEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptionBeanConfig {
    @Bean
    public PasswordService passwordService() {
        return new PasswordService();
    }

    @Bean
    public StringEncryptor stringEncryptor(Base64Encoder encoder) {
        return new StringEncryptor(encoder);
    }

    @Bean
    public IntegerEncryptor integerEncryptor(StringEncryptor stringEncryptor) {
        return new IntegerEncryptor(stringEncryptor);
    }

    @Bean
    public LongEncryptor longEncryptor(StringEncryptor stringEncryptor) {
        return new LongEncryptor(stringEncryptor);
    }

    @Bean
    public BooleanEncryptor booleanEncryptor(StringEncryptor stringEncryptor) {
        return new BooleanEncryptor(stringEncryptor);
    }
}
