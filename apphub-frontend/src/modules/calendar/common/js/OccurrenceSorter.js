import { type } from "@testing-library/user-event/dist/type";
import { OccurrenceStatusOrder } from "./OccurrenceStatus";
import { hasValue } from "../../../../common/js/Utils";

function sortOccurrences(a, b) {
    //Compare occurrences based of status
    const typeOrder = OccurrenceStatusOrder[a.status] - OccurrenceStatusOrder[b.status];
    if (typeOrder !== 0) {
        return typeOrder;
    }

    //If status equals, compare based on date
    const dateOrder = a.date.localeCompare(b.date);
    if(dateOrder !== 0){
        return dateOrder;
    }

    //If date equals, and time is available, compare based on time
    if(hasValue(a.time) && hasValue(b.time)){
        return a.time.localeCompare(b.time);
    }

    //Occurrence with time available should come first
    if(hasValue(a.time)){
        return -1;
    }

    if(hasValue(b.time)){
        return 1;
    }

    //Identical occurrences
    return 0;
}

export default sortOccurrences;