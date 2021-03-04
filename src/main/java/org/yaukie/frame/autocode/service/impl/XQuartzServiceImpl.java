
package org.yaukie.frame.autocode.service.impl;
import org.yaukie.core.base.service.BaseService;
import org.springframework.stereotype.Service;
 import org.yaukie.frame.autocode.dao.mapper.XQuartzMapper;
import org.yaukie.frame.autocode.model.XQuartz;
import org.yaukie.frame.autocode.model.XQuartzExample;
import org.yaukie.frame.autocode.service.api.XQuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

        /**
        * @author: yuenbin
        * @create: 2020/11/13 15/46/284
        **/
        @Service
        @Transactional
        public class XQuartzServiceImpl extends BaseService<XQuartzMapper,XQuartz,XQuartzExample> implements XQuartzService {

        @Autowired
        private XQuartzMapper xQuartzMapper;

        }
