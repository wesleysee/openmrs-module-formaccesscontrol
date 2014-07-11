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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.Extension.MEDIA_TYPE;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.formaccesscontrol.api.FormAccessControlService;
import org.openmrs.module.htmlformentry.extension.html.FormEntryHandlerExtension;
import org.openmrs.util.RoleConstants;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class FormAccessControlActivator implements ModuleActivator {
	
	private static FormEntryHandlerExtension htmlFormEntryHandlerExtension = null;
	
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
		
		removeHtmlFormEntryFormEntryHandlerExtension();
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
		
		Context.addProxyPrivilege("View Forms");
		Context.addProxyPrivilege(Constants.PRIV_VIEW_FORM_ACCESS_CONTROL);
		Context.addProxyPrivilege(Constants.PRIV_MANAGE_FORM_ACCESS_CONTROL);
		
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
		Context.removeProxyPrivilege(Constants.PRIV_VIEW_FORM_ACCESS_CONTROL);
		Context.removeProxyPrivilege(Constants.PRIV_MANAGE_FORM_ACCESS_CONTROL);
		
		removeHtmlFormEntryFormEntryHandlerExtension();
		
	}
	
	private void removeHtmlFormEntryFormEntryHandlerExtension() {
		Map<String, List<Extension>> extensionMap = ModuleFactory.getExtensionMap();
		
		Iterator<Extension> iterator = extensionMap.get(getHtmlFormEntryHandlerExtensionName()).iterator();
		while (iterator.hasNext()) {
			Extension extension = iterator.next();
			
			if (extension.getClass().equals(FormEntryHandlerExtension.class)) {
				htmlFormEntryHandlerExtension = (FormEntryHandlerExtension) extension;
				iterator.remove();
			}
		}
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
		
		if (htmlFormEntryHandlerExtension != null) {
			ModuleFactory.getExtensionMap().get(getHtmlFormEntryHandlerExtensionName()).add(htmlFormEntryHandlerExtension);
			htmlFormEntryHandlerExtension = null;
		}
	}
	
	private String getHtmlFormEntryHandlerExtensionName() {
		return Extension.toExtensionId("org.openmrs.module.web.extension.FormEntryHandler", MEDIA_TYPE.html);
	}
}
