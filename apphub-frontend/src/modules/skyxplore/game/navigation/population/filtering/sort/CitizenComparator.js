import Utils from "../../../../../../../common/js/Utils";

export const CitizenComparatorName = {
    BY_NAME: "by-name",
    BY_STAT: "by-stat",
    BY_SKILL: "by-skill",
}

export const ByNameCitizenComparator = class {
    constructor(order = Utils.throwException("IllegalArgument", "Order must be specified.")) {
        this.order = order;
        this.name = CitizenComparatorName.BY_NAME;
    }

    compare(a, b) {
        return this.order.modify(a.name.localeCompare(b.name));
    }

    withOrder(order) {
        return new ByNameCitizenComparator(order);
    }

    withStat() {
        return this;
    }

    withSkill() {
        return this;
    }
}

export const ByStatCitizenComparator = class {
    constructor(
        order = Utils.throwException("IllegalArgument", "Order must be specified."),
        stat = Utils.throwException("IllegalArgument", "Stat must be specified.")
    ) {
        this.order = order;
        this.stat = stat;
        this.name = CitizenComparatorName.BY_STAT;
    }

    compare(a, b) {
        return this.order.modify(a.stats[this.stat].value - b.stats[this.stat].value);
    }

    withOrder(order) {
        return new ByStatCitizenComparator(order, this.stat);
    }

    withStat(stat) {
        return new ByStatCitizenComparator(this.order, stat);
    }

    withSkill() {
        return this;
    }
}

export const BySkillCitizenComparator = class {
    constructor(
        order = Utils.throwException("IllegalArgument", "Order must be specified."),
        skill = Utils.throwException("IllegalArgument", "Skill must be specified.")
    ) {
        this.order = order;
        this.skill = skill;
        this.name = CitizenComparatorName.BY_SKILL;
    }

    compare(a, b) {
        const levelComparison = a.skills[this.skill].level - b.skills[this.skill].level;

        if(levelComparison === 0){
            return this.order.modify(a.skills[this.skill].experience - b.skills[this.skill].experience);
        }

        return this.order.modify(levelComparison);
    }

    withOrder(order) {
        return new BySkillCitizenComparator(order, this.skill);
    }

    withStat() {
        return this;
    }

    withSkill(skill) {
        return new BySkillCitizenComparator(this.order, skill);
    }
}