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
package org.openmrs.module.formaccesscontrol.api;

import java.util.List;

import org.openmrs.Form;
import org.openmrs.Role;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.formaccesscontrol.Constants;
import org.openmrs.module.formaccesscontrol.FormAccessControl;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service exposes module's core functionality. It is a Spring managed bean which is configured
 * in moduleApplicationContext.xml.
 * <p>
 * It can be accessed only via Context:<br>
 * <code>
 * Context.getService(FormAccessControlService.class).someMethod();
 * </code>
 * 
 * @see org.openmrs.api.context.Context
 */
@Transactional
public interface FormAccessControlService extends OpenmrsService {
	
	/**
	 * Create or update the given Form in the database
	 * 
	 * @param form the Form to save
	 * @return the Form that was saved
	 * @throws APIException
	 * @should save given form successfully
	 * @should update an existing form
	 */
	@Transactional
	@Authorized(Constants.PRIV_MANAGE_FORM_ACCESS_CONTROL)
	public FormAccessControl saveFormAccessControl(FormAccessControl formAccessControl) throws APIException;
	
	/**
	 * Get form access control by internal form identifier
	 * 
	 * @param formAccessControlId <code>Integer</code> internal identifier for requested Form Access
	 *            Control
	 * @return requested <code>FormAccessControl</code>
	 * @throws APIException
	 * @should return null if no form access control exists with given formAccessControlId
	 * @should return the requested form access control
	 */
	@Transactional(readOnly = true)
	@Authorized(Constants.PRIV_VIEW_FORM_ACCESS_CONTROL)
	public FormAccessControl getFormAccessControl(Integer formAccessControlId) throws APIException;
	
	/**
	 * Get all form access controls
	 * 
	 * @return the list of all form access controls
	 * @throws APIException
	 * @should return all form access controls
	 */
	@Transactional(readOnly = true)
	@Authorized(Constants.PRIV_VIEW_FORM_ACCESS_CONTROL)
	public List<FormAccessControl> getAllFormAccessControls() throws APIException;
	
	/**
	 * Get the form access controls by form
	 * 
	 * @param form the form to get
	 * @return the list of form access controls with the given form
	 * @throws APIException
	 * @should return all forms with the given Form
	 */
	@Transactional(readOnly = true)
	@Authorized(Constants.PRIV_VIEW_FORM_ACCESS_CONTROL)
	public List<FormAccessControl> getFormAccessControls(Form form) throws APIException;
	
	/**
	 * Get form access control by form and role
	 * 
	 * @param form the form to get
	 * @param role the role to get
	 * @return the form access control with the form and role given
	 * @throws APIException
	 * @should return the form access control with the form and role given
	 */
	@Transactional(readOnly = true)
	@Authorized(Constants.PRIV_VIEW_FORM_ACCESS_CONTROL)
	public FormAccessControl getFormAccessControl(Form form, Role role) throws APIException;
	
	/**
	 * Completely removes Form Access Controls with the given Form from the database. This is not
	 * reversible.
	 * 
	 * @param form
	 * @throws APIException
	 * @should delete given forms successfully
	 */
	@Transactional
	@Authorized(Constants.PRIV_MANAGE_FORM_ACCESS_CONTROL)
	public void deleteFormAccessControls(Form form) throws APIException;
	
	/**
	 * Completely removes Form Access Controls with the given Role from the database. This is not
	 * reversible.
	 * 
	 * @param role
	 * @throws APIException
	 * @should delete given forms successfully
	 */
	@Transactional
	@Authorized(Constants.PRIV_MANAGE_FORM_ACCESS_CONTROL)
	public void deleteFormAccessControls(Role role) throws APIException;
	
	@Transactional(readOnly = true)
	public boolean hasCreatePrivilege(Form form) throws APIException;
	
	@Transactional(readOnly = true)
	public boolean hasViewPrivilege(Form form) throws APIException;
	
	@Transactional(readOnly = true)
	public boolean hasUpdatePrivilege(Form form) throws APIException;
}
