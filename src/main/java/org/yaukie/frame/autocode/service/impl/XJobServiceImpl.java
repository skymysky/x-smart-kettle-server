
package org.yaukie.frame.autocode.service.impl;
import org.yaukie.core.base.service.BaseService;
import org.springframework.stereotype.Service;
 import org.yaukie.frame.autocode.dao.mapper.XJobMapper;
import org.yaukie.frame.autocode.model.XJob;
import org.yaukie.frame.autocode.model.XJobExample;
import org.yaukie.frame.autocode.service.api.XJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

        /**
        * @author: yuenbin
        * @create: 2021/02/02 11/07/404
        **/
        @Service
        @Transactional
        public class XJobServiceImpl extends BaseService<XJobMapper,XJob,XJobExample> implements XJobService {

        @Autowired
        private XJobMapper xJobMapper;

        }
