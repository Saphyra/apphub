import React from "react";
import ByStatOptions from "./ByStatOptions";
import BySkillOptions from "./BySkillOptions";
import { CitizenComparatorName } from "../../CitizenComparator";
import Utils from "../../../../../../../../../common/js/Utils";

const ComparatorMethodOptions = ({
    citizenComparator,
    selectedStat,
    stats,
    setSelectedStat,
    selectedSkill,
    skills,
    setSelectedSkill
}) => {
    switch (citizenComparator.name) {
        case CitizenComparatorName.BY_NAME:
            return;
        case CitizenComparatorName.BY_STAT:
            return <ByStatOptions
                selectedStat={selectedStat}
                stats={stats}
                setSelectedStat={setSelectedStat}
            />
        case CitizenComparatorName.BY_SKILL:
            return <BySkillOptions
                selectedSkill={selectedSkill}
                skills={skills}
                setSelectedSkill={setSelectedSkill}
            />
        default:
            Utils.throwException("IllegalArgument", "Unhandled CitizenComparator " + citizenComparator.name);
    }
}

export default ComparatorMethodOptions;