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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.formaccesscontrol.Constants;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.web.controller.HtmlFormEntryController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HtmlFormAccessControlEntryController {
	
	@Autowired
	private HtmlFormEntryController htmlFormEntryController;
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(method = RequestMethod.GET)
	public String showPage() {
		return "module/" + Constants.MODULE_ID + "/htmlFormEntry";
	}
	
	@ModelAttribute("command")
	public FormEntrySession getFormEntrySession(HttpServletRequest request,
	                                            // @RequestParam doesn't pick up query parameters (in the url) in a POST, so I'm handling encounterId, modeParam, and which specially
	                                            /*@RequestParam(value="mode", required=false) String modeParam,*/
	                                            /*@RequestParam(value="encounterId", required=false) Integer encounterId,*/
	                                            /*@RequestParam(value="which", required=false) String which,*/
	                                            @RequestParam(value = "patientId", required = false) Integer patientId,
	                                            /*@RequestParam(value="personId", required=false) Integer personId,*/
	                                            @RequestParam(value = "formId", required = false) Integer formId,
	                                            @RequestParam(value = "htmlformId", required = false) Integer htmlFormId,
	                                            @RequestParam(value = "returnUrl", required = false) String returnUrl,
	                                            @RequestParam(value = "formModifiedTimestamp", required = false) Long formModifiedTimestamp,
	                                            @RequestParam(value = "encounterModifiedTimestamp", required = false) Long encounterModifiedTimestamp,
	                                            @RequestParam(value = "hasChangedInd", required = false) String hasChangedInd)
	    throws Exception {
		return htmlFormEntryController.getFormEntrySession(request, patientId, formId, htmlFormId, returnUrl,
		    formModifiedTimestamp, encounterModifiedTimestamp, hasChangedInd);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView handleSubmit(@ModelAttribute("command") FormEntrySession session, Errors errors,
	                                 HttpServletRequest request, Model model) throws Exception {
		return htmlFormEntryController.handleSubmit(session, errors, request, model);
	}
}
