
package org.yaukie.frame.autocode.service.impl;
import org.yaukie.core.base.service.BaseService;
import org.springframework.stereotype.Service;
 import org.yaukie.frame.autocode.dao.mapper.XTransMapper;
import org.yaukie.frame.autocode.model.XTrans;
import org.yaukie.frame.autocode.model.XTransExample;
import org.yaukie.frame.autocode.service.api.XTransService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

        /**
        * @author: yuenbin
        * @create: 2021/02/02 11/07/404
        **/
        @Service
        @Transactional
        public class XTransServiceImpl extends BaseService<XTransMapper,XTrans,XTransExample> implements XTransService {

        @Autowired
        private XTransMapper xTransMapper;

        }
