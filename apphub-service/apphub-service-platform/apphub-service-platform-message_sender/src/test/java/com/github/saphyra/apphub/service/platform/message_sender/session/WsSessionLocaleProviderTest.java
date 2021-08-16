package com.github.saphyra.apphub.service.platform.message_sender.session;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.exception.LoggedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class WsSessionLocaleProviderTest {
    private static final String LOCALE = "locale";

    @InjectMocks
    private WsSessionLocaleProvider underTest;

    @Test(expected = LoggedException.class)
    public void localeCookieNotFound() {
        underTest.getLocale(new HashMap<>());
    }

    @Test
    public void getLocale() {
        Map<String, String> map = new HashMap<>();
        map.put(Constants.LOCALE_COOKIE, LOCALE);

        String result = underTest.getLocale(map);

        assertThat(result).isEqualTo(LOCALE);
    }
}