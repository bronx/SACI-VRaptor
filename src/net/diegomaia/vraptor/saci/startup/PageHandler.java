/*
 * Copyright 2010 Diego Maia da Silva http://dev.diegomaia.net
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package net.diegomaia.vraptor.saci.startup;

import java.lang.annotation.Annotation;

import net.diegomaia.vraptor.saci.exception.PageException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.StereotypeHandler;

@Component
@ApplicationScoped
public class PageHandler implements StereotypeHandler {

	private Router router;
	private PageChecker pageChecker;
	private String loginPageURL = new String();
	private String accessDeniedPageURL = new String();
	private Logger logger = LoggerFactory.getLogger(PageHandler.class);
	
	public PageHandler (Router router, PageChecker pageChecker) {
		this.router = router;
		this.pageChecker = pageChecker;
	}

	public String getLoginPageURL() {
		return this.loginPageURL;
	}

	public void setLoginPageURL(String loginPageURL) {
		this.loginPageURL = loginPageURL;
	}

	public String getAccessDeniedPageURL() {
		return this.accessDeniedPageURL;
	}

	public void setAccessDeniedPageURL(String accessDeniedPageURL) {
		this.accessDeniedPageURL = accessDeniedPageURL;
	}


	@Override
	public void handle(Class<?> type) {
		this.definePagesURLs(this.pageChecker.findPages(type), type);
	}

	private void definePagesURLs(PagesMethods pagesMethods, Class<?> type) {

		if (pagesMethods.getLoginPageMethod() != null) {
			if (!this.loginPageURL.isEmpty()) {
				throw new PageException("Multiple login pages not allowed.");
			} else {
				this.loginPageURL = this.router.urlFor(type, pagesMethods.getLoginPageMethod(), new Object[0]);
				this.logger.info("Login page >> " + this.loginPageURL);
			}
		}
		
		if (pagesMethods.getAccessDeniedPageMethod() != null) {
			if (!this.accessDeniedPageURL.isEmpty()) {
				throw new PageException("Multiple access denied pages not allowed.");
			} else {
				this.accessDeniedPageURL = this.router.urlFor(type, pagesMethods.getAccessDeniedPageMethod(), new Object[0]);
				this.logger.info("Access denied page >> " + this.accessDeniedPageURL);
			}
		}
		
	}

	@Override
	public Class<? extends Annotation> stereotype() {
		return Resource.class;
	}
	
}