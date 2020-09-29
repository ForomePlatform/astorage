/*
 *  Copyright (c) 2020. Vladimir Ulitin, Partners Healthcare and members of Forome Association
 *
 *  Developed by Vladimir Ulitin and Michael Bouzinier
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 * 	 http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.forome.astorage.service.controller;

import graphql.*;
import graphql.execution.ExecutionId;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;
import org.forome.astorage.core.exception.AStorageException;
import org.forome.astorage.service.Service;
import org.forome.astorage.service.controller.struct.GraphQLRequest;
import org.forome.astorage.service.exception.GraphQLWrapperAStorageException;
import org.forome.astorage.service.exception.ServiceExceptionBuilder;
import org.forome.astorage.service.graphql.GraphQLService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;


@Controller
@RequestMapping("/")
public class GraphQLController {

	private final static Logger log = LoggerFactory.getLogger(GraphQLController.class);

	public final static String JSON_PROP_DATA = "data";
	public final static String JSON_PROP_ERROR = "error";

	@RequestMapping(value = "/graphql")
	public ResponseEntity executeGraphQL(HttpServletRequest request) {
		Service service = Service.getInstance();

		GraphQLRequest graphQLRequest = GraphQLRequest.build(request);

		ExecutionInput executionInput = ExecutionInput.newExecutionInput()
				.executionId(ExecutionId.generate())
				.query(graphQLRequest.query)
				.variables(Collections.unmodifiableMap(graphQLRequest.queryVariables))
				.build();

		GraphQLService.GraphQLResponse<JSONObject> graphQLResponse = service.getGraphQLService().execute(executionInput);

		return buildResponseEntity(graphQLResponse);
	}

	private static ResponseEntity buildResponseEntity(GraphQLService.GraphQLResponse<JSONObject> graphQLResponse) {
		HttpStatus httpStatus;
		JSONObject out = new JSONObject();
		if (!graphQLResponse.error) {
			httpStatus = HttpStatus.OK;
			out.put(JSON_PROP_DATA, graphQLResponse.data);
		} else {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			out.put(JSON_PROP_ERROR, graphQLResponse.data);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		headers.setCacheControl("no-cache, no-store, must-revalidate");
		headers.setPragma("no-cache");
		headers.setExpires(0);

		return new ResponseEntity(out.toString().getBytes(StandardCharsets.UTF_8), headers, httpStatus);
	}
}
