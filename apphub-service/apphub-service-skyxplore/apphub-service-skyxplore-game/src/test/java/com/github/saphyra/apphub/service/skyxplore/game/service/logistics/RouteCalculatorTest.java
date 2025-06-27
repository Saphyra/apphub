package com.github.saphyra.apphub.service.skyxplore.game.service.logistics;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinateFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request.ResourceDeliveryRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request.ResourceDeliveryRequests;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class RouteCalculatorTest {
    private static final UUID RESOURCE_DELIVERY_REQUEST_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID REFERENCE_ID = UUID.randomUUID();
    private static final Integer WEIGHT_LOW = 1;
    private static final Integer WEIGHT_HIGH = 10;

    @Mock
    private MapWeighter mapWeighter;

    @Mock
    private ReferredCoordinateFactory referredCoordinateFactory;

    @Mock
    private CoordinateFinder coordinateFinder;

    @InjectMocks
    private RouteCalculator underTest;

    @Mock
    private ResourceDeliveryRequest resourceDeliveryRequest;

    @Mock
    private ResourceDeliveryRequests resourceDeliveryRequests;

    @Mock
    private StoredResource storedResource;

    @Mock
    private StoredResources storedResources;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private Coordinate coordinate1;

    @BeforeEach
    void setUp() {
        given(gameData.getResourceDeliveryRequests()).willReturn(resourceDeliveryRequests);
        given(resourceDeliveryRequests.findByIdValidated(RESOURCE_DELIVERY_REQUEST_ID)).willReturn(resourceDeliveryRequest);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.findByAllocatedByValidated(RESOURCE_DELIVERY_REQUEST_ID)).willReturn(storedResource);
        given(resourceDeliveryRequest.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);
        given(referredCoordinateFactory.save(eq(progressDiff), eq(gameData), eq(REFERENCE_ID), any(), any()))
            .willAnswer(invocation ->
                ReferredCoordinate.builder()
                    .referenceId(invocation.getArgument(2, UUID.class))
                    .coordinate(invocation.getArgument(3, Coordinate.class))
                    .order(invocation.getArgument(4, Integer.class))
                    .build()
            );
    }

    @Test
    void startCoordinateEqualsEndCoordinate() {
        given(coordinateFinder.getCoordinateByStoredResource(gameData, storedResource)).willReturn(coordinate1);
        given(coordinateFinder.getCoordinateByReservedStorageId(gameData, RESERVED_STORAGE_ID)).willReturn(coordinate1);
        given(mapWeighter.getWeightedMap(gameData, LOCATION)).willReturn(Map.of(coordinate1, WEIGHT_LOW));

        CustomAssertions.singleListAssertThat(underTest.calculateAndSaveForResourceDeliveryRequestId(progressDiff, gameData, LOCATION, REFERENCE_ID, RESOURCE_DELIVERY_REQUEST_ID))
            .returns(REFERENCE_ID, ReferredCoordinate::getReferenceId)
            .returns(coordinate1, ReferredCoordinate::getCoordinate)
            .returns(0, ReferredCoordinate::getOrder);
    }

    @Test
    void shortestRouteFound() {
        Coordinate c00 = new Coordinate(0, 0);
        Coordinate c01 = new Coordinate(0, 1);
        Coordinate c02 = new Coordinate(0, 2);
        Coordinate c10 = new Coordinate(1, 0);
        Coordinate c11 = new Coordinate(1, 1);
        Coordinate c12 = new Coordinate(1, 2);
        Coordinate c20 = new Coordinate(2, 0);
        Coordinate c21 = new Coordinate(2, 1);
        Coordinate c22 = new Coordinate(2, 2);

        given(coordinateFinder.getCoordinateByStoredResource(gameData, storedResource)).willReturn(c00);
        given(coordinateFinder.getCoordinateByReservedStorageId(gameData, RESERVED_STORAGE_ID)).willReturn(c02);
        given(mapWeighter.getWeightedMap(gameData, LOCATION)).willReturn(Map.of(
            c00, WEIGHT_LOW,
            c01, WEIGHT_HIGH,
            c02, WEIGHT_LOW,
            c10, WEIGHT_LOW,
            c11, WEIGHT_HIGH,
            c12, WEIGHT_LOW,
            c20, WEIGHT_LOW,
            c21, WEIGHT_LOW,
            c22, WEIGHT_LOW
        ));

        Coordinate[] expectedRoute = new Coordinate[]{c00, c10, c20, c21, c22, c12, c02};

        assertThat(underTest.calculateAndSaveForResourceDeliveryRequestId(progressDiff, gameData, LOCATION, REFERENCE_ID, RESOURCE_DELIVERY_REQUEST_ID))
            .extracting(ReferredCoordinate::getCoordinate)
            .containsExactly(expectedRoute);

        for (int i = 0; i < expectedRoute.length; i++) {
            then(referredCoordinateFactory).should().save(progressDiff, gameData, REFERENCE_ID, expectedRoute[i], i);
        }
    }
}