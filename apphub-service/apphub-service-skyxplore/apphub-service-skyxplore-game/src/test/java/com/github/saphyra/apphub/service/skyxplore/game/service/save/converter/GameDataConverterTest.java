package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocationToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizens;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.Coordinates;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.durability.Durabilities;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.durability.DurabilityToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priorities;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skills;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystems;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GameDataConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private CoordinateToModelConverter coordinateToModelConverter;

    @Mock
    private SolarSystemToModelConverter solarSystemToModelConverter;

    @Mock
    private PlanetToModelConverter planetToModelConverter;

    @Mock
    private SurfaceToModelConverter surfaceToModelConverter;

    @Mock
    private BuildingToModelConverter buildingToModelConverter;

    @Mock
    private ConstructionToModelConverter constructionToModelConverter;

    @Mock
    private PriorityToModelConverter priorityToModelConverter;

    @Mock
    private DeconstructionToModelConverter deconstructionToModelConverter;

    @Mock
    private AllocatedResourceToModelConverter allocatedResourceToModelConverter;

    @Mock
    private ReservedStorageToModelConverter reservedStorageToModelConverter;

    @Mock
    private StorageSettingToModelConverter storageSettingToModelConverter;

    @Mock
    private StoredResourceToModelConverter storedResourceToModelConverter;

    @Mock
    private CitizenToModelConverter citizenToModelConverter;

    @Mock
    private SkillToModelConverter skillToModelConverter;

    @Mock
    private DurabilityToModelConverter durabilityToModelConverter;

    @Mock
    private BuildingAllocationToModelConverter buildingAllocationToModelConverter;

    @Mock
    private CitizenAllocationToModelConverter citizenAllocationToModelConverter;

    @InjectMocks
    private GameDataConverter underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Coordinates coordinates;

    @Mock
    private CoordinateModel coordinateModel;

    @Mock
    private SolarSystems solarSystems;

    @Mock
    private SolarSystemModel solarSystemModel;

    @Mock
    private Planet planet;

    @Mock
    private PlanetModel planetModel;

    @Mock
    private Surfaces surfaces;

    @Mock
    private SurfaceModel surfaceModel;

    @Mock
    private Buildings buildings;

    @Mock
    private BuildingModel buildingModel;

    @Mock
    private Constructions constructions;

    @Mock
    private ConstructionModel constructionModel;

    @Mock
    private Priorities priorities;

    @Mock
    private PriorityModel priorityModel;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private DeconstructionModel deconstructionModel;

    @Mock
    private AllocatedResources allocatedResources;

    @Mock
    private AllocatedResourceModel allocatedResourceModel;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private ReservedStorageModel reservedStorageModel;

    @Mock
    private StorageSettings storageSettings;

    @Mock
    private StorageSettingModel storageSettingModel;

    @Mock
    private StoredResources storedResources;

    @Mock
    private StoredResourceModel storedResourceModel;

    @Mock
    private Citizens citizens;

    @Mock
    private CitizenModel citizenModel;

    @Mock
    private Skills skills;

    @Mock
    private SkillModel skillModel;

    @Mock
    private Durabilities durabilities;

    @Mock
    private DurabilityModel durabilityModel;

    @Mock
    private Process process;

    @Mock
    private ProcessModel processModel;

    @Mock
    private BuildingAllocations buildingAllocations;

    @Mock
    private BuildingAllocationModel buildingAllocationModel;

    @Mock
    private CitizenAllocations citizenAllocations;

    @Mock
    private CitizenAllocationModel citizenAllocationModel;

    @Test
    void convert() {
        given(gameData.getCoordinates()).willReturn(coordinates);
        given(coordinateToModelConverter.convert(GAME_ID, coordinates)).willReturn(List.of(coordinateModel));

        given(gameData.getSolarSystems()).willReturn(solarSystems);
        given(solarSystemToModelConverter.convert(GAME_ID, solarSystems)).willReturn(List.of(solarSystemModel));

        Planets planets = CollectionUtils.singleValueMap(UUID.randomUUID(), planet, new Planets());
        given(gameData.getPlanets()).willReturn(planets);
        given(planetToModelConverter.convert(GAME_ID, planets.values())).willReturn(List.of(planetModel));

        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaceToModelConverter.convert(GAME_ID, surfaces)).willReturn(List.of(surfaceModel));

        given(gameData.getBuildings()).willReturn(buildings);
        given(buildingToModelConverter.convert(GAME_ID, buildings)).willReturn(List.of(buildingModel));

        given(gameData.getConstructions()).willReturn(constructions);
        given(constructionToModelConverter.convert(GAME_ID, constructions)).willReturn(List.of(constructionModel));

        given(gameData.getPriorities()).willReturn(priorities);
        given(priorityToModelConverter.convert(GAME_ID, priorities)).willReturn(List.of(priorityModel));

        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructionToModelConverter.convert(GAME_ID, deconstructions)).willReturn(List.of(deconstructionModel));

        given(gameData.getAllocatedResources()).willReturn(allocatedResources);
        given(allocatedResourceToModelConverter.convert(GAME_ID, allocatedResources)).willReturn(List.of(allocatedResourceModel));

        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorageToModelConverter.convert(GAME_ID, reservedStorages)).willReturn(List.of(reservedStorageModel));

        given(gameData.getStorageSettings()).willReturn(storageSettings);
        given(storageSettingToModelConverter.convert(GAME_ID, storageSettings)).willReturn(List.of(storageSettingModel));

        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResourceToModelConverter.convert(GAME_ID, storedResources)).willReturn(List.of(storedResourceModel));

        given(gameData.getCitizens()).willReturn(citizens);
        given(citizenToModelConverter.convert(GAME_ID, citizens)).willReturn(List.of(citizenModel));

        given(gameData.getSkills()).willReturn(skills);
        given(skillToModelConverter.convert(GAME_ID, skills)).willReturn(List.of(skillModel));

        given(gameData.getDurabilities()).willReturn(durabilities);
        given(durabilityToModelConverter.convert(GAME_ID, durabilities)).willReturn(List.of(durabilityModel));

        Processes processes = CollectionUtils.toList(new Processes(), process);
        given(gameData.getProcesses()).willReturn(processes);
        given(process.toModel()).willReturn(processModel);

        given(gameData.getBuildingAllocations()).willReturn(buildingAllocations);
        given(buildingAllocationToModelConverter.convert(GAME_ID, buildingAllocations)).willReturn(List.of(buildingAllocationModel));

        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(citizenAllocationToModelConverter.convert(GAME_ID, citizenAllocations)).willReturn(List.of(citizenAllocationModel));

        List<GameItem> result = underTest.convert(GAME_ID, gameData);

        assertThat(result).containsExactlyInAnyOrder(
            coordinateModel,
            solarSystemModel,
            planetModel,
            surfaceModel,
            buildingModel,
            constructionModel,
            priorityModel,
            deconstructionModel,
            allocatedResourceModel,
            reservedStorageModel,
            storageSettingModel,
            storedResourceModel,
            citizenModel,
            skillModel,
            durabilityModel,
            processModel,
            buildingAllocationModel,
            citizenAllocationModel
        );
    }
}