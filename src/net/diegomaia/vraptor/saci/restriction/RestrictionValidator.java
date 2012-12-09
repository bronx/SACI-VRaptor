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

package net.diegomaia.vraptor.saci.restriction;

import java.util.List;

import net.diegomaia.vraptor.saci.annotation.OnAccessDenial;
import net.diegomaia.vraptor.saci.exception.RestrictionAnnotationException;
import net.diegomaia.vraptor.saci.interfaces.Profile;
import net.diegomaia.vraptor.saci.interfaces.Restriction;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Diego Maia da Silva a.k.a. Bronx
 */
@Component
public class RestrictionValidator {

	public RestrictionResult validateRestrictions(
			RestrictionsWrapper resourceRestrictions,
			RestrictionsWrapper methodRestrictions, Profile profile) {
		RestrictionResult restrictionResult;
		
		restrictionResult = this.checkLogin(profile, resourceRestrictions.getOnAccessDenial(), methodRestrictions.getOnAccessDenial());
		if (!restrictionResult.isRestricted()){
			//User logged in
			restrictionResult = this.checkMethodRestrictions(profile, resourceRestrictions, methodRestrictions);
			if (!restrictionResult.isRestricted()){
				//Method (action) has no restrictions regarding the current user
				if (this.shouldInheritResourceRestrictions(resourceRestrictions, methodRestrictions))
					//Cascade restrictions - verify both method and resource restrictions
					restrictionResult = this.checkResourceRestrictions(profile, resourceRestrictions, methodRestrictions);
			}
		}
		return restrictionResult;
	}
	
	private boolean shouldInheritResourceRestrictions(RestrictionsWrapper resourceRestrictions,
			RestrictionsWrapper methodRestrictions) {
		boolean cascade = true;
		switch (this.checkRestrictionsInheritancePolicy(methodRestrictions)) {
		case INHERIT:
			cascade = true;
			break;
		case DO_NOT_INHERIT:
			cascade = false;
			break;
		case CHECK_RESOURCE:
			cascade = (resourceRestrictions.getInheritRestrictions() == null ||
				       resourceRestrictions.getInheritRestrictions().cascade());
			break;
		}
		return cascade;
	}

	private RestrictionsInheritancePolicy checkRestrictionsInheritancePolicy(RestrictionsWrapper methodRestrictions) {
		RestrictionsInheritancePolicy inheritancePolicy = RestrictionsInheritancePolicy.INHERIT;
		
//		if (methodRestrictions.hasRestrictions()) {
			if (methodRestrictions.getInheritRestrictions() == null) {
				inheritancePolicy = RestrictionsInheritancePolicy.CHECK_RESOURCE;
			} else {
				if (methodRestrictions.getInheritRestrictions().cascade()) {
					inheritancePolicy = RestrictionsInheritancePolicy.INHERIT;
				} else {
					inheritancePolicy = RestrictionsInheritancePolicy.DO_NOT_INHERIT;
				}
			}
//		} else {
//			methodCascade = RestrictionsInheritancePolicy.CHECK_RESOURCE;
//		}
		
		return inheritancePolicy;
	}

	private RestrictionResult checkMethodRestrictions(Profile profile,
			RestrictionsWrapper resourceRestrictions,
			RestrictionsWrapper methodRestrictions) {
		List<Restriction> resourceRestrictionsList = methodRestrictions.getRestrictions();
		RestrictionResult restrictionResult = new RestrictionResult();
		for (Restriction restriction : resourceRestrictionsList){
			if (restriction instanceof AccessLevelRestriction){
				restrictionResult = this.checkAccessLevelRestriction(profile, (AccessLevelRestriction)restriction, methodRestrictions.getOnAccessDenial(), resourceRestrictions.getOnAccessDenial());
				if (restrictionResult.isRestricted()) break;
			}
			if (restriction instanceof RolesRestriction){
				restrictionResult = this.checkRolesRestriction(profile, (RolesRestriction)restriction, methodRestrictions.getOnAccessDenial(), resourceRestrictions.getOnAccessDenial());
			}
		}
		return restrictionResult;
	}
	
	private RestrictionResult checkResourceRestrictions(Profile profile,
			RestrictionsWrapper resourceRestrictions,
			RestrictionsWrapper methodRestrictions) {
		List<Restriction> resourceRestrictionsList = resourceRestrictions.getRestrictions();
		RestrictionResult restrictionResult = new RestrictionResult();
		for (Restriction restriction : resourceRestrictionsList){
			if (restriction instanceof AccessLevelRestriction){
				restrictionResult = this.checkAccessLevelRestriction(profile, (AccessLevelRestriction)restriction, methodRestrictions.getOnAccessDenial(), resourceRestrictions.getOnAccessDenial());
				if (restrictionResult.isRestricted()) break;
			}
			if (restriction instanceof RolesRestriction){
				restrictionResult = this.checkRolesRestriction(profile, (RolesRestriction)restriction, methodRestrictions.getOnAccessDenial(), resourceRestrictions.getOnAccessDenial());
			}
		}
		return restrictionResult;
	}

	private RestrictionResult checkRolesRestriction(Profile profile,
			RolesRestriction restriction, OnAccessDenial onMethodAccessDenial,
			OnAccessDenial onResourceAccessDenial) {
		RestrictionResult restrictionResult = new RestrictionResult();
		List<Role> roles = restriction.getRoles();
		if (roles.size() == 0){
			throw new RestrictionAnnotationException("You must specify the roles in the 'roles' attribute within the @Roles annotation.");
		}
		switch (restriction.getPolicy()){
		case CONJUNCTION:
			if (!profile.getRoles().containsAll(restriction.getRolesAsStrings())){
				restrictionResult.setRestricted();
				restrictionResult.setRestrictionReason(RestrictionReason.ROLE_NOT_PLAYED_BY_USER);
				restrictionResult.setDestination(this.getDestination(onMethodAccessDenial, onResourceAccessDenial));
				restrictionResult.setHttp403(this.isHttp403(onMethodAccessDenial, onResourceAccessDenial));
			}
			break;
		case DISJUNCTION:
			boolean hasRole = false;
			for (Role role : roles){
				if (profile.getRoles().contains(role.getRole())){
					hasRole = true;
					break;
				}
			}
			if (!hasRole){
				restrictionResult.setRestricted();
				restrictionResult.setRestrictionReason(RestrictionReason.ROLE_NOT_PLAYED_BY_USER);
				restrictionResult.setDestination(this.getDestination(onMethodAccessDenial, onResourceAccessDenial));
				restrictionResult.setHttp403(this.isHttp403(onMethodAccessDenial, onResourceAccessDenial));
			}
			break;
		}
		return restrictionResult;
	}

	private boolean isHttp403(OnAccessDenial onMethodAccessDenial,
			OnAccessDenial onResourceAccessDenial) {
		return ((onMethodAccessDenial != null && onMethodAccessDenial.forceHttp403()) ||
			    (onResourceAccessDenial != null && !onResourceAccessDenial.forceHttp403()));
	}

	private String getDestination(OnAccessDenial onMethodAccessDenial,
			OnAccessDenial onResourceAccessDenial) {
		String destination;
		if (onMethodAccessDenial != null && !onMethodAccessDenial.accessDeniedPage().isEmpty()){
			destination = onMethodAccessDenial.accessDeniedPage();
		} else {
			if (onResourceAccessDenial != null && !onResourceAccessDenial.accessDeniedPage().isEmpty()){
				destination = onResourceAccessDenial.accessDeniedPage();
			}
			destination = "";
		}
		return destination;
	}

	private RestrictionResult checkAccessLevelRestriction(
			Profile profile, AccessLevelRestriction restriction,
			OnAccessDenial onMethodAccessDenial, OnAccessDenial onResourceAccessDenial) {
		RestrictionResult restrictionResult = new RestrictionResult();
		if (restriction.getMinimumAccessLevel() > restriction.getMaximumAccessLevel())
			throw new RestrictionAnnotationException("'minimumAccessLevel' cannot be greater than 'maximumAccessLevel'.");
		if (profile.getAccessLevel() < restriction.getMinimumAccessLevel() || profile.getAccessLevel() > restriction.getMaximumAccessLevel()){
			restrictionResult.setRestricted();
			restrictionResult.setRestrictionReason(RestrictionReason.ACCESS_LEVEL_OUT_OF_RANGE);
			restrictionResult.setDestination(this.getDestination(onMethodAccessDenial, onResourceAccessDenial));
			restrictionResult.setHttp403(this.isHttp403(onMethodAccessDenial, onResourceAccessDenial));
		}
		return restrictionResult;
	}

	private RestrictionResult checkLogin(Profile profile,
			OnAccessDenial onResourceAccessDenial,
			OnAccessDenial onMethodAccessDenied) {
		RestrictionResult restrictionResult = new RestrictionResult();
		if (!profile.isLoggedIn()){
			String destination = "";
			restrictionResult.setRestricted();
			restrictionResult.setRestrictionReason(RestrictionReason.USER_NOT_LOGGED_IN);
			if (onMethodAccessDenied != null) {
				if (onMethodAccessDenied.forceHttp403()){
					restrictionResult.setHttp403(true);
				} else {
					if (!onMethodAccessDenied.loginPage().isEmpty()){
						destination = onMethodAccessDenied.loginPage();
					}
				}
			} else {
				if (onResourceAccessDenial != null) {
					if (onResourceAccessDenial.forceHttp403()) {
						restrictionResult.setHttp403(true);
					} else {
						if (!onResourceAccessDenial.loginPage().isEmpty()){
							destination = onResourceAccessDenial.loginPage();
						}
					}
				}
			}
			restrictionResult.setDestination(destination);
		}
		return restrictionResult;
	}

}
