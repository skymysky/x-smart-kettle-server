package org.yaukie.frame.autocode.dao.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.yaukie.frame.autocode.model.XQuartz;
import org.yaukie.frame.autocode.model.XQuartzExample;

public interface XQuartzMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_quartz
     *
     * @mbg.generated Fri Nov 13 15:46:31 CST 2020
     */
    long countByExample(XQuartzExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_quartz
     *
     * @mbg.generated Fri Nov 13 15:46:31 CST 2020
     */
    int deleteByExample(XQuartzExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_quartz
     *
     * @mbg.generated Fri Nov 13 15:46:31 CST 2020
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_quartz
     *
     * @mbg.generated Fri Nov 13 15:46:31 CST 2020
     */
    int insert(XQuartz record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_quartz
     *
     * @mbg.generated Fri Nov 13 15:46:31 CST 2020
     */
    int insertSelective(XQuartz record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_quartz
     *
     * @mbg.generated Fri Nov 13 15:46:31 CST 2020
     */
    List<XQuartz> selectByExample(XQuartzExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_quartz
     *
     * @mbg.generated Fri Nov 13 15:46:31 CST 2020
     */
    XQuartz selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_quartz
     *
     * @mbg.generated Fri Nov 13 15:46:31 CST 2020
     */
    int updateByExampleSelective(@Param("record") XQuartz record, @Param("example") XQuartzExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_quartz
     *
     * @mbg.generated Fri Nov 13 15:46:31 CST 2020
     */
    int updateByExample(@Param("record") XQuartz record, @Param("example") XQuartzExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_quartz
     *
     * @mbg.generated Fri Nov 13 15:46:31 CST 2020
     */
    int updateByPrimaryKeySelective(XQuartz record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_quartz
     *
     * @mbg.generated Fri Nov 13 15:46:31 CST 2020
     */
    int updateByPrimaryKey(XQuartz record);
}