package org.forome.astorage.service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by kris on 26.12.16.
 */
@Controller
@RequestMapping("/")
public class GraphiQLController {

	private final static Logger log = LoggerFactory.getLogger(GraphiQLController.class);

	@RequestMapping(value = "/graphiql")
	public Object viewGraphiQL(HttpServletRequest request) {
		return new ModelAndView("/static/graphiql/graphiql.html");
	}
}
