
package org.yaukie.frame.autocode.service.impl;
import org.yaukie.core.base.service.BaseService;
import org.springframework.stereotype.Service;
 import org.yaukie.frame.autocode.dao.mapper.XTemplateMapper;
import org.yaukie.frame.autocode.model.XTemplate;
import org.yaukie.frame.autocode.model.XTemplateExample;
import org.yaukie.frame.autocode.service.api.XTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

        /**
        * @author: yuenbin
        * @create: 2021/02/18 16/13/964
        **/
        @Service
        @Transactional
        public class XTemplateServiceImpl extends BaseService<XTemplateMapper,XTemplate,XTemplateExample> implements XTemplateService {

        @Autowired
        private XTemplateMapper xTemplateMapper;

        }
