package org.yaukie.frame.autocode;

import org.yaukie.core.util.GeneratorUtil;

/**
 * @Author: yuenbin
 * @Date :2020/3/15
 * @Time :20:25
 * @Motto: It is better to be clear than to be clever !
 * @Destrib:
 **/
public class Generator {

    public static void main(String[] args) {
        GeneratorUtil.generator("jdbc:mysql://localhost:3306/xtl",
                "root",
                "root",
                "com.mysql.jdbc.Driver",
                "org.yaukie.frame.autocode",
                "id",
                true,
                "x_template"
        );
    }

}
