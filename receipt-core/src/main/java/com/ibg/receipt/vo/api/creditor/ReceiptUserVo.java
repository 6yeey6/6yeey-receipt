package com.ibg.receipt.vo.api.creditor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptUserVo implements Serializable {

    private static final long serialVersionUID = 6688874867112067252L;

    private String userName;

    private String email;

}
