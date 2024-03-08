package com.ibg.receipt.service.base;

import com.ibg.receipt.base.enums.FunderChannelCode;
import com.ibg.receipt.service.common.AbstractFactory;
import com.ibg.receipt.service.funder.FunderRepayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StrategyFactory {

    //@Component
    //static class FunderRepayServiceFactory extends AbstractFactory<FunderRepayService> {
    //    @Autowired
    //    protected FunderRepayServiceFactory(List<FunderRepayService> list) {
    //        super(list);
    //    }
    //}
    //
    //@Autowired
    //FunderRepayServiceFactory funderRepayServiceFactory;


    //public FunderRepayService getFunderRepayService(FunderChannelCode funderCode) {
    //    return funderRepayServiceFactory.getService(funderCode.name());
    //}

}
