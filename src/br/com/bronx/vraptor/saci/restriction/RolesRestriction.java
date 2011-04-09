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

import java.util.ArrayList;
import java.util.List;

import br.com.bronx.vraptor.saci.annotation.RolesPolicy;
import br.com.bronx.vraptor.saci.interfaces.Restriction;

/**
 * @author Diego Maia da Silva a.k.a. Bronx
 */
public class RolesRestriction implements Restriction {

	private List<Role> roles;
	private RolesPolicy policy;

	public List<Role> getRoles() {
		return this.roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public void setPolicy(RolesPolicy policy) {
		this.policy = policy;
	}

	public RolesPolicy getPolicy() {
		return policy;
	}

	@Override
	public RestrictionType getRestrictionType() {
		return RestrictionType.ROLES;
	}
	
	public List<String> getRolesAsStrings(){
		List<String> stringRoles = new ArrayList<String>();
		for (Role role : this.roles) {
			stringRoles.add(role.getRole());
		}
		return stringRoles;
	}

}
