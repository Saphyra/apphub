import { SelectOption } from "../../../../../../common/component/input/SelectInput";

const LandingPad = {
    SMALL: "SMALL",
    MEDIUM: "MEDIUM",
    LARGE: "LARGE",

    getOptions: () => {
        return [
            new SelectOption("S", LandingPad.SMALL),
            new SelectOption("M", LandingPad.MEDIUM),
            new SelectOption("L", LandingPad.LARGE),
        ];
    }
}

export default LandingPad;