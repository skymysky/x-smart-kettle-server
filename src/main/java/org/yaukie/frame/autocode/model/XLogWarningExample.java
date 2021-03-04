package org.yaukie.frame.autocode.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class XLogWarningExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table x_log_warning
     *
     * @mbg.generated Mon Jan 18 15:58:33 CST 2021
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table x_log_warning
     *
     * @mbg.generated Mon Jan 18 15:58:33 CST 2021
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table x_log_warning
     *
     * @mbg.generated Mon Jan 18 15:58:33 CST 2021
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_log_warning
     *
     * @mbg.generated Mon Jan 18 15:58:33 CST 2021
     */
    public XLogWarningExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_log_warning
     *
     * @mbg.generated Mon Jan 18 15:58:33 CST 2021
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_log_warning
     *
     * @mbg.generated Mon Jan 18 15:58:33 CST 2021
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_log_warning
     *
     * @mbg.generated Mon Jan 18 15:58:33 CST 2021
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_log_warning
     *
     * @mbg.generated Mon Jan 18 15:58:33 CST 2021
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_log_warning
     *
     * @mbg.generated Mon Jan 18 15:58:33 CST 2021
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_log_warning
     *
     * @mbg.generated Mon Jan 18 15:58:33 CST 2021
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_log_warning
     *
     * @mbg.generated Mon Jan 18 15:58:33 CST 2021
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_log_warning
     *
     * @mbg.generated Mon Jan 18 15:58:33 CST 2021
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_log_warning
     *
     * @mbg.generated Mon Jan 18 15:58:33 CST 2021
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table x_log_warning
     *
     * @mbg.generated Mon Jan 18 15:58:33 CST 2021
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table x_log_warning
     *
     * @mbg.generated Mon Jan 18 15:58:33 CST 2021
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andLogIdIsNull() {
            addCriterion("log_id is null");
            return (Criteria) this;
        }

        public Criteria andLogIdIsNotNull() {
            addCriterion("log_id is not null");
            return (Criteria) this;
        }

        public Criteria andLogIdEqualTo(String value) {
            addCriterion("log_id =", value, "logId");
            return (Criteria) this;
        }

        public Criteria andLogIdNotEqualTo(String value) {
            addCriterion("log_id <>", value, "logId");
            return (Criteria) this;
        }

        public Criteria andLogIdGreaterThan(String value) {
            addCriterion("log_id >", value, "logId");
            return (Criteria) this;
        }

        public Criteria andLogIdGreaterThanOrEqualTo(String value) {
            addCriterion("log_id >=", value, "logId");
            return (Criteria) this;
        }

        public Criteria andLogIdLessThan(String value) {
            addCriterion("log_id <", value, "logId");
            return (Criteria) this;
        }

        public Criteria andLogIdLessThanOrEqualTo(String value) {
            addCriterion("log_id <=", value, "logId");
            return (Criteria) this;
        }

        public Criteria andLogIdLike(String value) {
            addCriterion("log_id like", value, "logId");
            return (Criteria) this;
        }

        public Criteria andLogIdNotLike(String value) {
            addCriterion("log_id not like", value, "logId");
            return (Criteria) this;
        }

        public Criteria andLogIdIn(List<String> values) {
            addCriterion("log_id in", values, "logId");
            return (Criteria) this;
        }

        public Criteria andLogIdNotIn(List<String> values) {
            addCriterion("log_id not in", values, "logId");
            return (Criteria) this;
        }

        public Criteria andLogIdBetween(String value1, String value2) {
            addCriterion("log_id between", value1, value2, "logId");
            return (Criteria) this;
        }

        public Criteria andLogIdNotBetween(String value1, String value2) {
            addCriterion("log_id not between", value1, value2, "logId");
            return (Criteria) this;
        }

        public Criteria andTargetIdIsNull() {
            addCriterion("target_id is null");
            return (Criteria) this;
        }

        public Criteria andTargetIdIsNotNull() {
            addCriterion("target_id is not null");
            return (Criteria) this;
        }

        public Criteria andTargetIdEqualTo(String value) {
            addCriterion("target_id =", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdNotEqualTo(String value) {
            addCriterion("target_id <>", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdGreaterThan(String value) {
            addCriterion("target_id >", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdGreaterThanOrEqualTo(String value) {
            addCriterion("target_id >=", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdLessThan(String value) {
            addCriterion("target_id <", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdLessThanOrEqualTo(String value) {
            addCriterion("target_id <=", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdLike(String value) {
            addCriterion("target_id like", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdNotLike(String value) {
            addCriterion("target_id not like", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdIn(List<String> values) {
            addCriterion("target_id in", values, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdNotIn(List<String> values) {
            addCriterion("target_id not in", values, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdBetween(String value1, String value2) {
            addCriterion("target_id between", value1, value2, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdNotBetween(String value1, String value2) {
            addCriterion("target_id not between", value1, value2, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetNameIsNull() {
            addCriterion("target_name is null");
            return (Criteria) this;
        }

        public Criteria andTargetNameIsNotNull() {
            addCriterion("target_name is not null");
            return (Criteria) this;
        }

        public Criteria andTargetNameEqualTo(String value) {
            addCriterion("target_name =", value, "targetName");
            return (Criteria) this;
        }

        public Criteria andTargetNameNotEqualTo(String value) {
            addCriterion("target_name <>", value, "targetName");
            return (Criteria) this;
        }

        public Criteria andTargetNameGreaterThan(String value) {
            addCriterion("target_name >", value, "targetName");
            return (Criteria) this;
        }

        public Criteria andTargetNameGreaterThanOrEqualTo(String value) {
            addCriterion("target_name >=", value, "targetName");
            return (Criteria) this;
        }

        public Criteria andTargetNameLessThan(String value) {
            addCriterion("target_name <", value, "targetName");
            return (Criteria) this;
        }

        public Criteria andTargetNameLessThanOrEqualTo(String value) {
            addCriterion("target_name <=", value, "targetName");
            return (Criteria) this;
        }

        public Criteria andTargetNameLike(String value) {
            addCriterion("target_name like", value, "targetName");
            return (Criteria) this;
        }

        public Criteria andTargetNameNotLike(String value) {
            addCriterion("target_name not like", value, "targetName");
            return (Criteria) this;
        }

        public Criteria andTargetNameIn(List<String> values) {
            addCriterion("target_name in", values, "targetName");
            return (Criteria) this;
        }

        public Criteria andTargetNameNotIn(List<String> values) {
            addCriterion("target_name not in", values, "targetName");
            return (Criteria) this;
        }

        public Criteria andTargetNameBetween(String value1, String value2) {
            addCriterion("target_name between", value1, value2, "targetName");
            return (Criteria) this;
        }

        public Criteria andTargetNameNotBetween(String value1, String value2) {
            addCriterion("target_name not between", value1, value2, "targetName");
            return (Criteria) this;
        }

        public Criteria andLogTypeIsNull() {
            addCriterion("log_type is null");
            return (Criteria) this;
        }

        public Criteria andLogTypeIsNotNull() {
            addCriterion("log_type is not null");
            return (Criteria) this;
        }

        public Criteria andLogTypeEqualTo(String value) {
            addCriterion("log_type =", value, "logType");
            return (Criteria) this;
        }

        public Criteria andLogTypeNotEqualTo(String value) {
            addCriterion("log_type <>", value, "logType");
            return (Criteria) this;
        }

        public Criteria andLogTypeGreaterThan(String value) {
            addCriterion("log_type >", value, "logType");
            return (Criteria) this;
        }

        public Criteria andLogTypeGreaterThanOrEqualTo(String value) {
            addCriterion("log_type >=", value, "logType");
            return (Criteria) this;
        }

        public Criteria andLogTypeLessThan(String value) {
            addCriterion("log_type <", value, "logType");
            return (Criteria) this;
        }

        public Criteria andLogTypeLessThanOrEqualTo(String value) {
            addCriterion("log_type <=", value, "logType");
            return (Criteria) this;
        }

        public Criteria andLogTypeLike(String value) {
            addCriterion("log_type like", value, "logType");
            return (Criteria) this;
        }

        public Criteria andLogTypeNotLike(String value) {
            addCriterion("log_type not like", value, "logType");
            return (Criteria) this;
        }

        public Criteria andLogTypeIn(List<String> values) {
            addCriterion("log_type in", values, "logType");
            return (Criteria) this;
        }

        public Criteria andLogTypeNotIn(List<String> values) {
            addCriterion("log_type not in", values, "logType");
            return (Criteria) this;
        }

        public Criteria andLogTypeBetween(String value1, String value2) {
            addCriterion("log_type between", value1, value2, "logType");
            return (Criteria) this;
        }

        public Criteria andLogTypeNotBetween(String value1, String value2) {
            addCriterion("log_type not between", value1, value2, "logType");
            return (Criteria) this;
        }

        public Criteria andLogFileIsNull() {
            addCriterion("log_file is null");
            return (Criteria) this;
        }

        public Criteria andLogFileIsNotNull() {
            addCriterion("log_file is not null");
            return (Criteria) this;
        }

        public Criteria andLogFileEqualTo(String value) {
            addCriterion("log_file =", value, "logFile");
            return (Criteria) this;
        }

        public Criteria andLogFileNotEqualTo(String value) {
            addCriterion("log_file <>", value, "logFile");
            return (Criteria) this;
        }

        public Criteria andLogFileGreaterThan(String value) {
            addCriterion("log_file >", value, "logFile");
            return (Criteria) this;
        }

        public Criteria andLogFileGreaterThanOrEqualTo(String value) {
            addCriterion("log_file >=", value, "logFile");
            return (Criteria) this;
        }

        public Criteria andLogFileLessThan(String value) {
            addCriterion("log_file <", value, "logFile");
            return (Criteria) this;
        }

        public Criteria andLogFileLessThanOrEqualTo(String value) {
            addCriterion("log_file <=", value, "logFile");
            return (Criteria) this;
        }

        public Criteria andLogFileLike(String value) {
            addCriterion("log_file like", value, "logFile");
            return (Criteria) this;
        }

        public Criteria andLogFileNotLike(String value) {
            addCriterion("log_file not like", value, "logFile");
            return (Criteria) this;
        }

        public Criteria andLogFileIn(List<String> values) {
            addCriterion("log_file in", values, "logFile");
            return (Criteria) this;
        }

        public Criteria andLogFileNotIn(List<String> values) {
            addCriterion("log_file not in", values, "logFile");
            return (Criteria) this;
        }

        public Criteria andLogFileBetween(String value1, String value2) {
            addCriterion("log_file between", value1, value2, "logFile");
            return (Criteria) this;
        }

        public Criteria andLogFileNotBetween(String value1, String value2) {
            addCriterion("log_file not between", value1, value2, "logFile");
            return (Criteria) this;
        }

        public Criteria andLogLevelIsNull() {
            addCriterion("log_level is null");
            return (Criteria) this;
        }

        public Criteria andLogLevelIsNotNull() {
            addCriterion("log_level is not null");
            return (Criteria) this;
        }

        public Criteria andLogLevelEqualTo(String value) {
            addCriterion("log_level =", value, "logLevel");
            return (Criteria) this;
        }

        public Criteria andLogLevelNotEqualTo(String value) {
            addCriterion("log_level <>", value, "logLevel");
            return (Criteria) this;
        }

        public Criteria andLogLevelGreaterThan(String value) {
            addCriterion("log_level >", value, "logLevel");
            return (Criteria) this;
        }

        public Criteria andLogLevelGreaterThanOrEqualTo(String value) {
            addCriterion("log_level >=", value, "logLevel");
            return (Criteria) this;
        }

        public Criteria andLogLevelLessThan(String value) {
            addCriterion("log_level <", value, "logLevel");
            return (Criteria) this;
        }

        public Criteria andLogLevelLessThanOrEqualTo(String value) {
            addCriterion("log_level <=", value, "logLevel");
            return (Criteria) this;
        }

        public Criteria andLogLevelLike(String value) {
            addCriterion("log_level like", value, "logLevel");
            return (Criteria) this;
        }

        public Criteria andLogLevelNotLike(String value) {
            addCriterion("log_level not like", value, "logLevel");
            return (Criteria) this;
        }

        public Criteria andLogLevelIn(List<String> values) {
            addCriterion("log_level in", values, "logLevel");
            return (Criteria) this;
        }

        public Criteria andLogLevelNotIn(List<String> values) {
            addCriterion("log_level not in", values, "logLevel");
            return (Criteria) this;
        }

        public Criteria andLogLevelBetween(String value1, String value2) {
            addCriterion("log_level between", value1, value2, "logLevel");
            return (Criteria) this;
        }

        public Criteria andLogLevelNotBetween(String value1, String value2) {
            addCriterion("log_level not between", value1, value2, "logLevel");
            return (Criteria) this;
        }

        public Criteria andLogErrorIsNull() {
            addCriterion("log_error is null");
            return (Criteria) this;
        }

        public Criteria andLogErrorIsNotNull() {
            addCriterion("log_error is not null");
            return (Criteria) this;
        }

        public Criteria andLogErrorEqualTo(String value) {
            addCriterion("log_error =", value, "logError");
            return (Criteria) this;
        }

        public Criteria andLogErrorNotEqualTo(String value) {
            addCriterion("log_error <>", value, "logError");
            return (Criteria) this;
        }

        public Criteria andLogErrorGreaterThan(String value) {
            addCriterion("log_error >", value, "logError");
            return (Criteria) this;
        }

        public Criteria andLogErrorGreaterThanOrEqualTo(String value) {
            addCriterion("log_error >=", value, "logError");
            return (Criteria) this;
        }

        public Criteria andLogErrorLessThan(String value) {
            addCriterion("log_error <", value, "logError");
            return (Criteria) this;
        }

        public Criteria andLogErrorLessThanOrEqualTo(String value) {
            addCriterion("log_error <=", value, "logError");
            return (Criteria) this;
        }

        public Criteria andLogErrorLike(String value) {
            addCriterion("log_error like", value, "logError");
            return (Criteria) this;
        }

        public Criteria andLogErrorNotLike(String value) {
            addCriterion("log_error not like", value, "logError");
            return (Criteria) this;
        }

        public Criteria andLogErrorIn(List<String> values) {
            addCriterion("log_error in", values, "logError");
            return (Criteria) this;
        }

        public Criteria andLogErrorNotIn(List<String> values) {
            addCriterion("log_error not in", values, "logError");
            return (Criteria) this;
        }

        public Criteria andLogErrorBetween(String value1, String value2) {
            addCriterion("log_error between", value1, value2, "logError");
            return (Criteria) this;
        }

        public Criteria andLogErrorNotBetween(String value1, String value2) {
            addCriterion("log_error not between", value1, value2, "logError");
            return (Criteria) this;
        }

        public Criteria andLogSubjectIsNull() {
            addCriterion("log_subject is null");
            return (Criteria) this;
        }

        public Criteria andLogSubjectIsNotNull() {
            addCriterion("log_subject is not null");
            return (Criteria) this;
        }

        public Criteria andLogSubjectEqualTo(String value) {
            addCriterion("log_subject =", value, "logSubject");
            return (Criteria) this;
        }

        public Criteria andLogSubjectNotEqualTo(String value) {
            addCriterion("log_subject <>", value, "logSubject");
            return (Criteria) this;
        }

        public Criteria andLogSubjectGreaterThan(String value) {
            addCriterion("log_subject >", value, "logSubject");
            return (Criteria) this;
        }

        public Criteria andLogSubjectGreaterThanOrEqualTo(String value) {
            addCriterion("log_subject >=", value, "logSubject");
            return (Criteria) this;
        }

        public Criteria andLogSubjectLessThan(String value) {
            addCriterion("log_subject <", value, "logSubject");
            return (Criteria) this;
        }

        public Criteria andLogSubjectLessThanOrEqualTo(String value) {
            addCriterion("log_subject <=", value, "logSubject");
            return (Criteria) this;
        }

        public Criteria andLogSubjectLike(String value) {
            addCriterion("log_subject like", value, "logSubject");
            return (Criteria) this;
        }

        public Criteria andLogSubjectNotLike(String value) {
            addCriterion("log_subject not like", value, "logSubject");
            return (Criteria) this;
        }

        public Criteria andLogSubjectIn(List<String> values) {
            addCriterion("log_subject in", values, "logSubject");
            return (Criteria) this;
        }

        public Criteria andLogSubjectNotIn(List<String> values) {
            addCriterion("log_subject not in", values, "logSubject");
            return (Criteria) this;
        }

        public Criteria andLogSubjectBetween(String value1, String value2) {
            addCriterion("log_subject between", value1, value2, "logSubject");
            return (Criteria) this;
        }

        public Criteria andLogSubjectNotBetween(String value1, String value2) {
            addCriterion("log_subject not between", value1, value2, "logSubject");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeIsNull() {
            addCriterion("created_time is null");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeIsNotNull() {
            addCriterion("created_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeEqualTo(Date value) {
            addCriterion("created_time =", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeNotEqualTo(Date value) {
            addCriterion("created_time <>", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeGreaterThan(Date value) {
            addCriterion("created_time >", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("created_time >=", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeLessThan(Date value) {
            addCriterion("created_time <", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeLessThanOrEqualTo(Date value) {
            addCriterion("created_time <=", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeIn(List<Date> values) {
            addCriterion("created_time in", values, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeNotIn(List<Date> values) {
            addCriterion("created_time not in", values, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeBetween(Date value1, Date value2) {
            addCriterion("created_time between", value1, value2, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeNotBetween(Date value1, Date value2) {
            addCriterion("created_time not between", value1, value2, "createdTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table x_log_warning
     *
     * @mbg.generated do_not_delete_during_merge Mon Jan 18 15:58:33 CST 2021
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table x_log_warning
     *
     * @mbg.generated Mon Jan 18 15:58:33 CST 2021
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}