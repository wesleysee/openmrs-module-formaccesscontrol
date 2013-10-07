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
package org.openmrs.module.formaccesscontrol.api.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Form;
import org.openmrs.Role;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.formaccesscontrol.FormAccessControl;
import org.openmrs.module.formaccesscontrol.api.db.FormAccessControlDAO;

/**
 * It is a default implementation of {@link FormAccessControlDAO}.
 */
public class HibernateFormAccessControlDAO implements FormAccessControlDAO {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private SessionFactory sessionFactory;
	
	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	/**
	 * Returns the form access control object originally passed in, which will have been persisted.
	 * 
	 * @see org.openmrs.module.formaccesscontrol.api.FormAccessControlService#createForm(org.openmrs.module.formaccesscontrol.FormAccessControl)
	 */
	@Override
	public FormAccessControl saveFormAccessControl(FormAccessControl formAccessControl) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(formAccessControl);
		return formAccessControl;
	}
	
	/**
	 * @see org.openmrs.module.formaccesscontrol.api.FormAccessControlService#getFormAccessControl(java.lang.Integer)
	 */
	@Override
	public FormAccessControl getFormAccessControl(Integer formAccessControlId) throws DAOException {
		return (FormAccessControl) sessionFactory.getCurrentSession().get(FormAccessControl.class, formAccessControlId);
	}
	
	/**
	 * @see org.openmrs.api.FormService#getFormAccessControls()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<FormAccessControl> getAllFormAccessControls() throws DAOException {
		return sessionFactory.getCurrentSession().createCriteria(FormAccessControl.class).createAlias("form", "form")
		        .createAlias("role", "role").addOrder(Order.asc("form.formId")).addOrder(Order.asc("role.role")).list();
	}
	
	/**
	 * @see org.openmrs.module.formaccesscontrol.api.FormAccessControlService#getFormAccessControls(Form)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<FormAccessControl> getFormAccessControls(Form form) throws DAOException {
		return sessionFactory.getCurrentSession().createCriteria(FormAccessControl.class).createAlias("role", "role")
		        .add(Restrictions.eq("form", form)).addOrder(Order.asc("role.role")).list();
	}
	
	/**
	 * @see org.openmrs.module.formaccesscontrol.api.FormAccessControlService#getFormAccessControl(Form,
	 *      Role)
	 */
	@Override
	public FormAccessControl getFormAccessControl(Form form, Role role) throws DAOException {
		return (FormAccessControl) sessionFactory.getCurrentSession().createCriteria(FormAccessControl.class)
		        .add(Restrictions.eq("form", form)).add(Restrictions.eq("role", role)).uniqueResult();
	}
	
	/**
	 * @see org.openmrs.module.formaccesscontrol.api.FormAccessControlService#deleteFormAccessControls(Form)
	 */
	@Override
	public void deleteFormAccessControls(Form form) throws DAOException {
		sessionFactory.getCurrentSession().createQuery("delete from FormAccessControl where form = :form")
		        .setParameter("form", form).executeUpdate();
	}
	
	/**
	 * @see org.openmrs.module.formaccesscontrol.api.FormAccessControlService#deleteFormAccessControls(Role)
	 */
	@Override
	public void deleteFormAccessControls(Role role) throws DAOException {
		sessionFactory.getCurrentSession().createQuery("delete from FormAccessControl where role = :role")
		        .setParameter("role", role).executeUpdate();
	}
}
