import React, { useEffect, useState } from "react";
import Utils from "../../../../../../../../common/js/Utils";
import { ByNameCitizenComparator, BySkillCitizenComparator, ByStatCitizenComparator, CitizenComparatorName } from "../CitizenComparator";
import SelectInput, { SelectOption } from "../../../../../../../../common/component/input/SelectInput";
import MapStream from "../../../../../../../../common/js/collection/MapStream";
import localizationData from "./citizen_sort_method_selector_localization.json";
import LocalizationHandler from "../../../../../../../../common/js/LocalizationHandler";
import "./citizen_sort_method_selector.css";
import Endpoints from "../../../../../../../../common/js/dao/dao";
import { useQuery } from "react-query";
import Stream from "../../../../../../../../common/js/collection/Stream";
import citizenLocalizationData from "../../../../../common/localization/citizen_localization.json";

//TODO split
const CitizenSortMethodSelector = ({
    citizenComparator,
    setCitizenComparator,
    order,
}) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const citizenLocalizationHandler = new LocalizationHandler(citizenLocalizationData);

    const [selectedStat, setSelectedStat] = useState("MORALE");
    const [selectedSkill, setSelectedSkill] = useState("BUILDING");

    useEffect(() => setCitizenComparator(citizenComparator.withStat(selectedStat)), [selectedStat]);
    useEffect(() => setCitizenComparator(citizenComparator.withSkill(selectedSkill)), [selectedSkill]);

    const [stats, setStats] = useState([]);
    const [skills, setSkills] = useState([]);

    const { data } = useQuery(
        "citizen-stats-and-skills",
        async () => {
            return await Endpoints.SKYXPLORE_DATA_CITIZEN_STATS_AND_SKILLS.createRequest()
                .send()
        },
        {
            staleTime: Infinity,
            cacheTime: Infinity,
        }
    );

    useEffect(
        () => {
            if (Utils.hasValue(data)) {
                setStats(data.stats);
                setSkills(data.skills);
            }
        },
        [data]
    );

    const getMethodSelector = () => {
        return (
            <SelectInput
                id="skyxplore-game-population-comparator-selector"
                value={citizenComparator.name}
                options={getOptions()}
                onchangeCallback={updateComparator}
            />
        );
    }

    const getOptions = () => {
        return new MapStream(CitizenComparatorName)
            .toListStream()
            .map(comparatorName => new SelectOption(
                localizationHandler.get(comparatorName),
                comparatorName
            ))
            .toList();
    }

    const getMethodSelecorOptions = () => {
        switch (citizenComparator.name) {
            case CitizenComparatorName.BY_NAME:
                return;
            case CitizenComparatorName.BY_STAT:
                return getSelector(
                    "skyxplore-game-population-stat-selector",
                    selectedStat,
                    stats,
                    setSelectedStat
                );
            case CitizenComparatorName.BY_SKILL:
                return getSelector(
                    "skyxplore-game-population-skill-selector",
                    selectedSkill,
                    skills,
                    setSelectedSkill
                );
            default:
                Utils.throwException("IllegalArgument", "Unhandled CitizenComparator " + citizenComparator.name);
        }
    }

    const updateComparator = (comparatorName) => {
        setCitizenComparator(getComparatorByName(comparatorName));
    }

    const getComparatorByName = (comparatorName) => {
        switch (comparatorName) {
            case CitizenComparatorName.BY_NAME:
                return new ByNameCitizenComparator(order);
            case CitizenComparatorName.BY_STAT:
                return new ByStatCitizenComparator(order, selectedStat);
            case CitizenComparatorName.BY_SKILL:
                return new BySkillCitizenComparator(order, selectedSkill);
            default:
                Utils.throwException("IllegalArgument", "Unhandled comparatorName " + comparatorName);
        }
    }

    const getSelector = (id, value, source, onchangeCallback) => {
        return (
            <SelectInput
                id={id}
                value={value}
                options={new Stream(source)
                    .sorted((a, b) => citizenLocalizationHandler.get(a).localeCompare(citizenLocalizationHandler.get(b)))
                    .map(item => new SelectOption(
                        citizenLocalizationHandler.get(item),
                        item
                    ))
                    .toList()
                }
                onchangeCallback={onchangeCallback}
            />
        );
    }

    return (
        <div>
            {getMethodSelector()}

            {getMethodSelecorOptions()}
        </div>
    );
}

export default CitizenSortMethodSelector;