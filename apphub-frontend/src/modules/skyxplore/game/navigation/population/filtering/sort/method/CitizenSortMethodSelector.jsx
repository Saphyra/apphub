import React, { useEffect, useState } from "react";
import { ByNameCitizenComparator, BySkillCitizenComparator, ByStatCitizenComparator, CitizenComparatorName } from "../CitizenComparator";
import localizationData from "./citizen_sort_method_selector_localization.json";
import LocalizationHandler from "../../../../../../../../common/js/LocalizationHandler";
import "./citizen_sort_method_selector.css";
import Endpoints from "../../../../../../../../common/js/dao/dao";
import useCache from "../../../../../../../../common/hook/Cache";
import CacheKey from "../../../../../common/constants/CacheKey";
import MethodSelect from "./MethodSelect";
import ComparatorMethodOptions from "./options/ComparatorMethodOptions";
import { hasValue, throwException } from "../../../../../../../../common/js/Utils";

const CitizenSortMethodSelector = ({
    citizenComparator,
    setCitizenComparator,
    order,
}) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [selectedStat, setSelectedStat] = useState("MORALE");
    const [selectedSkill, setSelectedSkill] = useState("BUILDING");

    useEffect(
        () => {
            if (hasValue(citizenComparator.stat)) {
                setSelectedStat(citizenComparator.stat);
            }
        },
        [citizenComparator]
    );
    useEffect(
        () => {
            if (hasValue(citizenComparator.skill)) {
                setSelectedSkill(citizenComparator.skill);
            }
        },
        [citizenComparator]
    );
    useEffect(() => setCitizenComparator(citizenComparator.withStat(selectedStat)), [selectedStat]);
    useEffect(() => setCitizenComparator(citizenComparator.withSkill(selectedSkill)), [selectedSkill]);

    const [stats, setStats] = useState([]);
    const [skills, setSkills] = useState([]);

    useCache(
        CacheKey.CITIZEN_STATS_AND_SKILLS,
        Endpoints.SKYXPLORE_DATA_CITIZEN_STATS_AND_SKILLS.createRequest(),
        response => {
            setStats(response.stats);
            setSkills(response.skills);
        }
    );

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
                throwException("IllegalArgument", "Unhandled comparatorName " + comparatorName);
        }
    }

    return (
        <div>
            <MethodSelect
                localizationHandler={localizationHandler}
                citizenComparator={citizenComparator}
                updateComparator={updateComparator}
            />

            <ComparatorMethodOptions
                citizenComparator={citizenComparator}
                selectedStat={selectedStat}
                stats={stats}
                setSelectedStat={setSelectedStat}
                selectedSkill={selectedSkill}
                skills={skills}
                setSelectedSkill={setSelectedSkill}
            />
        </div>
    );
}

export default CitizenSortMethodSelector;