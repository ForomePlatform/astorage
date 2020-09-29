/*
 Copyright (c) 2019. Vladimir Ulitin, Partners Healthcare and members of Forome Association

 Developed by Vladimir Ulitin and Michael Bouzinier

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

	 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package org.forome.astorage.service.network.transport.spring;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptorAdapter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.file.FileSystems;
import java.time.Duration;

@EnableWebMvc
@Configuration
@ComponentScan({ "org.forome.astorage.service" })
public class SpringConfigurationMvc extends WebMvcConfigurerAdapter {

	private static Duration requestTimeout;

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setDefaultTimeout(requestTimeout.toMillis());
		configurer.registerDeferredResultInterceptors(
				new DeferredResultProcessingInterceptorAdapter() {
					@Override
					public <T> boolean handleTimeout(NativeWebRequest req, DeferredResult<T> result) {
						return result.setErrorResult(new AsyncTimeoutException());
					}
				});
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		AntPathMatcher matcher = new AntPathMatcher();
		matcher.setCaseSensitive(false);
		configurer.setPathMatcher(matcher);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//Встроенные в jar ресурсы
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:webapp/static/");
	}


	public static void init(Duration requestTimeout) {
		SpringConfigurationMvc.requestTimeout = requestTimeout;
	}

	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	public static class AsyncTimeoutException extends Exception {
	}
}