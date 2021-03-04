
package org.yaukie.frame.autocode.service.impl;
import org.yaukie.core.base.service.BaseService;
import org.springframework.stereotype.Service;
 import org.yaukie.frame.autocode.dao.mapper.XDatabaseMapper;
import org.yaukie.frame.autocode.model.XDatabase;
import org.yaukie.frame.autocode.model.XDatabaseExample;
import org.yaukie.frame.autocode.service.api.XDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

        /**
        * @author: yuenbin
        * @create: 2021/01/29 19/28/716
        **/
        @Service
        @Transactional
        public class XDatabaseServiceImpl extends BaseService<XDatabaseMapper,XDatabase,XDatabaseExample> implements XDatabaseService {

        @Autowired
        private XDatabaseMapper xDatabaseMapper;

        }
