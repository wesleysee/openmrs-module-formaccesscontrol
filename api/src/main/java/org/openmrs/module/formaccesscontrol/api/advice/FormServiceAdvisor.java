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

import java.util.Iterator;
import java.util.List;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Role;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.formaccesscontrol.Constants;
import org.openmrs.module.formaccesscontrol.FormAccessControl;
import org.openmrs.module.formaccesscontrol.api.FormAccessControlService;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.util.RoleConstants;
import org.springframework.aop.Advisor;

/**
 * AOP class used to intercept and log calls to FormService methods
 */
public class FormServiceAdvisor implements Advisor {
	
	protected static final Log log = LogFactory.getLog(FormServiceAdvisor.class);
	
	@Override
	public Advice getAdvice() {
		return new FormServiceAroundAdvice();
	}
	
	@Override
	public boolean isPerInstance() {
		return false;
	}
	
	private class FormServiceAroundAdvice implements MethodInterceptor {
		
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			Object o = invocation.proceed();
			
			String methodName = invocation.getMethod().getName();
			
			if (methodName.equals("deleteForm") || methodName.equals("purgeForm")) {
				FormAccessControlService svc = Context.getService(FormAccessControlService.class);
				Form form = (Form) invocation.getArguments()[0];
				svc.deleteFormAccessControls(form);
			} else if (methodName.equals("createForm") || methodName.equals("saveForm")) {
				Context.addProxyPrivilege("Manage Form Access Control");
				FormAccessControlService svc = Context.getService(FormAccessControlService.class);
				Form form = (Form) o;
				for (Role role : Context.getUserService().getAllRoles()) {
					if (role.getRole().equals(RoleConstants.SUPERUSER)) {
						continue;
					}
					if (svc.getFormAccessControl(form, role) == null) {
						FormAccessControl formAccessControl = new FormAccessControl();
						formAccessControl.setForm(form);
						formAccessControl.setRole(role);
						formAccessControl.setCanCreate(false);
						formAccessControl.setCanUpdate(false);
						formAccessControl.setCanView(false);
						svc.saveFormAccessControl(formAccessControl);
					}
				}
			} else if (methodName.equals("getAllForms") || methodName.equals("getForms")) {
				@SuppressWarnings("unchecked")
				List<Form> forms = (List<Form>) o;
				Iterator<Form> i = forms.iterator();
				FormAccessControlService svc = Context.getService(FormAccessControlService.class);
				while (i.hasNext()) {
					Form form = i.next();
					if (form != null && !svc.hasViewPrivilege(form)) {
						i.remove();
					}
				}
			} else if (methodName.startsWith("getForm") && o instanceof Form) {
				Form form = (Form) o;
				FormAccessControlService svc = Context.getService(FormAccessControlService.class);
				if (form != null && !svc.hasViewPrivilege(form)) {
					throw new APIAuthenticationException(OpenmrsUtil.getMessage(Constants.MODULE_ID + ".privilegeRequired",
					    "View"));
				}
			}
			
			return o;
		}
	}
}
