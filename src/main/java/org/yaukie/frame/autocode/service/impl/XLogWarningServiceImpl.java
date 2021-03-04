
package org.yaukie.frame.autocode.service.impl;
import org.yaukie.core.base.service.BaseService;
import org.springframework.stereotype.Service;
 import org.yaukie.frame.autocode.dao.mapper.XLogWarningMapper;
import org.yaukie.frame.autocode.model.XLogWarning;
import org.yaukie.frame.autocode.model.XLogWarningExample;
import org.yaukie.frame.autocode.service.api.XLogWarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

        /**
        * @author: yuenbin
        * @create: 2021/01/18 15/58/594
        **/
        @Service
        @Transactional
        public class XLogWarningServiceImpl extends BaseService<XLogWarningMapper,XLogWarning,XLogWarningExample> implements XLogWarningService {

        @Autowired
        private XLogWarningMapper xLogWarningMapper;

        }
