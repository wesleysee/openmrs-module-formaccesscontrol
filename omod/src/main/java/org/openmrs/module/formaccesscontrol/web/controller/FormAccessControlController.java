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
package org.openmrs.module.formaccesscontrol.web.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.formaccesscontrol.Constants;
import org.openmrs.module.formaccesscontrol.FormAccessControl;
import org.openmrs.module.formaccesscontrol.api.FormAccessControlService;
import org.openmrs.util.RoleConstants;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FormAccessControlController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(method = RequestMethod.GET, value = "module/" + Constants.MODULE_ID + "/formAccessControlEdit")
	public void manageFormAccessControls() {
		if (!hasPrivilege()) {
			return;
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "module/" + Constants.MODULE_ID + "/formAccessControlEdit")
	public void obSubmit(@ModelAttribute("formAccessControlForm") FormAccessControlForm formAccessControlForm,
	                     Errors errors, HttpSession session) {
		if (!hasPrivilege()) {
			errors.reject("auth.invalid");
			return;
		}
		
		FormAccessControlService svc = Context.getService(FormAccessControlService.class);
		for (FormAccessControl formAccessControl : formAccessControlForm.getFormAccessControls()) {
			System.out.println("fac: " + formAccessControl.getRole() + " :: " + formAccessControl.isCanCreate());
			svc.saveFormAccessControl(formAccessControl);
		}
		
		session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "formaccesscontrol.saved");
	}
	
	@ModelAttribute("superuser")
	public String getSuperUser() {
		return RoleConstants.SUPERUSER;
	}
	
	@ModelAttribute("form")
	public Form getForm(@RequestParam("formId") Form form) {
		return form;
	}
	
	@ModelAttribute("formAccessControlForm")
	public FormAccessControlForm getFormAccessControls(@RequestParam("formId") Form form) {
		return new FormAccessControlForm(Context.getService(FormAccessControlService.class).getFormAccessControls(form));
	}
	
	private boolean hasPrivilege() {
		return Context.hasPrivilege(Constants.PRIV_MANAGE_FORM_ACCESS_CONTROL);
	}
}
