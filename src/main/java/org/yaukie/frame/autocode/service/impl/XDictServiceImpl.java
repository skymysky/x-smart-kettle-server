
package org.yaukie.frame.autocode.service.impl;
import org.yaukie.core.base.service.BaseService;
import org.springframework.stereotype.Service;
 import org.yaukie.frame.autocode.dao.mapper.XDictMapper;
import org.yaukie.frame.autocode.model.XDict;
import org.yaukie.frame.autocode.model.XDictExample;
import org.yaukie.frame.autocode.service.api.XDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

        /**
        * @author: yuenbin
        * @create: 2020/12/18 21/13/801
        **/
        @Service
        @Transactional
        public class XDictServiceImpl extends BaseService<XDictMapper,XDict,XDictExample> implements XDictService {

        @Autowired
        private XDictMapper xDictMapper;

        }
