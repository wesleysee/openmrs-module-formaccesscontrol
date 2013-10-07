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
package org.openmrs.module.formaccesscontrol;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.formaccesscontrol.api.FormAccessControlService;
import org.openmrs.util.RoleConstants;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class FormAccessControlActivator implements ModuleActivator {
	
	protected Log log = LogFactory.getLog(getClass());
	
	/**
	 * @see ModuleActivator#willRefreshContext()
	 */
	@Override
	public void willRefreshContext() {
		log.info("Refreshing Form Access Control Module");
	}
	
	/**
	 * @see ModuleActivator#contextRefreshed()
	 */
	@Override
	public void contextRefreshed() {
		log.info("Form Access Control Module refreshed");
	}
	
	/**
	 * @see ModuleActivator#willStart()
	 */
	@Override
	public void willStart() {
		log.info("Starting Form Access Control Module");
	}
	
	/**
	 * @see ModuleActivator#started()
	 */
	@Override
	public void started() {
		log.info("Form Access Control Module started");
		System.out.println("======inside started");
		
		Context.addProxyPrivilege("View Forms");
		Context.addProxyPrivilege("View Form Access Control");
		Context.addProxyPrivilege("Manage Form Access Control");
		
		UserService userService = Context.getUserService();
		FormAccessControlService svc = Context.getService(FormAccessControlService.class);
		for (Form form : Context.getFormService().getAllForms()) {
			for (Role role : userService.getAllRoles()) {
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
		
		Context.removeProxyPrivilege("View Forms");
		Context.removeProxyPrivilege("View Form Access Control");
		Context.removeProxyPrivilege("Manage Form Access Control");
	}
	
	/**
	 * @see ModuleActivator#willStop()
	 */
	@Override
	public void willStop() {
		log.info("Stopping Form Access Control Module");
	}
	
	/**
	 * @see ModuleActivator#stopped()
	 */
	@Override
	public void stopped() {
		log.info("Form Access Control Module stopped");
	}
	
}
