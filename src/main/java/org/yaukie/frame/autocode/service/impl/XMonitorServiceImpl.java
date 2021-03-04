
package org.yaukie.frame.autocode.service.impl;
import org.yaukie.core.base.service.BaseService;
import org.springframework.stereotype.Service;
 import org.yaukie.frame.autocode.dao.mapper.XMonitorMapper;
import org.yaukie.frame.autocode.model.XMonitor;
import org.yaukie.frame.autocode.model.XMonitorExample;
import org.yaukie.frame.autocode.service.api.XMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

        /**
        * @author: yuenbin
        * @create: 2021/01/20 14/27/503
        **/
        @Service
        @Transactional
        public class XMonitorServiceImpl extends BaseService<XMonitorMapper,XMonitor,XMonitorExample> implements XMonitorService {

        @Autowired
        private XMonitorMapper xMonitorMapper;

        }
