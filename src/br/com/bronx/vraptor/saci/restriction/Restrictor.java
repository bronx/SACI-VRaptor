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

package br.com.bronx.vraptor.saci.restriction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import br.com.bronx.vraptor.saci.annotation.AccessLevel;
import br.com.bronx.vraptor.saci.annotation.InheritRestrictions;
import br.com.bronx.vraptor.saci.annotation.LoggedIn;
import br.com.bronx.vraptor.saci.annotation.OnAccessDenial;
import br.com.bronx.vraptor.saci.annotation.Roles;
import br.com.bronx.vraptor.saci.interfaces.Profile;
import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Diego Maia da Silva a.k.a. Bronx
 */
@Component
public class Restrictor {
	
	private RestrictionValidator restrictionValidator;

	public Restrictor(RestrictionValidator restrictionValidator) {
		this.restrictionValidator = restrictionValidator;
	}

	public RestrictionResult checkRestriction(Method method, Profile profile) {
		RestrictionResult restrictionResult;
		RestrictionsWrapper resourceRestrictions = this.getResourceRestriction(method.getDeclaringClass());
		RestrictionsWrapper methodRestrictions = this.getMethodRestrictions(method);
		restrictionResult = this.restrictionValidator.validateRestrictions(resourceRestrictions, methodRestrictions, profile);
		return restrictionResult;
	}
	
	private RestrictionsWrapper getResourceRestriction(Class<?> clazz) {
		List<Restriction> restrictions = new ArrayList<Restriction>();
		if (clazz.isAnnotationPresent(LoggedIn.class)){
			Restriction loggedInRestriction = new LoggedInRestriction();
			restrictions.add(loggedInRestriction);
		}
		if (clazz.isAnnotationPresent(AccessLevel.class)){
			AccessLevel accessLevel = clazz.getAnnotation(AccessLevel.class);
			AccessLevelRestriction accessLevelRestriction = new AccessLevelRestriction();
			accessLevelRestriction.setMinimumAccessLevel(accessLevel.minimumAccessLevel());
			accessLevelRestriction.setMaximumAccessLevel(accessLevel.maximumAccessLevel());
			restrictions.add(accessLevelRestriction);
		}
		if (clazz.isAnnotationPresent(Roles.class)){
			Roles roles = clazz.getAnnotation(Roles.class);
			RolesRestriction rolesRestriction = new RolesRestriction();
			rolesRestriction.setRoles(this.getRoles(roles));
			rolesRestriction.setPolicy(roles.policy());
			restrictions.add(rolesRestriction);
		}
		return new RestrictionsWrapper(clazz.getAnnotation(InheritRestrictions.class), clazz.getAnnotation(OnAccessDenial.class), restrictions);
	}

	private List<Role> getRoles(Roles rolesList) {
		String[] rolesNames = rolesList.roles();
		List<Role> roles = new ArrayList<Role>();
		for (String roleName : rolesNames){
			if (!roleName.isEmpty()) {
				Role role = new Role();
				role.setRole(roleName);
				roles.add(role);
			}
		}		
		return roles;
	}
	
	private RestrictionsWrapper getMethodRestrictions(Method method) {
		List<Restriction> restrictions = new ArrayList<Restriction>();
		if (method.isAnnotationPresent(LoggedIn.class)){
			Restriction loggedInRestriction = new LoggedInRestriction();
			restrictions.add(loggedInRestriction);
		}
		if (method.isAnnotationPresent(AccessLevel.class)){
			AccessLevel accessLevel = method.getAnnotation(AccessLevel.class);
			AccessLevelRestriction accessLevelRestriction = new AccessLevelRestriction();
			accessLevelRestriction.setMinimumAccessLevel(accessLevel.minimumAccessLevel());
			accessLevelRestriction.setMaximumAccessLevel(accessLevel.maximumAccessLevel());
			restrictions.add(accessLevelRestriction);
		}
		if (method.isAnnotationPresent(Roles.class)){
			Roles roles = method.getAnnotation(Roles.class);
			RolesRestriction rolesRestriction = new RolesRestriction();
			rolesRestriction.setRoles(getRoles(roles));
			rolesRestriction.setPolicy(roles.policy());
			restrictions.add(rolesRestriction);
		}
		return new RestrictionsWrapper(method.getAnnotation(InheritRestrictions.class), method.getAnnotation(OnAccessDenial.class), restrictions);
	}

}
