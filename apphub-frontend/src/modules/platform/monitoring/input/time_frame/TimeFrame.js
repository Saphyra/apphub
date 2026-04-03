import Stream from "../../../../../common/js/collection/Stream";

export const SECOND = "SECOND";
export const MINUTE = "MINUTE";
export const HOUR = "HOUR";

export const TIME_FRAMES = [SECOND, MINUTE, HOUR];

const TimeFrame = new Stream(TIME_FRAMES)
    .toMap(timeFrame => timeFrame);

export default TimeFrame;