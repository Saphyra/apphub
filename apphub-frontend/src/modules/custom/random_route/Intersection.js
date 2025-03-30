import { randomNumber } from "../../../common/js/Utils";

class Intersection {
    constructor(classes, options) {
        this.classes = classes;
        this.options = options;
    }

    getRandomDirection() {
        return this.options[randomNumber(0, this.options.length - 1)];
    }
}

export const Direction = {
    STRAIGHT: "straight",
    LEFT: "left",
    RIGHT: "right"
}

export const STRAIGHT_OR_LEFT = new Intersection(
    "bottom left straight-or-left",
    [Direction.LEFT, Direction.STRAIGHT]
)

export const STRAIGHT_OR_RIGHT = new Intersection(
    "bottom right straight-or-right",
    [Direction.RIGHT, Direction.STRAIGHT]
)

export const STRAIGHT_LEFT_OR_RIGHT = new Intersection(
    "top left straight-left-or-right",
    [Direction.RIGHT, Direction.STRAIGHT, Direction.LEFT]
)

export const LEFT_OR_RIGHT = new Intersection(
    "top right left-or-right",
    [Direction.RIGHT, Direction.LEFT]
)