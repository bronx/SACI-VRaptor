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

package net.diegomaia.vraptor.saci.component.checker;

import java.lang.reflect.Method;

import net.diegomaia.vraptor.saci.annotation.AccessLevel;
import net.diegomaia.vraptor.saci.annotation.LoggedIn;
import net.diegomaia.vraptor.saci.annotation.Roles;
import net.diegomaia.vraptor.saci.component.startup.PageHandler;
import net.diegomaia.vraptor.saci.interfaces.Profile;
import net.diegomaia.vraptor.saci.restriction.RestrictionResult;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.view.Results;

/**
 * RestrictionChecker looks for restrictions present in the resource
 * (class) or in the requested method.
 * 
 * @author Diego Maia da Silva a.k.a. Bronx
 */
@Component
public class RestrictionChecker {

	private String defaultLoginPage = new String();
	private String defaultAccessDeniedPage = new String();
	private PageHandler pageHandler;
	private Restrictor restrictor;
	private Result result;
	
	public RestrictionChecker(PageHandler pageHandler, Restrictor restrictor
			 				 ,Result result){
		this.pageHandler = pageHandler;
		this.restrictor = restrictor;
		this.result = result;
	}

	public RestrictionResult checkRestrictions(Method method, Profile profile, Boolean handleRedirection) {
		RestrictionResult restrictionResult = this.restrictor.checkRestriction(method, profile);
		this.confirmDestination(restrictionResult);
		if (restrictionResult.isRestricted() && handleRedirection) {
			this.handleRedirection(restrictionResult.isHttp403(), restrictionResult.getDestination());
			restrictionResult = null;
		}
		return restrictionResult;
	}

	private void handleRedirection(boolean forceHttp403, String destination) {
		if (forceHttp403) {
			this.result.use(Results.http()).sendError(403);
		} else {
			this.result.use(Results.page()).redirectTo(destination);
		}
	}

	private void confirmDestination(RestrictionResult restrictionResult) {
		if (restrictionResult.isRestricted()) {
			switch (restrictionResult.getRestrictionReason()){
			case USER_NOT_LOGGED_IN:
				if (!restrictionResult.isHttp403()) {
					if (restrictionResult.getDestination().isEmpty()) {
						if (!this.defaultLoginPage.isEmpty()) {
							restrictionResult.setDestination(this.defaultLoginPage);
						} else {
							if (!this.pageHandler.getLoginPageURL().isEmpty()) {
								restrictionResult.setDestination(this.pageHandler.getLoginPageURL());
							} else {
								restrictionResult.setHttp403(true);
							}
						}
					}
				}
				break;
			case ACCESS_LEVEL_OUT_OF_RANGE:
			case ROLE_NOT_PLAYED_BY_USER:
				if (!restrictionResult.isHttp403()) {
					if (restrictionResult.getDestination().isEmpty()) {
						if (!this.defaultAccessDeniedPage.isEmpty()) {
							restrictionResult.setDestination(this.defaultAccessDeniedPage);
						} else {
							if (!this.pageHandler.getAccessDeniedPageURL().isEmpty()) {
								restrictionResult.setDestination(this.pageHandler.getAccessDeniedPageURL());
							} else {
								restrictionResult.setHttp403(true);
							}						
						}
					}
				}
				break;
			}
		}
	}

	public boolean hasRestriction(Method method) {
		boolean hasRestriction = this.hasResourceRestriction(method.getDeclaringClass());
		if (!hasRestriction){
			hasRestriction = this.hasMethodRestriction(method);
		}
		return hasRestriction;
	}

	private boolean hasMethodRestriction(Method method) {
		return  (method.isAnnotationPresent(LoggedIn.class) || 
				 method.isAnnotationPresent(AccessLevel.class) ||
				 method.isAnnotationPresent(Roles.class));
	}

	private boolean hasResourceRestriction(Class<?> resourceClass) {
		return (resourceClass.isAnnotationPresent(LoggedIn.class) || 
				resourceClass.isAnnotationPresent(AccessLevel.class) ||
				resourceClass.isAnnotationPresent(Roles.class));
	}

	public void setDefaultLoginPage(String defaultLoginPage) {
		this.defaultLoginPage = defaultLoginPage;
	}

	public void setDefaultAccessDeniedPage(String defaultAccessDeniedPage) {
		this.defaultAccessDeniedPage = defaultAccessDeniedPage;
	}

}
