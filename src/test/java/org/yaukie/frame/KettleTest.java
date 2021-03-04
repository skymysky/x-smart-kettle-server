package org.yaukie.frame;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author: yuenbin
 * @Date :2020/3/1
 * @Time :16:53
 * @Motto: It is better to be clear than to be clever !
 * @Destrib:
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Start.class} )
public class KettleTest {

    @Autowired
    DataSource dataSource ;

    @Test
    public void contextLoader() throws SQLException {
        System.out.println("数据源>>>>>>" + dataSource.getClass());
        Connection connection = dataSource.getConnection();
        System.out.println("连接>>>>>>>>>" + connection);
        System.out.println("连接地址>>>>>" + connection.getMetaData().getURL());

        DruidDataSource druidDataSource = (DruidDataSource) dataSource;
        System.out.println("druidDataSource 数据源最大连接数：" + druidDataSource.getMaxActive());
        System.out.println("druidDataSource 数据源初始化连接数：" + druidDataSource.getInitialSize());

        connection.close();

    }
}
