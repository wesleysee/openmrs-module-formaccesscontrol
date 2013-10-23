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
package org.openmrs.module.formaccesscontrol.web.taglib;

import javax.servlet.jsp.tagext.TagSupport;

import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.formaccesscontrol.api.FormAccessControlService;

public class FormPrivilegeTag extends TagSupport {
	
	public static final long serialVersionUID = 11233L;
	
	//private final Log log = LogFactory.getLog(getClass());
	
	private String privilege;
	
	private Form form;
	
	@Override
	public int doStartTag() {
		boolean hasPrivilege = false;
		
		if (form == null) {
			hasPrivilege = true;
		} else {
			FormAccessControlService svc = Context.getService(FormAccessControlService.class);
			
			if (privilege.contains(",")) {
				String[] privs = privilege.split(",");
				for (String p : privs) {
					if (svc.hasPrivilege(form, p.trim())) {
						hasPrivilege = true;
						break;
					}
				}
			} else {
				hasPrivilege = svc.hasPrivilege(form, privilege);
			}
		}
		
		if (hasPrivilege) {
			pageContext.setAttribute("authenticatedUser", Context.getAuthenticatedUser());
			return EVAL_BODY_INCLUDE;
		} else {
			return SKIP_BODY;
		}
	}
	
	/**
	 * @return Returns the privilege.
	 */
	public String getPrivilege() {
		return privilege;
	}
	
	/**
	 * @param converse The privilege to set.
	 */
	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}
	
	public Form getForm() {
		return form;
	}
	
	public void setForm(Form form) {
		this.form = form;
	}
	
}
