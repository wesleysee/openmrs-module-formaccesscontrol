package org.openmrs.module.formaccesscontrol.extension.html;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.Extension;
import org.openmrs.module.formaccesscontrol.Constants;

/**
 * Adds Access Control links to the form edit page.
 * 
 * @author Spencer
 */
public class FormHeaderExt extends Extension {
	
	private String formId;
	
	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	@Override
	public void initialize(Map<String, String> parameters) {
		formId = parameters.get("formId");
	}
	
	public Map<String, String> getLinks() {
		
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		
		if (StringUtils.isBlank(formId)) {
			return map;
		}
		map.put("module/" + Constants.MODULE_ID + "/formAccessControlEdit.form?formId=" + formId, Constants.MODULE_ID
		        + ".manage");
		
		return map;
	}
}
