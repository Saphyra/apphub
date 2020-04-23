package com.github.saphyra.apphub.service.platform.main_gateway.filter;

import com.github.saphyra.apphub.lib.common_util.Constants;
import com.netflix.zuul.context.RequestContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LocaleFilterTest {
    @InjectMocks
    private LocaleFilter underTest;

    @Mock
    private RequestContext requestContext;

    @Test
    public void run() {
        RequestContext.testSetCurrentContext(requestContext);

        underTest.run();

        verify(requestContext).addZuulRequestHeader(Constants.LOCALE_HEADER, "hu");
    }
}