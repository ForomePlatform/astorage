package org.forome.astorage.service.controller.struct;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.BadMessageException;
import org.forome.astorage.core.exception.AStorageException;
import org.forome.astorage.service.exception.ServiceExceptionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.*;

/**
 * Created by kris on 26.01.17.
 */
public class GraphQLRequest {

	private final static Logger log = LoggerFactory.getLogger(GraphQLRequest.class);

    private static final String QUERY_PARAM = "query";
    private static final String VARIABLES_PARAM = "variables";

    public final String query;
    public final Map<String, Serializable> queryVariables;

    private GraphQLRequest(
            String query,
            HashMap<String, Serializable> queryVariables
    ) {
        this.query = query;
        this.queryVariables = Collections.unmodifiableMap(queryVariables);
    }

    public static GraphQLRequest build(HttpServletRequest request) {
        HashMap<String, String[]> parameters = new HashMap<>();

        try {
            //Собираем параметры
            String query = request.getParameter(QUERY_PARAM);
            HashMap<String, Serializable> queryVariables = null;

            String variablesJson = request.getParameter(VARIABLES_PARAM);
            if (variablesJson != null) {
                JSONObject variables = parseJSONObject(variablesJson);
                if (variables != null) {
                    queryVariables = new HashMap<>((Map) variables);
                }
            }

            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String parameterName = parameterNames.nextElement();
                parameters.put(parameterName, request.getParameterValues(parameterName));
            }

            if (request instanceof MultipartHttpServletRequest) {//Проверяем возможно это Multipart request
                MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

                Map<String, String[]> multipartParameters = multipartRequest.getParameterMap();
                String[] queryArray = multipartParameters.get(QUERY_PARAM);
                if (queryArray != null && queryArray.length > 0) {
                    query = queryArray[0];
                }

                String[] variablesJsonArray =  multipartParameters.get(VARIABLES_PARAM);
                if (variablesJsonArray != null && variablesJsonArray.length > 0) {
                    JSONObject variables = parseJSONObject(variablesJsonArray[0]);
                    if (variables != null) {
                        queryVariables = new HashMap<>((Map)variables);
                    }
                }
                multipartParameters.forEach((key, values) -> parameters.put(key, values));
            } else {//Ищем POST параметры
                BufferedReader reader = null;
                try {
                    reader = request.getReader();
                } catch (IllegalStateException ignore) {
                }
                if (reader != null) {
                    JSONObject dataPostVariables = parseJSONObject(request.getReader());
                    if (dataPostVariables != null) {
                        if (dataPostVariables.containsKey(QUERY_PARAM)) {
                            query = dataPostVariables.getAsString(QUERY_PARAM);
                        }

                        Object variables = dataPostVariables.get(VARIABLES_PARAM);
                        if (variables != null && variables instanceof Map) {
                            queryVariables = new HashMap<>((Map) variables);
                        }

                        dataPostVariables.forEach((key, value) -> {
                            if (value instanceof String) {
                                parameters.put(key, new String[]{(String) value});
                            }
                        });
                    }
                }
            }

            if (StringUtils.isBlank(query)) {
                throw ServiceExceptionBuilder.buildEmptyValueException(QUERY_PARAM);
            }


            return new GraphQLRequest(
                    query,
                    queryVariables != null ? queryVariables : new HashMap<>()
            );
        } catch (BadMessageException | IOException pe) {
            throw ServiceExceptionBuilder.buildInvalidJsonException(pe);
        } catch (Throwable t) {
            throw t;
        }
    }

    private static JSONObject parseJSONObject(Reader in) throws AStorageException {
        try {
            Object parseData = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(in);
            return castToJSONObject(parseData);
        } catch (ParseException e) {
            throw ServiceExceptionBuilder.buildInvalidJsonException(e);
        }
    }

    private static JSONObject parseJSONObject(String in) throws AStorageException {
        try {
            Object parseData = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(in);
            return castToJSONObject(parseData);
        } catch (ParseException e) {
            throw ServiceExceptionBuilder.buildInvalidJsonException(e);
        }
    }

    private static JSONObject castToJSONObject(Object obj) throws AStorageException {
        if (obj instanceof JSONObject) {
            return (JSONObject) obj;
        } else if (obj instanceof String) {
            if (((String) obj).isEmpty()) {
                return null;
            }
        }

        throw ServiceExceptionBuilder.buildInvalidJsonException();
    }

}
