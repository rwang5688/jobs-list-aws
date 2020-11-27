package com.rwang5688;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import com.rwang5688.dal.Task;
import com.rwang5688.dal.TaskTable;


public class CreateTaskHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger logger = LoggerFactory.getLogger(CreateTaskHandler.class);
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		// log execution details
		logger.info("ENVIRONMENT VARIABLES: {}", gson.toJson(System.getenv()));
		logger.info("CONTEXT: {}", gson.toJson(context));
		logger.info("INPUT: {}", gson.toJson(input));

		try {
			// get the 'body' from input
			ObjectMapper mapper = new ObjectMapper();
			JsonNode body = mapper.readTree((String) input.get("body"));
			//JsonNode taskExtraOptions = body.get("task_extra_options");
			//Map<String, String> taskExtraOptionsMap = mapper.convertValue(taskExtraOptions,
			//											new TypeReference<Map<String, String>>(){});

			// create the Task object based on POST parameters
			Task task = new Task();
			task.setUserId(body.get("user_id").asText());
			//task.setTaskId(body.get("task_id").asText());
			task.setTaskTool(body.get("task_tool").asText());
			//task.setTaskExtraOptions(taskExtraOptionsMap);
			task.setTaskFileinfoJson(body.get("task_fileinfo_json").asText());
			task.setTaskPreprocessTar(body.get("task_preprocess_tar").asText());
			task.setTaskSourceCodeZip(body.get("task_source_code_zip").asText());
			task.setTaskDotScanLogTar(body.get("task_dot_scan_log_tar").asText());
			task.setTaskDotScanLogTarUrl(body.get("task_dot_scan_log_tar_url").asText());
			task.setTaskScanResultTar(body.get("task_scan_result_tar").asText());
			task.setTaskScanResultTarUrl(body.get("task_scan_result_tar_url").asText());
			task.setTaskSummaryPdf(body.get("task_summary_pdf").asText());
			task.setTaskSummaryPdfUrl(body.get("task_summary_pdf_url").asText());
			task.setTaskIssuesCsv(body.get("task_issues_csv").asText());
			task.setTaskIssuesCsvUrl(body.get("task_issues_csv_url").asText());
			task.setTaskStatus(body.get("task_status").asText());
			task.setSubmitTimestamp(body.get("submit_timestamp").asText());
			task.setUpdateTimestamp(body.get("update_timestamp").asText());

			// save to Task table
			TaskTable taskTable = new TaskTable();
			taskTable.save(task);

			// send the response back
			return ApiGatewayResponse.builder()
					.setStatusCode(200)
					.setObjectBody(task)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
					.build();
		} catch (Exception ex) {
			logger.error("Error in saving task: " + ex);

			// send the error response back
			Response responseBody = new Response("Error in saving task: ", input);
			return ApiGatewayResponse.builder()
					.setStatusCode(500)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
					.build();
		}
	}

}

