package com.ibg.receipt.sensitive.impl;

import com.ibg.receipt.sensitive.SensitiveExecute;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author zhangxiusen
 * @date 20-7-21
 */
@Component
public class SensitiveExecuteImpl implements SensitiveExecute {

    @Override
    public boolean execute() {
        return true;
    }
}
