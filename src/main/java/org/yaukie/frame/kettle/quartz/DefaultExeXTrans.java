package org.yaukie.frame.kettle.quartz;

import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaukie.frame.autocode.model.XTrans;
import org.yaukie.frame.kettle.service.TransService;

/**
 * @Author: yuenbin
 * @Date :2020/11/16
 * @Time :10:08
 * @Motto: It is better to be clear than to be clever !
 * @Destrib:
 **/
@Component
public class DefaultExeXTrans {

    @Autowired
    private TransService transService;

    public void doExeTrans(XTrans xTrans) throws Exception {
        transService.startTrans(xTrans);
    }

}
