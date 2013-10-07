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
import org.openmrs.Form;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.formaccesscontrol.FormAccessControl;
import org.openmrs.module.formaccesscontrol.api.FormAccessControlService;
import org.openmrs.util.RoleConstants;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;

/**
 * AOP class used to intercept and log calls to FormService methods
 */
public class FormServiceAdvice implements MethodBeforeAdvice, AfterReturningAdvice {
	
	protected static final Log log = LogFactory.getLog(FormServiceAdvice.class);
	
	/**
	 * @see org.springframework.aop.MethodBeforeAdvice#before(Method, Object[], Object)
	 */
	@Override
	public void before(Method method, Object[] args, Object target) throws Throwable {
		if (method.getName().equals("deleteForm") || method.getName().equals("updatePatient")) {}
		
	}
	
	/**
	 * @see org.springframework.aop.AfterReturningAdvice#afterReturning(Object, Method, Object[],
	 *      Object)
	 */
	@Override
	public void afterReturning(Object returnVal, Method method, Object[] args, Object target) throws Throwable {
		if (method.getName().equals("deleteForm") || method.getName().equals("purgeForm")) {
			FormAccessControlService svc = Context.getService(FormAccessControlService.class);
			Form form = (Form) args[0];
			svc.deleteFormAccessControls(form);
		} else if (method.getName().equals("createForm") || method.getName().equals("saveForm")) {
			Context.addProxyPrivilege("Manage Form Access Control");
			FormAccessControlService svc = Context.getService(FormAccessControlService.class);
			Form form = (Form) returnVal;
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
		}
	}
}
