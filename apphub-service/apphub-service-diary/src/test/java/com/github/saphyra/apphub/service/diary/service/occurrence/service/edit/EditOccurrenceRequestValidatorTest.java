package com.github.saphyra.apphub.service.diary.service.occurrence.service.edit;

import com.github.saphyra.apphub.api.diary.model.EditOccurrenceRequest;
import com.github.saphyra.apphub.api.diary.model.ReferenceDate;
import com.github.saphyra.apphub.service.diary.service.ReferenceDateValidator;
import com.github.saphyra.apphub.service.diary.service.event.service.EventTitleValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EditOccurrenceRequestValidatorTest {
    private static final String TITLE = "title";

    @Mock
    private EventTitleValidator eventTitleValidator;

    @Mock
    private ReferenceDateValidator referenceDateValidator;

    @InjectMocks
    private EditOccurrenceRequestValidator underTest;

    @Mock
    private EditOccurrenceRequest request;

    @Mock
    private ReferenceDate referenceDate;

    @Test
    public void validate() {
        given(request.getTitle()).willReturn(TITLE);
        given(request.getReferenceDate()).willReturn(referenceDate);

        underTest.validate(request);

        verify(eventTitleValidator).validate(TITLE);
        verify(referenceDateValidator).validate(referenceDate);
    }
}