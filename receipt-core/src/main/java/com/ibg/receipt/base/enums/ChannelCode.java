package com.ibg.receipt.base.enums;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 资金方和担保方的所有编号
 *
 * @author taixin
 */
public enum ChannelCode {
    XW_BANK_NEW {
        @Override
        public String toString() {
            return "新网银行_新";
        }
    },
    SY_CF {
        @Override
        public String toString() {
            return "盛银消金";
        }
    },
    NJ_BANK {
        @Override
        public String toString() {
            return "南京银行";
        }
    },
    YN_TRUST {
        @Override
        public String toString() {
            return "云信";
        }
    },
    ZHONG_AN {
        @Override
        public String toString() {
            return "众安";
        }
    },
    XW_BANK {
        @Override
        public String toString() {
            return "新网";
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
    SZS_BANK {
        @Override
        public String toString() {
            return "石嘴山银行";
        }
    },
    EB_TRUST {
        @Override
        public String toString() {
            return "光大信托";
        }
    },
    PICC_ABC {
        @Override
        public String toString() {
            return "人保-农行";
        }
    },
    BH_TRUST {
        @Override
        public String toString() {
            return "渤海信托";
        }
    },
    YZT {
        @Override
        public String toString() {
            return "壹账通";
        }
    },
    JMX_CF {
        @Override
        public String toString() {
            return "金美信";
        }
    },
    TB {
        @Override
        public String toString() {
            return "天邦担保";
        }
    },
    HFDB {
        @Override
        public String toString() {
            return "鸿飞担保";
        }
    },
    ZB_BANK {
        @Override
        public String toString() {
            return "众邦银行";
        }
    },
    SOTER {
        @Override
        public String toString() {
            return "厦门融担";
        }
    },
    SD_TRUST {
        @Override
        public String toString() {
            return "山东信托";
        }
    },
    YR {
        @Override
        public String toString() {
            return "亿融";
        }
    },
    YR_FG {
        @Override
        public String toString() {
            return "大秦担保";
        }
    },
    LJ_BANK {
        @Override
        public String toString() {
            return "龙江银行";
        }
    },
    CNIF {
        @Override
        public String toString() {
            return "中保国信";
        }
    },
    BY_CF {
        @Override
        public String toString() {
            return "蒙商消金";
        }
    },
    SGR_FG {
        @Override
        public String toString() {
            return "深国融";
        }
    },
    PDSB_BANK {
        @Override
        public String toString() {
            return "平顶山银行";
        }
    },
    YINDA_FG {
        @Override
        public String toString() {
            return "广东银达";
        }
    },
    SN_BANK {
        @Override
        public String toString() {
            return "苏宁银行";
        }
    },
    AJ_TRUST {
        @Override
        public String toString() {
            return "爱建信托";
        }
    },
    CY_CF {
        @Override
        public String toString() {
            return "长银消金";
        }
    },
    XZ_TRUST {
        @Override
        public String toString() {
            return "西藏信托";
        }
    },
    GM_TRUST {
        @Override
        public String toString() {
            return "国民信托";
        }
    },
    GS_BANK {
        @Override
        public String toString() {
            return "甘肃银行";
        }
    },
    TS_BANK {
        @Override
        public String toString() {
            return "天山银行";
        }
    },
    BHXT_LHBANK {
        @Override
        public String toString() {
            return "蓝海银行";
        }
    },
    QS_BANK {
        @Override
        public String toString() {
            return "齐商银行";
        }
    },
    SN_CF {
        @Override
        public String toString() {
            return "苏宁消金";
        }
    },
    ZSPH_FG {
        @Override
        public String toString() {
            return "中世普惠";
        }
    },
    ZABX_SP {
        @Override
        public String toString() {
            return "众安保险";
        }
    },
    ZRX_FG {
        @Override
        public String toString() {
            return "中融信担保";
        }
    },
    HY_FG {
        @Override
        public String toString() {
            return "昊悦融担";
        }
    },
    GS_BANK_ZL {
        @Override
        public String toString() {
            return "甘肃银行直连";
        }
    },
    BX_BANK_SP {
        @Override
        public String toString() {
            return "百信银行";
        }
    },
    MSYD_SP_XW {
        @Override
        public String toString() {
            return "民生易贷-新网";
        }
    },
    MSYD_SP_MS_FG {
        @Override
        public String toString() {
            return "民生融担";
        }
    },
    QH_GT_TRUST {
        @Override
        public String toString() {
            return "全互-国通信托";
        }
    },
    DS_FG {
        @Override
        public String toString() {
            return "鼎盛融担";
        }
    },
    ZX_BANK {
        @Override
        public String toString() {
            return "振兴银行";
        }
    },
    FOTIC_TRUST {
        @Override
        public String toString() {
            return "外贸信托";
        }
    },
    HD_FG {
        @Override
        public String toString() {
            return "海大富林";
        }
    },
    LH_ZL_BANK {
        @Override
        public String toString() {
            return "蓝海银行-直连";
        }
    },
    TB_FG {
        @Override
        public String toString() {
            return "天邦";
        }
    },
    DY_BANK {
        @Override
        public String toString() {
            return "东营银行";
        }
    },
    YL_BANK {
        @Override
        public String toString() {
            return "亿联银行";
        }
    },
    YINDING {
        @Override
        public String toString() {
            return "银鼎融担";
        }
    },
    HEIKA {
        @Override
        public String toString() {
            return "黑卡小贷";
        }
    },
    CY_CF_ZL {
        @Override
        public String toString() {
            return "长银消金直连";
        }
    },
    WP_CF {
        @Override
        public String toString() {
            return "唯品消金";
        }
    },
    ZZX_FG{
        @Override
        public String toString() {
            return "中智信";
        }
    },
    ZY_CF {
        @Override
        public String toString() {
            return "中原消金-众智融";
        }
    },
    HS_FG {
        @Override
        public String toString() {
            return "汇盛融担";
        }
    },
    XY_CF {
        @Override
        public String toString() {
            return "兴业消金";
        }
    },
    SN_ZB_BANK{
        @Override
        public String toString() {
            return "苏宁众邦";
        }
    },
    LZ_BANK {
        @Override
        public String toString() {
            return "兰州银行";
        }
    },
    JX_FG{
        @Override
        public String toString() {
            return "陕西钧信担保";
        }
    },
    HR_CF {
        @Override
        public String toString() {
            return "海尔消金";
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
    SXTH_FG {
        @Override
        public String toString() {
            return "晟信泰华";
        }

    },
    SX_BANK {
        @Override
        public String toString() {
            return "三湘银行";
        }
    },
    DXAL_BANK {
        @Override
        public String toString() {
            return "大兴安岭农商行";
        }
    },
    ZW_FG {
        @Override
        public String toString() {
            return "中为融担";
        }
    }
    ;

    @Override
    public abstract String toString();

    public static ChannelCode getEnmu(String channelCode) {
        for (ChannelCode code : ChannelCode.values()) {
            if (code.name().equals(channelCode)) {
                return code;
            }
        }
        return null;
    }

    /**
     * 标准码匹配时需要模糊匹配的渠道
     */
    public static EnumSet<ChannelCode> FUZZY_MATCH_CHANNEL = EnumSet.of(YN_TRUST);

    private static EnumSet<ChannelCode> EXCLUDE_REAL_CHANNEL = EnumSet.of(MSYD_SP_MS_FG);

    //获取真正的渠道，比如MSYD_SP_MS_FG（民生融担）、DEMO（测试）就不是真正的渠道
    public static Set<ChannelCode> getRealChannel() {
        return Stream.of(ChannelCode.values()).filter(channelCode -> !EXCLUDE_REAL_CHANNEL.contains(channelCode))
            .collect(Collectors.toSet());
    }
};
