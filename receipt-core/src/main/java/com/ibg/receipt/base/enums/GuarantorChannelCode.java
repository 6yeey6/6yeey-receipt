package com.ibg.fund.base.enums;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 担保方渠道编号
 *
 * @author taixin
 */
public enum GuarantorChannelCode {
    ZHONG_AN {
        @Override
        public String toString() {
            return "众安";
        }
    },
    SF {
        @Override
        public String toString() {
            return "集成";
        }
    },
    PICC {
        @Override
        public String toString() {
            return "人保";
        }
    },
    PICC_ABC{
        @Override
        public String toString() {
            return "人保-农行";
        }
    },
    TBTZ {
        @Override
        public String toString() {
            return "天邦代收";
        }
    },
    HFDB {
        @Override
        public String toString() {
            return "鸿飞担保";
        }
    },
    SOTER{
        @Override
        public String toString() {
            return "厦门融担";
        }
    },
    CNIF {
        @Override
        public String toString() {
            return "中保国信";
        }
    },
    SGR_FG{
        @Override
        public String toString() {
            return "深国融融担";
        }
    },
    YINDA_FG {
        @Override
        public String toString() {
            return "广东银达";
        }
    },
    YR_FG {
        @Override
        public String toString() {
            return "大秦丝路担保";
        }
    },
    ZSPH_FG {
        @Override
        public String toString() {
            return "中世普惠";
        }
    },
    HY_FG {
        @Override
        public String toString() {
            return "昊悦担保";
        }
    },
    DS_FG {
        @Override
        public String toString() {
            return "鼎盛融担";
        }
    },
    HD_FG {
        @Override
        public String toString() {
            return "海大富林";
        }
    },
    TB_FG {
        @Override
        public String toString() {
            return "天邦担保";
        }
    },
    YINDING {
        @Override
        public String toString() {
            return "银鼎融担";
        }
    },
    ZRX_FG {
        @Override
        public String toString() {
            return "中融信融担";
        }
    },
    ZZX_FG {
        @Override
        public String toString() {
            return "中智信融担";
        }
    },
    HS_FG {
        @Override
        public String toString() {
            return "汇盛融担";
        }
    },
    JX_FG{
        @Override
        public String toString() {
            return "陕西钧信担保";
        }
    },
    WHCY_FG {
        @Override
        public String toString() {
            return "文化产业融担";
        }
    },
    YNGM_FG {
        @Override
        public String toString() {
            return "云南国茂融担";
        }
    },
    ZW_FG {
        @Override
        public String toString() {
            return "中为融担";
        }
    },
    SXTH_FG {
        @Override
        public String toString() {
            return "晟信泰华";
        }
    },
    ;

    @Override
    public abstract String toString();

    public static Optional<GuarantorChannelCode> parse(String value) {
        return Stream.of(GuarantorChannelCode.values()).filter(e -> e.name().equals(value)).findFirst();
    }
}
