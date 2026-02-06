package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.Relation;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.type.ItemTypeDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.PowerplayState;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

@Disabled //TODO restore
@ExtendWith(MockitoExtension.class)
class CommodityTradingRequestValidatorTest {
    private static final UUID REFERENCE_STAR_ID = UUID.randomUUID();
    private static final String COMMODITY_NAME = "commodity-name";
    private static final Integer MAX_STAR_SYSTEM_DISTANCE = 24;
    private static final Integer MAX_STATION_DISTANCE = 324;
    private static final Duration MAX_TIME_SINCE_LAST_UPDATED = Duration.ZERO;
    private static final Integer MIN_PRICE = 34;
    private static final Integer MAX_PRICE = 35;
    private static final Integer MIN_TRADE_AMOUNT = 5;

    @Mock
    private ItemTypeDao itemTypeDao;

    @InjectMocks
    private CommodityTradingRequestValidator underTest;

    @Test
    void nullReferenceStarId() {
        CommodityTradingRequest request = CommodityTradingRequest.builder()
            .referenceStarId(null)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "referenceStarId", "must not be null");
    }

    @Test
    void invalidCommodity() {
        CommodityTradingRequest request = CommodityTradingRequest.builder()
            .referenceStarId(REFERENCE_STAR_ID)
            .itemName("invalid-commodity-name")
            .build();

        given(itemTypeDao.getItemNames(ItemType.TRADING_TYPES)).willReturn(List.of(COMMODITY_NAME));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "commodity", "must be one of [%s]".formatted(COMMODITY_NAME));
    }

    @Test
    void nullMaxStarSystemDistance() {
        CommodityTradingRequest request = CommodityTradingRequest.builder()
            .referenceStarId(REFERENCE_STAR_ID)
            .itemName(COMMODITY_NAME)
            .maxStarSystemDistance(null)
            .build();

        given(itemTypeDao.getItemNames(ItemType.TRADING_TYPES)).willReturn(List.of(COMMODITY_NAME));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "maxStarSystemDistance", "must not be null");
    }

    @Test
    void nullMaxStationDistance() {
        CommodityTradingRequest request = CommodityTradingRequest.builder()
            .referenceStarId(REFERENCE_STAR_ID)
            .itemName(COMMODITY_NAME)
            .maxStarSystemDistance(MAX_STAR_SYSTEM_DISTANCE)
            .maxStationDistance(null)
            .build();

        given(itemTypeDao.getItemNames(ItemType.TRADING_TYPES)).willReturn(List.of(COMMODITY_NAME));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "maxStationDistance", "must not be null");
    }

    @Test
    void nullIncludeUnknownStationDistance() {
        CommodityTradingRequest request = CommodityTradingRequest.builder()
            .referenceStarId(REFERENCE_STAR_ID)
            .itemName(COMMODITY_NAME)
            .maxStarSystemDistance(MAX_STAR_SYSTEM_DISTANCE)
            .maxStationDistance(MAX_STATION_DISTANCE)
            .includeUnknownStationDistance(null)
            .build();

        given(itemTypeDao.getItemNames(ItemType.TRADING_TYPES)).willReturn(List.of(COMMODITY_NAME));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "includeUnknownStationDistance", "must not be null");
    }

    @Test
    void nullIncludeUnknownLandingPad() {
        CommodityTradingRequest request = CommodityTradingRequest.builder()
            .referenceStarId(REFERENCE_STAR_ID)
            .itemName(COMMODITY_NAME)
            .maxStarSystemDistance(MAX_STAR_SYSTEM_DISTANCE)
            .maxStationDistance(MAX_STATION_DISTANCE)
            .includeUnknownStationDistance(true)
            .includeUnknownLandingPad(null)
            .build();

        given(itemTypeDao.getItemNames(ItemType.TRADING_TYPES)).willReturn(List.of(COMMODITY_NAME));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "includeUnknownLandingPad", "must not be null");
    }

    @Test
    void nullMaxTimeSinceLastUpdated() {
        CommodityTradingRequest request = CommodityTradingRequest.builder()
            .referenceStarId(REFERENCE_STAR_ID)
            .itemName(COMMODITY_NAME)
            .maxStarSystemDistance(MAX_STAR_SYSTEM_DISTANCE)
            .maxStationDistance(MAX_STATION_DISTANCE)
            .includeUnknownStationDistance(true)
            .includeUnknownLandingPad(false)
            .maxTimeSinceLastUpdated(null)
            .build();

        given(itemTypeDao.getItemNames(ItemType.TRADING_TYPES)).willReturn(List.of(COMMODITY_NAME));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "maxTimeSinceLastUpdated", "must not be null");
    }

    @Test
    void nullIncludeSurfaceStations() {
        CommodityTradingRequest request = CommodityTradingRequest.builder()
            .referenceStarId(REFERENCE_STAR_ID)
            .itemName(COMMODITY_NAME)
            .maxStarSystemDistance(MAX_STAR_SYSTEM_DISTANCE)
            .maxStationDistance(MAX_STATION_DISTANCE)
            .includeUnknownStationDistance(true)
            .includeUnknownLandingPad(false)
            .maxTimeSinceLastUpdated(MAX_TIME_SINCE_LAST_UPDATED)
            .includeSurfaceStations(null)
            .build();

        given(itemTypeDao.getItemNames(ItemType.TRADING_TYPES)).willReturn(List.of(COMMODITY_NAME));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "includeSurfaceStations", "must not be null");
    }

    @Test
    void nullIncludeFleetCarriers() {
        CommodityTradingRequest request = CommodityTradingRequest.builder()
            .referenceStarId(REFERENCE_STAR_ID)
            .itemName(COMMODITY_NAME)
            .maxStarSystemDistance(MAX_STAR_SYSTEM_DISTANCE)
            .maxStationDistance(MAX_STATION_DISTANCE)
            .includeUnknownStationDistance(true)
            .includeUnknownLandingPad(false)
            .maxTimeSinceLastUpdated(MAX_TIME_SINCE_LAST_UPDATED)
            .includeSurfaceStations(false)
            .includeFleetCarriers(null)
            .build();

        given(itemTypeDao.getItemNames(ItemType.TRADING_TYPES)).willReturn(List.of(COMMODITY_NAME));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "includeFleetCarriers", "must not be null");
    }

    @Test
    void minPriceTooLow() {
        CommodityTradingRequest request = CommodityTradingRequest.builder()
            .referenceStarId(REFERENCE_STAR_ID)
            .itemName(COMMODITY_NAME)
            .maxStarSystemDistance(MAX_STAR_SYSTEM_DISTANCE)
            .maxStationDistance(MAX_STATION_DISTANCE)
            .includeUnknownStationDistance(true)
            .includeUnknownLandingPad(false)
            .maxTimeSinceLastUpdated(MAX_TIME_SINCE_LAST_UPDATED)
            .includeSurfaceStations(false)
            .includeFleetCarriers(false)
            .minPrice(0)
            .build();

        given(itemTypeDao.getItemNames(ItemType.TRADING_TYPES)).willReturn(List.of(COMMODITY_NAME));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "minPrice", "too low");
    }

    @Test
    void maxPriceTooLow() {
        CommodityTradingRequest request = CommodityTradingRequest.builder()
            .referenceStarId(REFERENCE_STAR_ID)
            .itemName(COMMODITY_NAME)
            .maxStarSystemDistance(MAX_STAR_SYSTEM_DISTANCE)
            .maxStationDistance(MAX_STATION_DISTANCE)
            .includeUnknownStationDistance(true)
            .includeUnknownLandingPad(false)
            .maxTimeSinceLastUpdated(MAX_TIME_SINCE_LAST_UPDATED)
            .includeSurfaceStations(false)
            .includeFleetCarriers(false)
            .minPrice(MIN_PRICE)
            .maxPrice(MIN_PRICE - 1)
            .build();

        given(itemTypeDao.getItemNames(ItemType.TRADING_TYPES)).willReturn(List.of(COMMODITY_NAME));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "maxPrice", "too low");
    }

    @Test
    void nullControllingPowers() {
        CommodityTradingRequest request = CommodityTradingRequest.builder()
            .referenceStarId(REFERENCE_STAR_ID)
            .itemName(COMMODITY_NAME)
            .maxStarSystemDistance(MAX_STAR_SYSTEM_DISTANCE)
            .maxStationDistance(MAX_STATION_DISTANCE)
            .includeUnknownStationDistance(true)
            .includeUnknownLandingPad(false)
            .maxTimeSinceLastUpdated(MAX_TIME_SINCE_LAST_UPDATED)
            .includeSurfaceStations(false)
            .includeFleetCarriers(false)
            .minPrice(MIN_PRICE)
            .maxPrice(MAX_PRICE)
            .controllingPowers(null)
            .build();

        given(itemTypeDao.getItemNames(ItemType.TRADING_TYPES)).willReturn(List.of(COMMODITY_NAME));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "controllingPowers", "must not be null");
    }

    @Test
    void nullControllingPowerRelationWithControllingPowerSpecified() {
        CommodityTradingRequest request = CommodityTradingRequest.builder()
            .referenceStarId(REFERENCE_STAR_ID)
            .itemName(COMMODITY_NAME)
            .maxStarSystemDistance(MAX_STAR_SYSTEM_DISTANCE)
            .maxStationDistance(MAX_STATION_DISTANCE)
            .includeUnknownStationDistance(true)
            .includeUnknownLandingPad(false)
            .maxTimeSinceLastUpdated(MAX_TIME_SINCE_LAST_UPDATED)
            .includeSurfaceStations(false)
            .includeFleetCarriers(false)
            .minPrice(MIN_PRICE)
            .maxPrice(MAX_PRICE)
            .controllingPowers(List.of(Power.NAKATO_KAINE.name()))
            .controllingPowerRelation(null)
            .build();

        given(itemTypeDao.getItemNames(ItemType.TRADING_TYPES)).willReturn(List.of(COMMODITY_NAME));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "controllingPowerRelation", "must not be null");
    }

    @Test
    void nullPowers() {
        CommodityTradingRequest request = CommodityTradingRequest.builder()
            .referenceStarId(REFERENCE_STAR_ID)
            .itemName(COMMODITY_NAME)
            .maxStarSystemDistance(MAX_STAR_SYSTEM_DISTANCE)
            .maxStationDistance(MAX_STATION_DISTANCE)
            .includeUnknownStationDistance(true)
            .includeUnknownLandingPad(false)
            .maxTimeSinceLastUpdated(MAX_TIME_SINCE_LAST_UPDATED)
            .includeSurfaceStations(false)
            .includeFleetCarriers(false)
            .minPrice(MIN_PRICE)
            .maxPrice(MAX_PRICE)
            .controllingPowers(List.of(Power.NAKATO_KAINE.name()))
            .controllingPowerRelation(Relation.ALL_MATCH)
            .powers(null)
            .build();

        given(itemTypeDao.getItemNames(ItemType.TRADING_TYPES)).willReturn(List.of(COMMODITY_NAME));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "powers", "must not be null");
    }

    @Test
    void nullPowersRelationWithPowerSpecified() {
        CommodityTradingRequest request = CommodityTradingRequest.builder()
            .referenceStarId(REFERENCE_STAR_ID)
            .itemName(COMMODITY_NAME)
            .maxStarSystemDistance(MAX_STAR_SYSTEM_DISTANCE)
            .maxStationDistance(MAX_STATION_DISTANCE)
            .includeUnknownStationDistance(true)
            .includeUnknownLandingPad(false)
            .maxTimeSinceLastUpdated(MAX_TIME_SINCE_LAST_UPDATED)
            .includeSurfaceStations(false)
            .includeFleetCarriers(false)
            .minPrice(MIN_PRICE)
            .maxPrice(MAX_PRICE)
            .controllingPowers(Collections.emptyList())
            .controllingPowerRelation(null)
            .powers(List.of(Power.NAKATO_KAINE.name()))
            .powersRelation(null)
            .build();

        given(itemTypeDao.getItemNames(ItemType.TRADING_TYPES)).willReturn(List.of(COMMODITY_NAME));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "powersRelation", "must not be null");
    }

    @Test
    void invalidPowerplayState() {
        CommodityTradingRequest request = CommodityTradingRequest.builder()
            .referenceStarId(REFERENCE_STAR_ID)
            .itemName(COMMODITY_NAME)
            .maxStarSystemDistance(MAX_STAR_SYSTEM_DISTANCE)
            .maxStationDistance(MAX_STATION_DISTANCE)
            .includeUnknownStationDistance(true)
            .includeUnknownLandingPad(false)
            .maxTimeSinceLastUpdated(MAX_TIME_SINCE_LAST_UPDATED)
            .includeSurfaceStations(false)
            .includeFleetCarriers(false)
            .minPrice(MIN_PRICE)
            .maxPrice(MAX_PRICE)
            .controllingPowers(Collections.emptyList())
            .controllingPowerRelation(null)
            .powers(List.of(Power.NAKATO_KAINE.name()))
            .powersRelation(Relation.NONE_MATCH)
            .powerplayState("asd")
            .build();

        given(itemTypeDao.getItemNames(ItemType.TRADING_TYPES)).willReturn(List.of(COMMODITY_NAME));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "powerplayState", "invalid value");
    }

    @Test
    void nullMinTradeAmount() {
        CommodityTradingRequest request = CommodityTradingRequest.builder()
            .referenceStarId(REFERENCE_STAR_ID)
            .itemName(COMMODITY_NAME)
            .maxStarSystemDistance(MAX_STAR_SYSTEM_DISTANCE)
            .maxStationDistance(MAX_STATION_DISTANCE)
            .includeUnknownStationDistance(true)
            .includeUnknownLandingPad(false)
            .maxTimeSinceLastUpdated(MAX_TIME_SINCE_LAST_UPDATED)
            .includeSurfaceStations(false)
            .includeFleetCarriers(false)
            .minPrice(MIN_PRICE)
            .maxPrice(MAX_PRICE)
            .controllingPowers(Collections.emptyList())
            .controllingPowerRelation(null)
            .powers(Collections.emptyList())
            .powersRelation(null)
            .powerplayState(PowerplayState.CONTESTED.name())
            .minTradeAmount(null)
            .build();

        given(itemTypeDao.getItemNames(ItemType.TRADING_TYPES)).willReturn(List.of(COMMODITY_NAME));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "minTradeAmount", "must not be null");
    }

    @Test
    void valid() {
        CommodityTradingRequest request = CommodityTradingRequest.builder()
            .referenceStarId(REFERENCE_STAR_ID)
            .itemName(COMMODITY_NAME)
            .maxStarSystemDistance(MAX_STAR_SYSTEM_DISTANCE)
            .maxStationDistance(MAX_STATION_DISTANCE)
            .includeUnknownStationDistance(true)
            .includeUnknownLandingPad(false)
            .maxTimeSinceLastUpdated(MAX_TIME_SINCE_LAST_UPDATED)
            .includeSurfaceStations(false)
            .includeFleetCarriers(false)
            .minPrice(MIN_PRICE)
            .maxPrice(MAX_PRICE)
            .controllingPowers(Collections.emptyList())
            .controllingPowerRelation(null)
            .powers(Collections.emptyList())
            .powersRelation(null)
            .powerplayState(PowerplayState.CONTESTED.name())
            .minTradeAmount(MIN_TRADE_AMOUNT)
            .build();

        given(itemTypeDao.getItemNames(ItemType.TRADING_TYPES)).willReturn(List.of(COMMODITY_NAME));

        underTest.validate(request);
    }
}