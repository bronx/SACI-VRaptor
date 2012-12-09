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

import net.diegomaia.vraptor.saci.interfaces.Restriction;


/**
 * @author Diego Maia da Silva a.k.a. Bronx
 */
public class AccessLevelRestriction implements Restriction{
	
	private int minimumAccessLevel;
	private int maximumAccessLevel;

	public int getMinimumAccessLevel() {
		return this.minimumAccessLevel;
	}

	public void setMinimumAccessLevel(int minimumAccessLevel) {
		this.minimumAccessLevel = minimumAccessLevel;
	}

	public void setMaximumAccessLevel(int maximumAccessLevel) {
		this.maximumAccessLevel = maximumAccessLevel;
	}

	public int getMaximumAccessLevel() {
		return maximumAccessLevel;
	}
	
	public RestrictionType getRestrictionType() {
		return RestrictionType.ACCESS_LEVEL;
	}

}
