package com.ibg.receipt.enums.business;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PriorityLevelEnum {

    URGENT("紧急", 1),
    NORMAL("正常", 2),
;

    private String desc;

    private int level;

    public int getLevel() {
        return this.level;
    }

    public String getDesc() {
        return this.desc;
    }

    public static PriorityLevelEnum getPriorityLevel(Integer level) {
        if (level == null) {
            return null;
        }

        for (PriorityLevelEnum priorityLevel : PriorityLevelEnum.values()) {
            if (level.equals(priorityLevel.getLevel())) {
                return priorityLevel;
            }
        }
        return null;
    }

}
