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

/**
 * AOP class used to intercept and log calls to FormService methods
 */
public class UserServiceAdvice implements AfterReturningAdvice {
	
	protected static final Log log = LogFactory.getLog(UserServiceAdvice.class);
	
	/**
	 * @see org.springframework.aop.AfterReturningAdvice#afterReturning(Object, Method, Object[],
	 *      Object)
	 */
	@Override
	public void afterReturning(Object returnVal, Method method, Object[] args, Object target) throws Throwable {
		if (method.getName().equals("purgeRole")) {
			FormAccessControlService svc = Context.getService(FormAccessControlService.class);
			Role role = (Role) args[0];
			svc.deleteFormAccessControls(role);
		} else if (method.getName().equals("saveRole")) {
			Context.addProxyPrivilege("Manage Form Access Control");
			FormAccessControlService svc = Context.getService(FormAccessControlService.class);
			Role role = (Role) returnVal;
			for (Form form : Context.getFormService().getAllForms()) {
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
