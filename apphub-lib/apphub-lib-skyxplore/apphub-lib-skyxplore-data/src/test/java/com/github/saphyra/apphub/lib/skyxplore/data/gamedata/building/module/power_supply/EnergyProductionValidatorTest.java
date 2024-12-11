package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.power_supply;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EnergyProductionValidatorTest {
    private static final String FUEL = "fuel";

    @Spy
    private ResourceDataService resourceDataService = new ResourceDataService();

    @InjectMocks
    private EnergyProductionValidator underTest;

    @Mock
    private EnergyProduction energyProduction;

    @Mock
    private ResourceData resourceData;

    @Test
    void nullProductions() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(null), "productions", "must not be null");
    }

    @Test
    void blankId() {
        given(energyProduction.getId()).willReturn(" ");

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(energyProduction)), "id", "must not be null or blank");
    }

    @Test
    void energyPerBatchTooLow() {
        given(energyProduction.getId()).willReturn("id");
        given(energyProduction.getEnergyPerBatch()).willReturn(-1);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(energyProduction)), "energyPerBatch", "too low");
    }

    @Test
    void batchTicksTooLow() {
        given(energyProduction.getId()).willReturn("id");
        given(energyProduction.getEnergyPerBatch()).willReturn(0);
        given(energyProduction.getBatchTicks()).willReturn(-1);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(energyProduction)), "batchTicks", "too low");
    }

    @Test
    void fuelLastsForTicksTooLow() {
        given(energyProduction.getId()).willReturn("id");
        given(energyProduction.getEnergyPerBatch()).willReturn(0);
        given(energyProduction.getBatchTicks()).willReturn(0);
        given(energyProduction.getFuelLastsForTicks()).willReturn(-1);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(energyProduction)), "fuelLastsForTicks", "too low");
    }

    @Test
    void fuelStorageTooLow() {
        given(energyProduction.getId()).willReturn("id");
        given(energyProduction.getEnergyPerBatch()).willReturn(0);
        given(energyProduction.getBatchTicks()).willReturn(0);
        given(energyProduction.getFuelLastsForTicks()).willReturn(0);
        given(energyProduction.getFuelStorage()).willReturn(-1);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(energyProduction)), "fuelStorage", "too low");
    }

    @Test
    void nullHumanPowered() {
        given(energyProduction.getId()).willReturn("id");
        given(energyProduction.getEnergyPerBatch()).willReturn(0);
        given(energyProduction.getBatchTicks()).willReturn(0);
        given(energyProduction.getFuelLastsForTicks()).willReturn(0);
        given(energyProduction.getFuelStorage()).willReturn(0);
        given(energyProduction.getHumanPowered()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(energyProduction)), "humanPowered", "must not be null");
    }

    @Test
    void fuelDoesNotExist() {
        given(energyProduction.getId()).willReturn("id");
        given(energyProduction.getEnergyPerBatch()).willReturn(0);
        given(energyProduction.getBatchTicks()).willReturn(0);
        given(energyProduction.getFuelLastsForTicks()).willReturn(0);
        given(energyProduction.getFuelStorage()).willReturn(0);
        given(energyProduction.getHumanPowered()).willReturn(false);
        given(energyProduction.getFuel()).willReturn(FUEL);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(energyProduction)), "fuel=%s".formatted(FUEL), "invalid value");
    }

    @Test
    void valid() {
        given(energyProduction.getId()).willReturn("id");
        given(energyProduction.getEnergyPerBatch()).willReturn(0);
        given(energyProduction.getBatchTicks()).willReturn(0);
        given(energyProduction.getFuelLastsForTicks()).willReturn(0);
        given(energyProduction.getFuelStorage()).willReturn(0);
        given(energyProduction.getHumanPowered()).willReturn(false);
        given(energyProduction.getFuel()).willReturn(FUEL);

        resourceDataService.put(FUEL, resourceData);

        underTest.validate(List.of(energyProduction));
    }
}