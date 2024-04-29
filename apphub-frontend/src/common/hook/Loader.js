import { useEffect } from "react";

const useLoader = (request, mapper, listener = [], condition = () => true) => {
    useEffect(() => loader(), listener);

    const loader = () => {
        if (condition()) {
            const fetch = async () => {
                const response = await request.send();
                mapper(response);
            }
            fetch();
        }
    }
}

export default useLoader;