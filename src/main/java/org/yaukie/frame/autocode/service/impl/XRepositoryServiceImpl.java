
package org.yaukie.frame.autocode.service.impl;
import org.yaukie.core.base.service.BaseService;
import org.springframework.stereotype.Service;
 import org.yaukie.frame.autocode.dao.mapper.XRepositoryMapper;
import org.yaukie.frame.autocode.model.XRepository;
import org.yaukie.frame.autocode.model.XRepositoryExample;
import org.yaukie.frame.autocode.service.api.XRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

        /**
        * @author: yuenbin
        * @create: 2020/11/23 19/21/670
        **/
        @Service
        @Transactional
        public class XRepositoryServiceImpl extends BaseService<XRepositoryMapper,XRepository,XRepositoryExample> implements XRepositoryService {

        @Autowired
        private XRepositoryMapper xRepositoryMapper;

        }
