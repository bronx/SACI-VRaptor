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

import java.util.List;

import br.com.bronx.vraptor.saci.annotation.InheritRestrictions;
import br.com.bronx.vraptor.saci.annotation.OnAccessDenial;

/**
 * @author Diego Maia da Silva a.k.a. Bronx
 */
public class RestrictionsWrapper {
	
	private List<Restriction> restrictions;
	private OnAccessDenial onAccessDenial;
	private InheritRestrictions inheritRestrictions;
	
	public RestrictionsWrapper(InheritRestrictions inheritRestrictions, OnAccessDenial onAccessDenial, 
			List<Restriction> restrictions){
		this.onAccessDenial = onAccessDenial;
		this.restrictions = restrictions;
		this.inheritRestrictions = inheritRestrictions;
	}
	
	public void setRestrictions(List<Restriction> restrictions) {
		this.restrictions = restrictions;
	}
	public List<Restriction> getRestrictions() {
		return restrictions;
	}
	public void setOnAccessDenial(OnAccessDenial onAccessDenial) {
		this.onAccessDenial = onAccessDenial;
	}
	public OnAccessDenial getOnAccessDenial() {
		return onAccessDenial;
	}
	public void setInheritRestrictions(InheritRestrictions inheritRestrictions) {
		this.inheritRestrictions = inheritRestrictions;
	}

	public InheritRestrictions getInheritRestrictions() {
		return inheritRestrictions;
	}
	
	public boolean hasRestrictions() {
		return (this.restrictions != null && !this.restrictions.isEmpty());
	}

}
