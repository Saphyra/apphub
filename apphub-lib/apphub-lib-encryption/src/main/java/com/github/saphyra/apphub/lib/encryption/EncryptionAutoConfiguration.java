package com.github.saphyra.apphub.lib.encryption;

import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.DefaultStringEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.DoubleEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.LocalDateEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.LocalTimeEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.LongEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.lib.encryption.impl.RevealingStringEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@AutoConfiguration
public class EncryptionAutoConfiguration {
    @Bean
    public PasswordService passwordService() {
        return new PasswordService();
    }

    @Bean
    public DefaultStringEncryptor stringEncryptor(Base64Encoder encoder, ObjectMapperWrapper objectMapperWrapper) {
        return new DefaultStringEncryptor(encoder, objectMapperWrapper);
    }

    @Bean
    @Primary
    @ConditionalOnProperty(value = "encryptionStrategy", havingValue = "revealing")
    RevealingStringEncryptor revealingStringEncryptor(DefaultStringEncryptor defaultStringEncryptor, ObjectMapperWrapper objectMapperWrapper) {
        return new RevealingStringEncryptor(defaultStringEncryptor, objectMapperWrapper);
    }

    @Bean
    public IntegerEncryptor integerEncryptor(StringEncryptor stringEncryptor) {
        return new IntegerEncryptor(stringEncryptor);
    }

    @Bean
    public DoubleEncryptor doubleEncryptor(StringEncryptor stringEncryptor) {
        return new DoubleEncryptor(stringEncryptor);
    }

    @Bean
    public LongEncryptor longEncryptor(StringEncryptor stringEncryptor) {
        return new LongEncryptor(stringEncryptor);
    }

    @Bean
    public BooleanEncryptor booleanEncryptor(StringEncryptor stringEncryptor) {
        return new BooleanEncryptor(stringEncryptor);
    }

    @Bean
    LocalDateEncryptor localDateEncryptor(StringEncryptor stringEncryptor){
        return new LocalDateEncryptor(stringEncryptor);
    }

    @Bean
    LocalTimeEncryptor localTimeEncryptor(StringEncryptor stringEncryptor){
        return new LocalTimeEncryptor(stringEncryptor);
    }
}
