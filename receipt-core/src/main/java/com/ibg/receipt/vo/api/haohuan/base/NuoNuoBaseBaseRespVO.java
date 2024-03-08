package com.ibg.receipt.vo.api.haohuan.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NuoNuoBaseBaseRespVO<T> {

    private String code;
    private T data;
}
