package com.ibg.receipt.vo.api.receiptChild;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 子单列表查询请求vo
 *
 * @author zhou
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptExportRequestVo{

    private String[] creditors;

    private String[] statusList;

}
