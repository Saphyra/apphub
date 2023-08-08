const ValidationResult = class {
    constructor(valid = true, message) {
        this.valid = valid;
        this.message = message;
    }
}

export default ValidationResult;