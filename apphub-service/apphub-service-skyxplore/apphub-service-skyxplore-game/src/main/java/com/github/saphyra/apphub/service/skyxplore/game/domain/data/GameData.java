package com.github.saphyra.apphub.service.skyxplore.game.domain.data;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizens;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.Coordinates;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.durability.Durabilities;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priorities;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skills;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystems;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_armor_piece.SoldierArmorPieces;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_data.SoldierWeapons;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_energy_shield.SoldierEnergyShields;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class GameData {
    //MAP
    private final int universeSize;
    @Builder.Default
    private final Coordinates coordinates = new Coordinates();
    @Builder.Default
    private final SolarSystems solarSystems = new SolarSystems();
    @Builder.Default
    private final Planets planets = new Planets();

    //PLANET
    @Builder.Default
    private final Surfaces surfaces = new Surfaces();
    @Builder.Default
    private final Buildings buildings = new Buildings();
    @Builder.Default
    private final Constructions constructions = new Constructions();
    @Builder.Default
    private final Priorities priorities = new Priorities();
    @Builder.Default
    private final Deconstructions deconstructions = new Deconstructions();

    //STORAGE
    @Builder.Default
    private final AllocatedResources allocatedResources = new AllocatedResources();
    @Builder.Default
    private final ReservedStorages reservedStorages = new ReservedStorages();
    @Builder.Default
    private final StorageSettings storageSettings = new StorageSettings();
    @Builder.Default
    private final StoredResources storedResources = new StoredResources();

    //CITIZEN
    @Builder.Default
    private final Citizens citizens = new Citizens();
    @Builder.Default
    private final Skills skills = new Skills();
    @Builder.Default
    private final SoldierWeapons soldierWeapons = new SoldierWeapons();
    @Builder.Default
    private final SoldierEnergyShields soldierEnergyShields = new SoldierEnergyShields();
    @Builder.Default
    private final SoldierArmorPieces soldierArmorPieces = new SoldierArmorPieces();
    @Builder.Default
    private final Durabilities durabilities = new Durabilities();

    //PROCESS
    @Builder.Default
    private final Processes processes = new Processes();
    @Builder.Default
    private final BuildingAllocations buildingAllocations = new BuildingAllocations();
    @Builder.Default
    private final CitizenAllocations citizenAllocations = new CitizenAllocations();
}
