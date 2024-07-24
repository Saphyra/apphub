import useLoader from "../../../../../common/hook/Loader";
import Endpoints from "../../../../../common/js/dao/dao";

const useLoadSetting = (type, location, callback) => {
    const payload = {
        type: type,
        location: location
    }

    useLoader(
        Endpoints.SKYXPLORE_DATA_GET_SETTING.createRequest(payload),
        response => callback(response.value)
    );
}

export const SettingType = {
    POPULATION_ORDER: "POPULATION_ORDER",
    POPULATION_HIDE: "POPULATION_HIDE",
    PLANET_OVERVIEW_TAB: "PLANET_OVERVIEW_TAB",
}

export default useLoadSetting;