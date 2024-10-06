import { useEffect } from "react";
import Utils from "../js/Utils";

const useLoader = (request, mapper, listener = [], condition = () => true, alternativeResult = null, errorHandler) => {
    useEffect(() => loader(), listener);

    const loader = () => {
        if (condition()) {
            if(Utils.hasValue(errorHandler)){
                console.log(request)
                request.addErrorHandler(errorHandler)
            }
            const fetch = async () => {
                const response = await request.send();
                mapper(response);
            }
            fetch();
        } else {
            mapper(alternativeResult);
        }
    }
}

export default useLoader;