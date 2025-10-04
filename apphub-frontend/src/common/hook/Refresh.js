import { useState } from "react";

const useRefresh = (initialCount = 0) => {
    const [refreshCount, setRefreshCount] = useState(initialCount);

    const refresh = (increment = 1) => {
        setRefreshCount(refreshCount + increment);
    }

    const reset = () => {
        setRefreshCount(0);
    }

    return [refreshCount, refresh, reset];
}

export default useRefresh;