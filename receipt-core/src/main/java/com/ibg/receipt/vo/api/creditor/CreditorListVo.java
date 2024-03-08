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
public class CreditorListVo{


    private String creditor;

    private String creditorName;

    private int existCode;
}
