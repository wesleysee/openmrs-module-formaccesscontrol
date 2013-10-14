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

import java.util.Collection;
import java.util.Iterator;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.formaccesscontrol.Constants;
import org.openmrs.module.formaccesscontrol.api.FormAccessControlService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.aop.Advisor;

/**
 * AOP class used to intercept and log calls to FormService methods
 */
public class EncounterServiceAdvisor implements Advisor {
	
	protected static final Log log = LogFactory.getLog(EncounterServiceAdvisor.class);
	
	@Override
	public Advice getAdvice() {
		return new EncounterServiceAroundAdvice();
	}
	
	@Override
	public boolean isPerInstance() {
		return false;
	}
	
	private class EncounterServiceAroundAdvice implements MethodInterceptor {
		
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			String methodName = invocation.getMethod().getName();
			
			if (methodName.equals("createEncounter") || methodName.equals("saveEncounter")
			        || methodName.equals("updateEncounter") || methodName.equals("voidEncounter")
			        || methodName.equals("upvoidEncounter") || methodName.equals("deleteEncounter")
			        || methodName.equals("purgeEncounter")) {
				Encounter encounter = (Encounter) invocation.getArguments()[0];
				if (encounter.getForm() != null) {
					Form form = encounter.getForm();
					FormAccessControlService svc = Context.getService(FormAccessControlService.class);
					boolean hasPrivilege = encounter.getId() != null ? svc.hasUpdatePrivilege(form) : svc
					        .hasCreatePrivilege(form);
					if (!hasPrivilege) {
						throw new APIAuthenticationException(OpenmrsUtil.getMessage(Constants.MODULE_ID
						        + ".privilegeRequired", (encounter.getId() != null ? "Edit" : "Create")));
					}
				}
			}
			
			Object o = invocation.proceed();
			
			if (!methodName.equals("getEncountersByVisitsAndPatientCount")) {
				if (methodName.startsWith("getEncounters")) {
					@SuppressWarnings("unchecked")
					Collection<Encounter> encounters = (Collection<Encounter>) o;
					Iterator<Encounter> i = encounters.iterator();
					FormAccessControlService svc = Context.getService(FormAccessControlService.class);
					while (i.hasNext()) {
						Encounter encounter = i.next();
						if (encounter.getForm() != null && !svc.hasViewPrivilege(encounter.getForm())) {
							i.remove();
						}
					}
				} else if (methodName.equals("getEncounter")) {
					FormAccessControlService svc = Context.getService(FormAccessControlService.class);
					Encounter encounter = (Encounter) o;
					if (encounter.getForm() != null && !svc.hasViewPrivilege(encounter.getForm())) {
						throw new APIAuthenticationException(OpenmrsUtil.getMessage(Constants.MODULE_ID
						        + ".privilegeRequired", "View"));
					}
				}
			}
			
			return o;
		}
	}
}
