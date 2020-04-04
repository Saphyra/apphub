package com.github.saphyra.apphub.lib.common_util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class UuidConverterTest {
    private static final UUID ID = UUID.randomUUID();
    private static final String ID_STRING = ID.toString();

    @InjectMocks
    private UuidConverter underTest;

    @Test
    public void convertDomain() {
        String result = underTest.convertDomain(ID);

        assertThat(result).isEqualTo(ID_STRING);
    }

    @Test
    public void convertEntity() {
        UUID result = underTest.convertEntity(ID_STRING);

        assertThat(result).isEqualTo(ID);
    }
}