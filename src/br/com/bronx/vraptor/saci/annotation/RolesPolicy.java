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

package br.com.bronx.vraptor.saci.annotation;

/**
 * Represents the policy that should be adopted by the validator.<br>
 * CONJUNCTION: the user must play all the roles present within the Roles annotation.<br>
 * DISJUNCTION: the user must assume at least one of the roles present within the Roles annotation.
 * It's the default value for the policy attribute<br>
 *  
 * @author Diego Maia da Silva a.k.a. Bronx
 */
public enum RolesPolicy {

	CONJUNCTION,
	DISJUNCTION;
}
