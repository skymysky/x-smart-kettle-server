package org.yaukie.frame.kettle.quartz;

import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaukie.frame.autocode.model.XJob;
import org.yaukie.frame.kettle.service.JobService;

/**
 * @Author: yuenbin
 * @Date :2020/11/16
 * @Time :10:08
 * @Motto: It is better to be clear than to be clever !
 * @Destrib:
 **/
@Component
public class DefaultExeXJob {

    @Autowired
    private JobService jobService;

    public void doExeJob(XJob xJob) throws KettleException {
        jobService.startJob(xJob);
    }

}
