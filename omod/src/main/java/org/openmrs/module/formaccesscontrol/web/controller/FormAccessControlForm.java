package org.openmrs.module.formaccesscontrol.web.controller;

import java.util.List;

import org.openmrs.module.formaccesscontrol.FormAccessControl;

public class FormAccessControlForm {
	
	private List<FormAccessControl> formAccessControls;
	
	public FormAccessControlForm(List<FormAccessControl> formAccessControls) {
		this.formAccessControls = formAccessControls;
	}
	
	public List<FormAccessControl> getFormAccessControls() {
		return formAccessControls;
	}
	
	public void setFormAccessControls(List<FormAccessControl> formAccessControls) {
		this.formAccessControls = formAccessControls;
	}
	
}
