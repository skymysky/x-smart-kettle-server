
package org.yaukie.frame.autocode.service.impl;
import org.yaukie.core.base.service.BaseService;
import org.springframework.stereotype.Service;
 import org.yaukie.frame.autocode.dao.mapper.XLogMapper;
import org.yaukie.frame.autocode.model.XLog;
import org.yaukie.frame.autocode.model.XLogExample;
import org.yaukie.frame.autocode.service.api.XLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

        /**
        * @author: yuenbin
        * @create: 2020/11/11 14/28/274
        **/
        @Service
        @Transactional
        public class XLogServiceImpl extends BaseService<XLogMapper,XLog,XLogExample> implements XLogService {

        @Autowired
        private XLogMapper xLogMapper;

        }
