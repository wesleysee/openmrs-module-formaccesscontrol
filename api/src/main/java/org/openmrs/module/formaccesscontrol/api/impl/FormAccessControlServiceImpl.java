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
package org.openmrs.module.formaccesscontrol.api.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Role;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.formaccesscontrol.FormAccessControl;
import org.openmrs.module.formaccesscontrol.api.FormAccessControlService;
import org.openmrs.module.formaccesscontrol.api.db.FormAccessControlDAO;
import org.openmrs.util.RoleConstants;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Predicate;

/**
 * It is a default implementation of {@link FormAccessControlService}.
 */
public class FormAccessControlServiceImpl extends BaseOpenmrsService implements FormAccessControlService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private FormAccessControlDAO dao;
	
	/**
	 * @param dao the dao to set
	 */
	public void setDao(FormAccessControlDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @return the dao
	 */
	public FormAccessControlDAO getDao() {
		return dao;
	}
	
	/**
	 * @see org.openmrs.module.formaccesscontrol.api.FormAccessControlService#saveFormAccessControl(FormAccessControl)
	 */
	@Override
	public FormAccessControl saveFormAccessControl(FormAccessControl formAccessControl) throws APIException {
		return dao.saveFormAccessControl(formAccessControl);
	}
	
	/**
	 * @see org.openmrs.module.formaccesscontrol.api.FormAccessControlService#getFormAccessControl(Integer)
	 */
	@Override
	public FormAccessControl getFormAccessControl(Integer formAccessControlId) throws APIException {
		return dao.getFormAccessControl(formAccessControlId);
	}
	
	/**
	 * @see org.openmrs.module.formaccesscontrol.api.FormAccessControlService#getAllFormAccessControls()
	 */
	@Override
	public List<FormAccessControl> getAllFormAccessControls() throws APIException {
		return dao.getAllFormAccessControls();
	}
	
	/**
	 * @see org.openmrs.module.formaccesscontrol.api.FormAccessControlService#getAllFormAccessControls(Form)
	 */
	@Override
	public List<FormAccessControl> getFormAccessControls(Form form) throws APIException {
		return dao.getFormAccessControls(form);
	}
	
	/**
	 * @see org.openmrs.module.formaccesscontrol.api.FormAccessControlService#getFormAccessControl(Form,
	 *      Role)
	 */
	@Override
	public FormAccessControl getFormAccessControl(Form form, Role role) throws APIException {
		return dao.getFormAccessControl(form, role);
	}
	
	/**
	 * @see org.openmrs.module.formaccesscontrol.api.FormAccessControlService#deleteFormAccessControls(Form)
	 */
	@Override
	public void deleteFormAccessControls(Form form) throws APIException {
		dao.deleteFormAccessControls(form);
	}
	
	/**
	 * @see org.openmrs.module.formaccesscontrol.api.FormAccessControlService#deleteFormAccessControls(Role)
	 */
	@Override
	public void deleteFormAccessControls(Role role) throws APIException {
		dao.deleteFormAccessControls(role);
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasCreatePrivilege(Form form) throws APIException {
		return hasPrivilege(form, new Predicate<FormAccessControl>() {
			
			@Override
			public boolean apply(FormAccessControl fac) {
				return fac.isCanCreate();
			}
		});
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasViewPrivilege(Form form) {
		return hasPrivilege(form, new Predicate<FormAccessControl>() {
			
			@Override
			public boolean apply(FormAccessControl fac) {
				return fac.isCanView();
			}
		});
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasUpdatePrivilege(Form form) {
		return hasPrivilege(form, new Predicate<FormAccessControl>() {
			
			@Override
			public boolean apply(FormAccessControl fac) {
				return fac.isCanUpdate();
			}
		});
	}
	
	public boolean hasPrivilege(Form form, Predicate<FormAccessControl> predicate) throws APIException {
		try {
			Set<Role> roles = Context.getUserContext().getAllRoles();
			
			for (Role role : roles) {
				if (role.getRole().equals(RoleConstants.SUPERUSER)) {
					return true;
				}
				FormAccessControl fac = getFormAccessControl(form, role);
				if (fac != null && predicate.apply(fac)) {
					return true;
				}
			}
		}
		catch (Exception e) {
			throw new APIException(e);
		}
		return false;
	}
}
