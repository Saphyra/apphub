import Optional from "../../../../common/js/collection/Optional";

const sortTools = (t1, t2) => {
    const t1Type = new Optional(t1.toolType).map(toolType => toolType.name).orElse("").toLowerCase();
    const t2Type = new Optional(t2.toolType).map(toolType => toolType.name).orElse("").toLowerCase();

    if (t1Type === t2Type) {
        return t1.name.toLowerCase().localeCompare(t2.name.toLowerCase());
    }

    return t1Type.localeCompare(t2Type);
}

export default sortTools;