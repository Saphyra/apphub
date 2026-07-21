package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.LabelEventMapping;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EventQueryServiceTest {
	private static final UUID USER_ID = UUID.randomUUID();
	private static final UUID LABEL_ID = UUID.randomUUID();
	private static final UUID EVENT_ID = UUID.randomUUID();

	@Mock
	private EventDao eventDao;

	@Mock
	private EventLabelMappingDao eventLabelMappingDao;

	@Mock
	private EventResponseMapper eventResponseMapper;

	@InjectMocks
	private EventQueryService underTest;

	@Mock
	private Event event;

	@Mock
	private EventResponse eventResponse;

	@Mock
	private LabelEventMapping labelEventMapping;

	@Mock
	private EventLabelMapping eventLabelMapping;

	@Test
	void getEvents_nullLabelId() {
		given(eventDao.getByUserId(USER_ID)).willReturn(List.of(event));
		given(eventResponseMapper.toResponse(USER_ID, List.of(event))).willReturn(List.of(eventResponse));

		List<EventResponse> result = underTest.getEvents(USER_ID, null);

		assertThat(result).containsExactly(eventResponse);
	}

	@Test
	void getEvents_withLabelId() {
		given(eventLabelMappingDao.getEventsOfLabel(USER_ID, LABEL_ID)).willReturn(labelEventMapping);
		Map<UUID, UUID> eventIds = Map.of(EVENT_ID, USER_ID);
		given(labelEventMapping.getEventIds()).willReturn(eventIds);
		given(eventDao.getByIds(USER_ID, eventIds.keySet())).willReturn(List.of(event));
		given(eventResponseMapper.toResponse(USER_ID, List.of(event))).willReturn(List.of(eventResponse));

		List<EventResponse> result = underTest.getEvents(USER_ID, LABEL_ID);

		assertThat(result).containsExactly(eventResponse);
	}

	@Test
	void getLabellessEvents() {
		given(eventLabelMappingDao.getLabelsOfEventsByUserId(USER_ID)).willReturn(List.of(eventLabelMapping));
		given(eventLabelMapping.getEventId()).willReturn(EVENT_ID);
		given(eventLabelMapping.getLabelIds()).willReturn(Map.of());

		given(eventDao.getByIds(USER_ID, List.of(EVENT_ID))).willReturn(List.of(event));
		given(eventResponseMapper.toResponse(event, List.of())).willReturn(eventResponse);

		List<EventResponse> result = underTest.getLabellessEvents(USER_ID);

		assertThat(result).containsExactly(eventResponse);
	}

	@Test
	void getEvent() {
		given(eventDao.findByIdValidated(USER_ID, EVENT_ID)).willReturn(event);
		given(eventResponseMapper.toResponse(USER_ID, event)).willReturn(eventResponse);

		EventResponse result = underTest.getEvent(USER_ID, EVENT_ID);

		assertThat(result).isEqualTo(eventResponse);
	}
}