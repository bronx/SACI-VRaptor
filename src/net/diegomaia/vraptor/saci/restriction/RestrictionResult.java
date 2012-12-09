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


/**
 * Holds the result of a restriction check.<br>
 * If there is any restriction with the given method/resource, the destination
 * field indicates where to redirect the request.
 * 
 * @author Diego Maia da Silva a.k.a. Bronx
 */
public class RestrictionResult {

	private boolean restricted;
	private String destination;
	private RestrictionReason restrictionReason;
	private boolean http403;
	
	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDestination() {
		return destination;
	}

	public RestrictionResult(){
		this.restricted = false;
		this.destination = null;
	}
	
	public boolean isRestricted() {
		return this.restricted;
	}
	
	public void setRestricted(){
		this.restricted = true;
	}

	public void setRestrictionReason(RestrictionReason restrictionReason) {
		this.restrictionReason = restrictionReason;
	}

	public RestrictionReason getRestrictionReason() {
		return restrictionReason;
	}

	public void setHttp403(boolean http403) {
		this.http403 = http403;
	}

	public boolean isHttp403() {
		return http403;
	}

}
