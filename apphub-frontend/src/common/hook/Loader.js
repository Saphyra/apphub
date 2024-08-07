import { useEffect } from "react";

const useLoader = (request, mapper, listener = [], condition = () => true, alternativeResult = null) => {
    useEffect(() => loader(), listener);

    const loader = () => {
        if (condition()) {
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