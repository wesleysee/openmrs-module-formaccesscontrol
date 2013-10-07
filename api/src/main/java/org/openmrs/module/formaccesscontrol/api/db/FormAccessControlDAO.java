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
package org.openmrs.module.formaccesscontrol.api.db;

import java.util.List;

import org.openmrs.Form;
import org.openmrs.Role;
import org.openmrs.api.FormService;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.formaccesscontrol.FormAccessControl;
import org.openmrs.module.formaccesscontrol.api.FormAccessControlService;

/**
 * Database methods for {@link FormAccessControlService}.
 */
public interface FormAccessControlDAO {
	
	/**
	 * @see FormService#saveFormAccessControl(FormAccessControl)
	 */
	public FormAccessControl saveFormAccessControl(FormAccessControl formAccessControl) throws DAOException;
	
	/**
	 * Get form access control by internal form identifier
	 * 
	 * @param formAccessControlId <code>Integer</code> internal identifier for requested Form Access
	 *            Control
	 * @return requested <code>FormAccessControl</code>
	 * @throws DAOException
	 */
	public FormAccessControl getFormAccessControl(Integer formAccessControlId) throws DAOException;
	
	/**
	 * Get all form access controls
	 * 
	 * @return the list of all form access controls
	 * @throws DAOException
	 */
	public List<FormAccessControl> getAllFormAccessControls() throws DAOException;
	
	/**
	 * Get the form access controls by form
	 * 
	 * @param form the form to get
	 * @return the list of form access controls with the given form
	 * @throws DAOException
	 */
	public List<FormAccessControl> getFormAccessControls(Form form) throws DAOException;
	
	/**
	 * Get form access control by form and role
	 * 
	 * @param form the form to get
	 * @param role the role to get
	 * @return the form access control with the form and role given
	 * @throws DAOException
	 */
	public FormAccessControl getFormAccessControl(Form form, Role role) throws DAOException;
	
	/**
	 * Delete form access controls from database. This is included for troubleshooting and low-level
	 * system administration. This should only be called when the Form is deleted, which ideally
	 * should never happen.
	 * 
	 * @param form Form to delete
	 * @throws DAOException
	 */
	public void deleteFormAccessControls(Form form) throws DAOException;
	
	/**
	 * @see org.openmrs.module.formaccesscontrol.api.FormAccessControlService#deleteFormAccessControls(Role)
	 */
	public void deleteFormAccessControls(Role role) throws DAOException;
}
