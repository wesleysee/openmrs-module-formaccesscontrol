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

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.formaccesscontrol.Constants;
import org.openmrs.module.formaccesscontrol.api.FormAccessControlService;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.ValidationException;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.aop.Advisor;

/**
 * AOP class used to intercept and log calls to FormService methods
 */
public class HtmlFormEntryServiceAdvisor implements Advisor {
	
	protected static final Log log = LogFactory.getLog(HtmlFormEntryServiceAdvisor.class);
	
	@Override
	public Advice getAdvice() {
		return new HtmlFormEntryServiceAroundAdvice();
	}
	
	@Override
	public boolean isPerInstance() {
		return false;
	}
	
	private class HtmlFormEntryServiceAroundAdvice implements MethodInterceptor {
		
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			String methodName = invocation.getMethod().getName();
			if (methodName.equals("applyActions")) {
				try {
					return invocation.proceed();
				}
				catch (APIAuthenticationException e) {
					throw new ValidationException(e.getMessage());
				}
			}
			
			Object o = invocation.proceed();
			
			if (methodName.equals("getHtmlForm") || methodName.equals("getHtmlFormByForm")) {
				FormAccessControlService svc = Context.getService(FormAccessControlService.class);
				HtmlForm htmlForm = (HtmlForm) o;
				if (!svc.hasViewPrivilege(htmlForm.getForm())) {
					throw new APIAuthenticationException(OpenmrsUtil.getMessage(Constants.MODULE_ID + ".privilegeRequired",
					    "View"));
				}
			}
			
			return o;
		}
	}
	
}
