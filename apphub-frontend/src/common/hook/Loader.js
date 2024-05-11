import { useEffect } from "react";

const useLoader = (request, mapper, listener = [], condition = () => true, alternateiveResult = null) => {
    useEffect(() => loader(), listener);

    const loader = () => {
        if (condition()) {
            const fetch = async () => {
                const response = await request.send();
                mapper(response);
            }
            fetch();
        } else {
            mapper(alternateiveResult);
        }
    }
}

export default useLoader;