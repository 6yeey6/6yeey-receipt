package com.ibg.receipt.vo.api.creditor;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/24 20:31
 */
@Data
public class CreditorConfigQueryResponseVo {

        private String creditor;

        private String creditorName;

        private String creditorConfigVersion;

        private List<String> items;

        private List<String> amounts;

        private Set<String> capitalOperation;

        private String belongOrg;

        private int isReceipt;
}
