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
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
public class GameData {
    private final UUID gameId;
    //MAP
    private final int universeSize;
    @Builder.Default
    private final Coordinates coordinates = new Coordinates(); //TODO create loader
    @Builder.Default
    private final SolarSystems solarSystems = new SolarSystems(); //TODO create loader
    @Builder.Default
    private final Planets planets = new Planets(); //TODO create loader

    //PLANET
    @Builder.Default
    private final Surfaces surfaces = new Surfaces(); //TODO create loader
    @Builder.Default
    private final Buildings buildings = new Buildings(); //TODO create loader
    @Builder.Default
    private final Constructions constructions = new Constructions(); //TODO create loader
    @Builder.Default
    private final Priorities priorities = new Priorities(); //TODO create loader
    @Builder.Default
    private final Deconstructions deconstructions = new Deconstructions(); //TODO create loader

    //STORAGE
    @Builder.Default
    private final AllocatedResources allocatedResources = new AllocatedResources(); //TODO create loader
    @Builder.Default
    private final ReservedStorages reservedStorages = new ReservedStorages(); //TODO create loader
    @Builder.Default
    private final StorageSettings storageSettings = new StorageSettings(); //TODO create loader
    @Builder.Default
    private final StoredResources storedResources = new StoredResources(); //TODO create loader

    //CITIZEN
    @Builder.Default
    private final Citizens citizens = new Citizens(); //TODO create loader
    @Builder.Default
    private final Skills skills = new Skills(); //TODO create loader
    @Builder.Default
    private final SoldierWeapons soldierWeapons = new SoldierWeapons(); //TODO create loader //TODO convert to model
    @Builder.Default
    private final SoldierEnergyShields soldierEnergyShields = new SoldierEnergyShields(); //TODO create loader //TODO convert to model
    @Builder.Default
    private final SoldierArmorPieces soldierArmorPieces = new SoldierArmorPieces(); //TODO create loader //TODO convert to model
    @Builder.Default
    private final Durabilities durabilities = new Durabilities(); //TODO create loader

    //PROCESS
    @Builder.Default
    private final Processes processes = new Processes(); //TODO create loader
    @Builder.Default
    private final BuildingAllocations buildingAllocations = new BuildingAllocations(); //TODO convert to model //TODO create loader
    @Builder.Default
    private final CitizenAllocations citizenAllocations = new CitizenAllocations(); //TODO convert to model //TODO create loader
}
