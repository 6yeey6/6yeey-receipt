package com.ibg.receipt.vo.api.nuonuo.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NuoNuoBaseRespVO<T> {

    private String code;
    private String describe;
    private T result;
}
