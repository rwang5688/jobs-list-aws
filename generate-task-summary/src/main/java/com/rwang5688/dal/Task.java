package com.rwang5688.dal;

import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;


@DynamoDBTable(tableName = "PLACEHOLDER_TASK_TABLE")
public class Task {

    private static final String TASK_TABLE = System.getenv("TASK_TABLE");
	private static final Logger logger = LoggerFactory.getLogger(Task.class);

    private DynamoDBConnection db_connection;
    private AmazonDynamoDB db;
    private DynamoDBMapper mapper;

    private String user_id;
    private String task_id;
    private String task_tool;
    private Map<String, String> task_extra_options;
    private String task_fileinfo_json;
    private String task_preprocess_tar;
    private String task_source_code_zip;
    private String task_status;
    private String submit_timestamp;
    private String update_timestamp;

    @DynamoDBHashKey(attributeName = "user_id")
    public String getUserId() {
        return this.user_id;
    }
    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    @DynamoDBRangeKey(attributeName = "task_id")
    @DynamoDBAutoGeneratedKey
    public String getTaskId() {
        return this.task_id;
    }
    public void setTaskId(String task_id) {
        this.task_id = task_id;
    }

    @DynamoDBAttribute(attributeName = "task_tool")
    public String getTaskTool() {
        return this.task_tool;
    }
    public void setTaskTool(String task_tool) {
        this.task_tool = task_tool;
    }

    @DynamoDBAttribute(attributeName = "task_extra_options")
    public Map<String, String> getTaskExtraOptions() {
        return this.task_extra_options;
    }
    public void setTaskExtraOptions(Map<String, String> task_extra_options) {
        this.task_extra_options = task_extra_options;
    }

    @DynamoDBAttribute(attributeName = "task_fileinfo_json")
    public String getTaskFileinfoJson() {
        return this.task_fileinfo_json;
    }
    public void setTaskFileinfoJson(String task_fileinfo_json) {
        this.task_fileinfo_json = task_fileinfo_json;
    }

    @DynamoDBAttribute(attributeName = "task_preprocess_tar")
    public String getTaskPreprocessTar() {
        return this.task_preprocess_tar;
    }
    public void setTaskPreprocessTar(String task_preprocess_tar) {
        this.task_preprocess_tar = task_preprocess_tar;
    }

    @DynamoDBAttribute(attributeName = "task_source_code_zip")
    public String getTaskSourceCodeZip() {
        return this.task_source_code_zip;
    }
    public void setTaskSourceCodeZip(String task_source_code_zip) {
        this.task_source_code_zip = task_source_code_zip;
    }

    @DynamoDBAttribute(attributeName = "task_status")
    public String getTaskStatus() {
        return this.task_status;
    }
    public void setTaskStatus(String task_status) {
        this.task_status = task_status;
    }

    @DynamoDBAttribute(attributeName = "submit_timestamp")
    public String getSubmitTimestamp() {
        return this.submit_timestamp;
    }
    public void setSubmitTimestamp(String submit_timestamp) {
        this.submit_timestamp = submit_timestamp;
    }

    @DynamoDBAttribute(attributeName = "update_timestamp")
    public String getUpdateTimestamp() {
        return this.update_timestamp;
    }
    public void setUpdateTimestamp(String update_timestamp) {
        this.update_timestamp = update_timestamp;
    }


    public Task() {
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
            .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(TASK_TABLE))
            .build();
        this.db_connection = DynamoDBConnection.getInstance();
        this.db = this.db_connection.getDb();
        this.mapper = this.db_connection.createDbMapper(mapperConfig);
    }

    private String toStringTemplate = null;

    public String toString() {
        if (this.toStringTemplate == null) {
            this.toStringTemplate = "Task [user_id=%s, task_id=%s, ";
            this.toStringTemplate += "task_tool=%s, task_extra_options=%s, ";
            this.toStringTemplate += "task_fileinfo_json=%s, task_preprocess_tar=%s, ";
            this.toStringTemplate += "task_source_code_zip=%s, task_status=%s, ";
            this.toStringTemplate += "submit_timestmp=%s, update_timestamp=%s]";
        }

        return String.format(toStringTemplate,
                            this.user_id, this.task_id,
                            this.task_tool, this.task_extra_options.toString(),
                            this.task_fileinfo_json, this.task_preprocess_tar,
                            this.task_source_code_zip, this.task_status,
                            this.submit_timestamp, this.update_timestamp);
    }

    public Task get(String user_id, String task_id) throws IOException {
        Task task = null;

        HashMap<String, AttributeValue> av = new HashMap<String, AttributeValue>();
        av.put(":v1", new AttributeValue().withS(user_id));
        av.put(":v2", new AttributeValue().withS(task_id));

        DynamoDBQueryExpression<Task> queryExp = new DynamoDBQueryExpression<Task>()
            .withKeyConditionExpression("user_id = :v1 and task_id = :v2")
            .withExpressionAttributeValues(av);

        PaginatedQueryList<Task> result = this.mapper.query(Task.class, queryExp);
        if (result.size() > 0) {
            task = result.get(0);
            logger.info("Tasks - get(): task - " + task.toString());
        } else {
            logger.info("Tasks - get(): task - Not Found.");
        }

        return task;
    }

    public void save(Task task) throws IOException {
        logger.info("Tasks - save(): " + task.toString());
        this.mapper.save(task);
    }

    public Boolean delete(String user_id, String task_id) throws IOException {
        Task task = null;

        // get task if exists
        task = get(user_id, task_id);
        if (task != null) {
            logger.info("Tasks - delete(): " + task.toString());
            this.mapper.delete(task);
        } else {
            logger.info("Tasks - delete(): task - does not exist.");
            return false;
        }

        return true;
    }

    public List<Task> list() throws IOException {
        DynamoDBScanExpression scanExp = new DynamoDBScanExpression();
        List<Task> results = this.mapper.scan(Task.class, scanExp);
        for (Task t : results) {
            logger.info("Tasks - list(): " + t.toString());
        }
        return results;
    }

}

