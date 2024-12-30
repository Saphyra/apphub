package com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.NamePercentPair;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.Ring;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ScanJournalMessage extends JournalMessage {
    @JsonProperty("ScanType")
    private String scanType;

    @JsonProperty("DistanceFromArrivalLS")
    private Double distanceFromStar;

    @JsonProperty("Landable")
    private Boolean landable;

    @JsonProperty("Materials")
    private NamePercentPair[] materials;

    @JsonProperty("Parents")
    private Map<String, Integer>[] parents;

    @JsonProperty("PlanetClass")
    private String planetClass;

    @JsonProperty("SurfaceGravity")
    private Double surfaceGravity;

    @JsonProperty("ReserveLevel")
    private String reserveLevel;

    @JsonProperty("Rings")
    private Ring[] rings;

    @JsonProperty("StarType")
    private String startType;

    //Unused
    @JsonProperty("Atmosphere")
    private String atmosphere;

    @JsonProperty("AtmosphereType")
    private String atmosphereType;

    @JsonProperty("AscendingNode")
    private Double ascendingNode;

    @JsonProperty("AxialTilt")
    private Double axialTilt;

    @JsonProperty("Composition")
    private Map<String, Double> composition;

    @JsonProperty("Eccentricity")
    private Double eccentricity;

    @JsonProperty("MassEM")
    private Double mass;

    @JsonProperty("MeanAnomaly")
    private Double meanAnomaly;

    @JsonProperty("OrbitalInclination")
    private Double orbitalInclination;

    @JsonProperty("OrbitalPeriod")
    private Double orbitalPeriod;

    @JsonProperty("Periapsis")
    private Double periapsis;

    @JsonProperty("Radius")
    private Double radius;

    @JsonProperty("RotationPeriod")
    private Double rotationPeriod;

    @JsonProperty("SemiMajorAxis")
    private Double semiMajorAxis;

    @JsonProperty("SurfacePressure")
    private Double surfacePressure;

    @JsonProperty("SurfaceTemperature")
    private Double surfaceTemperature;

    @JsonProperty("TerraformState")
    private String terraformState;

    @JsonProperty("TidalLock")
    private Boolean tidallyLocked;

    @JsonProperty("Volcanism")
    private String volcanism;

    @JsonProperty("WasDiscovered")
    private Boolean wasDiscovered;

    @JsonProperty("WasMapped")
    private Boolean wasMapped;

    @JsonProperty("AbsoluteMagnitude")
    private Double absoluteMagnitude;

    @JsonProperty("Age_MY")
    private Integer age;

    @JsonProperty("Luminosity")
    private String luminosity;

    @JsonProperty("StellarMass")
    private Double stellarMass;

    @JsonProperty("Subclass")
    private Integer subclass;

    @JsonProperty("AtmosphereComposition")
    private NamePercentPair[] atmosphereComposition;
}
