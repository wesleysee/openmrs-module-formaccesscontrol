package org.openmrs.module.formaccesscontrol.extension.html;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.formaccesscontrol.api.FormAccessControlService;
import org.openmrs.module.htmlformentry.extension.html.FormEntryHandlerExtension;
import org.openmrs.module.web.FormEntryContext;

public class FormAccessControlFormEntryHandlerExtension extends FormEntryHandlerExtension {
	
	@Override
	public Set<Form> getFormsModuleCanView() {
		Set<Form> forms = super.getFormsModuleCanView();
		
		Iterator<Form> i = forms.iterator();
		FormAccessControlService svc = Context.getService(FormAccessControlService.class);
		while (i.hasNext()) {
			Form form = i.next();
			if (!svc.hasViewPrivilege(form)) {
				i.remove();
			}
		}
		
		return forms;
	}
	
	@Override
	public Set<Form> getFormsModuleCanEdit() {
		Set<Form> forms = super.getFormsModuleCanEdit();
		
		Iterator<Form> i = forms.iterator();
		FormAccessControlService svc = Context.getService(FormAccessControlService.class);
		while (i.hasNext()) {
			Form form = i.next();
			if (!svc.hasUpdatePrivilege(form)) {
				i.remove();
			}
		}
		
		return forms;
	}
	
	@Override
	public List<Form> getFormsModuleCanEnter(FormEntryContext formEntryContext) {
		List<Form> forms = super.getFormsModuleCanEnter(formEntryContext);
		
		Iterator<Form> i = forms.iterator();
		FormAccessControlService svc = Context.getService(FormAccessControlService.class);
		while (i.hasNext()) {
			Form form = i.next();
			if (!svc.hasCreatePrivilege(form)) {
				i.remove();
			}
		}
		
		return forms;
	}
}
