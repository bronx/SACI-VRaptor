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

package net.diegomaia.vraptor.saci.component.startup;

import java.lang.reflect.Method;

import net.diegomaia.vraptor.saci.annotation.AccessDeniedPage;
import net.diegomaia.vraptor.saci.annotation.LoginPage;
import net.diegomaia.vraptor.saci.exception.PageException;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

@Component
@PrototypeScoped
public class PageChecker {
	
	public PagesMethods findPages(Class<?> resource) {
		
		Method loginPageMethod = null;
		Method accessDeniedPageMethod = null;
		
		for (Method method : resource.getMethods()) {
			if (method.isAnnotationPresent(LoginPage.class)) {
				if (loginPageMethod == null) {
					if (this.hasParameters(method)) {
						throw new PageException("Login page method cannot have parameters.");
					} else {
						loginPageMethod = method;
					}
				} else {
					throw new PageException("Multiple login pages not allowed.");
				}
			}
			if (method.isAnnotationPresent(AccessDeniedPage.class)) {
				if (accessDeniedPageMethod == null) {
					if (this.hasParameters(method)) {
						throw new PageException("Access denied page method cannot have parameters.");
					} else {
						accessDeniedPageMethod = method;
					}
				} else {
					throw new PageException("Multiple access denied pages not allowed.");
				}
			}
			
		}
		
		PagesMethods pagesInfo = new PagesMethods();
		pagesInfo.setLoginPageMethod(loginPageMethod);
		pagesInfo.setAccessDeniedPageMethod(accessDeniedPageMethod);
		
		return pagesInfo;
	}

	private boolean hasParameters(Method method) {
		return (method.getParameterTypes().length != 0);
	}
	
}
