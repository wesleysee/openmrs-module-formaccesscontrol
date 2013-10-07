/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.formaccesscontrol.api.advice;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.formaccesscontrol.api.FormAccessControlService;
import org.springframework.aop.MethodBeforeAdvice;

/**
 * AOP class used to intercept and log calls to FormService methods
 */
public class EncounterServiceAdvice implements MethodBeforeAdvice {
	
	protected static final Log log = LogFactory.getLog(EncounterServiceAdvice.class);
	
	/**
	 * @see org.springframework.aop.MethodBeforeAdvice#before(Method, Object[], Object)
	 */
	@Override
	public void before(Method method, Object[] args, Object target) throws Throwable {
		if (method.getName().equals("createEncounter") || method.getName().equals("saveEncounter")
		        || method.getName().equals("updateEncounter") || method.getName().equals("voidEncounter")
		        || method.getName().equals("upvoidEncounter") || method.getName().equals("deleteEncounter")
		        || method.getName().equals("purgeEncounter")) {
			Encounter encounter = (Encounter) args[0];
			if (encounter.getForm() != null) {
				Form form = encounter.getForm();
				FormAccessControlService svc = Context.getService(FormAccessControlService.class);
				boolean hasPrivilege = encounter.getId() != null ? svc.hasUpdatePrivilege(form) : svc
				        .hasCreatePrivilege(form);
				if (!hasPrivilege) {
					throw new APIAuthenticationException("Form Privilege required: "
					        + (encounter.getId() != null ? "Edit" : "Create"));
				}
			}
		}
		
	}
}
