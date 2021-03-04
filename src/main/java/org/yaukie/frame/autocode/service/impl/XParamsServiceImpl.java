
package org.yaukie.frame.autocode.service.impl;
import org.yaukie.core.base.service.BaseService;
import org.springframework.stereotype.Service;
 import org.yaukie.frame.autocode.dao.mapper.XParamsMapper;
import org.yaukie.frame.autocode.model.XParams;
import org.yaukie.frame.autocode.model.XParamsExample;
import org.yaukie.frame.autocode.service.api.XParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

        /**
        * @author: yuenbin
        * @create: 2021/01/21 15/34/412
        **/
        @Service
        @Transactional
        public class XParamsServiceImpl extends BaseService<XParamsMapper,XParams,XParamsExample> implements XParamsService {

        @Autowired
        private XParamsMapper xParamsMapper;

        }
