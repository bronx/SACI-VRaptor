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

package br.com.bronx.vraptor.saci.interceptor;

import br.com.bronx.vraptor.saci.component.checker.RestrictionChecker;
import br.com.bronx.vraptor.saci.interfaces.Profile;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * This interceptor controls the access to the resources, based on their
 * restrictions annotations.<br>
 * The restrictions might be method restrictions or resource restrictions,
 * according to where the annotations had been placed.
 * 
 * @author Diego Maia da Silva a.k.a. Bronx
 */
@Intercepts
public class AccessControllerInterceptor implements Interceptor {

	private Result result;
	private RestrictionChecker restrictionChecker;
	private Profile profile;
	
	public AccessControllerInterceptor(Result result, RestrictionChecker restrictionChecker
			,Profile profile) {
		this.result = result;
		this.restrictionChecker = restrictionChecker;
		this.profile = profile;
	}
	
	@Override
	public boolean accepts(ResourceMethod method) {
		return this.restrictionChecker.hasRestriction(method.getMethod());
	}

	
	public void intercept(InterceptorStack stack, ResourceMethod resourceMethod,
			Object resourceInstance) throws InterceptionException {
//		RestrictionResult restrictionResult;
//		this.restrictionChecker.setDefaultAccessDeniedPage("/access/denied/page");
//		this.restrictionChecker.setDefaultLoginPage("/login/page");
		this.restrictionChecker.checkRestrictions(resourceMethod.getMethod(), this.profile, true);
		if (!this.result.used())
			stack.next(resourceMethod, resourceInstance);
	}
}
