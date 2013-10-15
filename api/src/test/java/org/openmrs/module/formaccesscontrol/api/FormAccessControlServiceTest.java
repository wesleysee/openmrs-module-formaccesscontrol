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

import static org.junit.Assert.assertNotNull;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Form;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.formaccesscontrol.FormAccessControl;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests {@link $ FormAccessControlService} .
 */
public class FormAccessControlServiceTest extends BaseModuleContextSensitiveTest {
	
	protected static final String DATASET_XML = "org/openmrs/module/formaccesscontrol/include/FormAccessControlServiceTest.xml";
	
	@Test
	public void shouldSetupContext() {
		assertNotNull(Context.getService(FormAccessControlService.class));
	}
	
	private FormAccessControlService getFormAccessControlService() {
		return Context.getService(FormAccessControlService.class);
	}
	
	@Before
	public void beforeTest() throws Exception {
		executeDataSet(DATASET_XML);
	}
	
	@Override
	@After
	public void deleteAllData() throws Exception {
		Context.addProxyPrivilege("View Users");
		super.deleteAllData();
		Context.removeProxyPrivilege("View Users");
	}
	
	/**
	 * @see FormAccessControlService#getAllFormAccessControls()
	 * @verifies return all form access controls
	 */
	@Test
	public void getAllFormAccessControls_shouldReturnAllFormAccessControls() throws Exception {
		Assert.assertEquals(8, getFormAccessControlService().getAllFormAccessControls().size());
	}
	
	/**
	 * @see FormAccessControlService#getFormAccessControl(Form,Role)
	 * @verifies return the form access control with the form and role given
	 */
	@Test
	public void getFormAccessControl_shouldReturnTheFormAccessControlWithTheFormAndRoleGiven() throws Exception {
		Assert.assertNotNull(getFormAccessControlService().getFormAccessControl(Context.getFormService().getForm(14),
		    Context.getUserService().getRole("Some Role")));
	}
	
	/**
	 * @see FormAccessControlService#getFormAccessControl(Integer)
	 * @verifies return null if no form access control exists with given formAccessControlId
	 */
	@Test
	public void getFormAccessControl_shouldReturnNullIfNoFormAccessControlExistsWithGivenFormAccessControlId()
	    throws Exception {
		Assert.assertNull(getFormAccessControlService().getFormAccessControl(10));
	}
	
	/**
	 * @see FormAccessControlService#getFormAccessControl(Integer)
	 * @verifies return the requested form access control
	 */
	@Test
	public void getFormAccessControl_shouldReturnTheRequestedFormAccessControl() throws Exception {
		Assert.assertNotNull(getFormAccessControlService().getFormAccessControl(4));
	}
	
	/**
	 * @see FormAccessControlService#getFormAccessControls(Form)
	 * @verifies return all forms with the given Form
	 */
	@Test
	public void getFormAccessControls_shouldReturnAllFormsWithTheGivenForm() throws Exception {
		Assert.assertEquals(4, getFormAccessControlService().getFormAccessControls(Context.getFormService().getForm(14))
		        .size());
	}
	
	/**
	 * @see FormAccessControlService#hasCreatePrivilege(Form)
	 * @verifies authorize if authenticated user has create privilege for the specified form
	 */
	@Test
	public void hasCreatePrivilege_shouldAuthorizeIfAuthenticatedUserHasCreatePrivilegeForTheSpecifiedForm()
	    throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Forms");
		Assert.assertTrue(getFormAccessControlService().hasCreatePrivilege(Context.getFormService().getForm(15)));
		Context.removeProxyPrivilege("View Forms");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Forms");
		Assert.assertTrue(getFormAccessControlService().hasCreatePrivilege(Context.getFormService().getForm(15)));
		Context.removeProxyPrivilege("View Forms");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Forms");
		Assert.assertTrue(getFormAccessControlService().hasCreatePrivilege(Context.getFormService().getForm(15)));
		Assert.assertTrue(getFormAccessControlService().hasCreatePrivilege(Context.getFormService().getForm(14)));
		Context.removeProxyPrivilege("View Forms");
		
		Context.logout();
	}
	
	/**
	 * @see FormAccessControlService#hasCreatePrivilege(Form)
	 * @verifies authorize if anonymous user has create privilege for the specified form
	 */
	@Test
	public void hasCreatePrivilege_shouldAuthorizeIfAnonymousUserHasCreatePrivilegeForTheSpecifiedForm() throws Exception {
		Context.logout();
		Context.addProxyPrivilege("View Forms");
		Assert.assertTrue(getFormAccessControlService().hasCreatePrivilege(Context.getFormService().getForm(15)));
		Context.removeProxyPrivilege("View Forms");
	}
	
	/**
	 * @see FormAccessControlService#hasCreatePrivilege(Form)
	 * @verifies not authorize if authenticated user does not have create privilege for the
	 *           specified
	 */
	@Test
	public void hasCreatePrivilege_shouldNotAuthorizeIfAuthenticatedUserDoesNotHaveCreatePrivilegeForTheSpecified()
	    throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Forms");
		Assert.assertFalse(getFormAccessControlService().hasCreatePrivilege(Context.getFormService().getForm(14)));
		Context.removeProxyPrivilege("View Forms");
		
		Context.logout();
	}
	
	/**
	 * @see FormAccessControlService#hasCreatePrivilege(Form)
	 * @verifies not authorize if anonymous user does not have create privilege for the specified
	 *           form
	 */
	@Test
	public void hasCreatePrivilege_shouldNotAuthorizeIfAnonymousUserDoesNotHaveCreatePrivilegeForTheSpecifiedForm()
	    throws Exception {
		Context.logout();
		Context.addProxyPrivilege("View Forms");
		Assert.assertFalse(getFormAccessControlService().hasCreatePrivilege(Context.getFormService().getForm(14)));
		Context.removeProxyPrivilege("View Forms");
	}
	
	/**
	 * @see FormAccessControlService#hasPrivilege(Form,String)
	 * @verifies authorize if authenticated user has specified privilege for the specified form
	 */
	@Test
	public void hasPrivilege_shouldAuthorizeIfAuthenticatedUserHasSpecifiedPrivilegeForTheSpecifiedForm() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Forms");
		Assert.assertTrue(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(15), "CREATE"));
		Assert.assertTrue(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(15), "UPDATE"));
		Assert.assertTrue(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(15), "VIEW"));
		Context.removeProxyPrivilege("View Forms");
		Context.logout();
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Forms");
		Assert.assertTrue(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(14), "VIEW"));
		Assert.assertTrue(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(14), "UPDATE"));
		Assert.assertTrue(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(15), "CREATE"));
		Assert.assertTrue(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(15), "UPDATE"));
		Assert.assertTrue(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(15), "VIEW"));
		Context.removeProxyPrivilege("View Forms");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Forms");
		Assert.assertTrue(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(14), "CREATE"));
		Assert.assertTrue(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(14), "VIEW"));
		Assert.assertTrue(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(15), "CREATE"));
		Assert.assertTrue(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(15), "UPDATE"));
		Assert.assertTrue(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(15), "VIEW"));
		Context.removeProxyPrivilege("View Forms");
		
		Context.logout();
	}
	
	/**
	 * @see FormAccessControlService#hasPrivilege(Form,String)
	 * @verifies authorize if anonymous user has specified privilege for the specified form
	 */
	@Test
	public void hasPrivilege_shouldAuthorizeIfAnonymousUserHasSpecifiedPrivilegeForTheSpecifiedForm() throws Exception {
		Context.logout();
		Context.addProxyPrivilege("View Forms");
		Assert.assertTrue(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(15), "VIEW"));
		Assert.assertTrue(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(15), "CREATE"));
		Assert.assertTrue(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(15), "UPDATE"));
		Assert.assertTrue(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(15), "EDIT"));
		Context.removeProxyPrivilege("View Forms");
	}
	
	/**
	 * @see FormAccessControlService#hasPrivilege(Form,String)
	 * @verifies not authorize if authenticated user does not have specified privilege for the
	 */
	@Test
	public void hasPrivilege_shouldNotAuthorizeIfAuthenticatedUserDoesNotHaveSpecifiedPrivilegeForThe() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Forms");
		Assert.assertFalse(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(14), "CREATE"));
		Assert.assertFalse(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(14), "UPDATE"));
		Context.removeProxyPrivilege("View Forms");
		
		Context.logout();
	}
	
	/**
	 * @see FormAccessControlService#hasPrivilege(Form,String)
	 * @verifies not authorize if anonymous user does not have specified privilege for the specified
	 */
	@Test
	public void hasPrivilege_shouldNotAuthorizeIfAnonymousUserDoesNotHaveSpecifiedPrivilegeForTheSpecified()
	    throws Exception {
		Context.logout();
		Context.addProxyPrivilege("View Forms");
		Assert.assertFalse(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(14), "VIEW"));
		Assert.assertFalse(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(14), "CREATE"));
		Assert.assertFalse(getFormAccessControlService().hasPrivilege(Context.getFormService().getForm(14), "UPDATE"));
		Context.removeProxyPrivilege("View Forms");
	}
	
	/**
	 * @see FormAccessControlService#hasUpdatePrivilege(Form)
	 * @verifies authorize if authenticated user has update privilege for the specified form
	 */
	@Test
	public void hasUpdatePrivilege_shouldAuthorizeIfAuthenticatedUserHasUpdatePrivilegeForTheSpecifiedForm()
	    throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Forms");
		Assert.assertTrue(getFormAccessControlService().hasUpdatePrivilege(Context.getFormService().getForm(15)));
		Context.removeProxyPrivilege("View Forms");
		Context.logout();
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Forms");
		Assert.assertTrue(getFormAccessControlService().hasUpdatePrivilege(Context.getFormService().getForm(15)));
		Assert.assertTrue(getFormAccessControlService().hasUpdatePrivilege(Context.getFormService().getForm(14)));
		Context.removeProxyPrivilege("View Forms");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Forms");
		Assert.assertTrue(getFormAccessControlService().hasUpdatePrivilege(Context.getFormService().getForm(15)));
		Context.removeProxyPrivilege("View Forms");
		
		Context.logout();
	}
	
	/**
	 * @see FormAccessControlService#hasUpdatePrivilege(Form)
	 * @verifies authorize if anonymous user has update privilege for the specified form
	 */
	@Test
	public void hasUpdatePrivilege_shouldAuthorizeIfAnonymousUserHasUpdatePrivilegeForTheSpecifiedForm() throws Exception {
		Context.addProxyPrivilege("View Forms");
		Assert.assertTrue(getFormAccessControlService().hasUpdatePrivilege(Context.getFormService().getForm(15)));
		Context.removeProxyPrivilege("View Forms");
	}
	
	/**
	 * @see FormAccessControlService#hasUpdatePrivilege(Form)
	 * @verifies not authorize if authenticated user does not have update privilege for the
	 *           specified
	 */
	@Test
	public void hasUpdatePrivilege_shouldNotAuthorizeIfAuthenticatedUserDoesNotHaveUpdatePrivilegeForTheSpecified()
	    throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Forms");
		Assert.assertFalse(getFormAccessControlService().hasUpdatePrivilege(Context.getFormService().getForm(14)));
		Context.removeProxyPrivilege("View Forms");
		Context.logout();
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Forms");
		Assert.assertFalse(getFormAccessControlService().hasUpdatePrivilege(Context.getFormService().getForm(14)));
		Context.removeProxyPrivilege("View Forms");
		
		Context.logout();
	}
	
	/**
	 * @see FormAccessControlService#hasUpdatePrivilege(Form)
	 * @verifies not authorize if anonymous user does not have update privilege for the specified
	 *           form
	 */
	@Test
	public void hasUpdatePrivilege_shouldNotAuthorizeIfAnonymousUserDoesNotHaveUpdatePrivilegeForTheSpecifiedForm()
	    throws Exception {
		Context.logout();
		Context.addProxyPrivilege("View Forms");
		Assert.assertFalse(getFormAccessControlService().hasUpdatePrivilege(Context.getFormService().getForm(14)));
		Context.removeProxyPrivilege("View Forms");
	}
	
	/**
	 * @see FormAccessControlService#hasViewPrivilege(Form)
	 * @verifies authorize if authenticated user has view privilege for the specified form
	 */
	@Test
	public void hasViewPrivilege_shouldAuthorizeIfAuthenticatedUserHasViewPrivilegeForTheSpecifiedForm() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Forms");
		Assert.assertTrue(getFormAccessControlService().hasViewPrivilege(Context.getFormService().getForm(15)));
		Context.removeProxyPrivilege("View Forms");
		Context.logout();
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Forms");
		Assert.assertTrue(getFormAccessControlService().hasViewPrivilege(Context.getFormService().getForm(14)));
		Assert.assertTrue(getFormAccessControlService().hasViewPrivilege(Context.getFormService().getForm(15)));
		Context.removeProxyPrivilege("View Forms");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Forms");
		Assert.assertTrue(getFormAccessControlService().hasViewPrivilege(Context.getFormService().getForm(14)));
		Assert.assertTrue(getFormAccessControlService().hasViewPrivilege(Context.getFormService().getForm(15)));
		Context.removeProxyPrivilege("View Forms");
		
		Context.logout();
	}
	
	/**
	 * @see FormAccessControlService#hasViewPrivilege(Form)
	 * @verifies authorize if anonymous user has view privilege for the specified form
	 */
	@Test
	public void hasViewPrivilege_shouldAuthorizeIfAnonymousUserHasViewPrivilegeForTheSpecifiedForm() throws Exception {
		Context.addProxyPrivilege("View Forms");
		Assert.assertTrue(getFormAccessControlService().hasViewPrivilege(Context.getFormService().getForm(15)));
		Context.removeProxyPrivilege("View Forms");
	}
	
	/**
	 * @see FormAccessControlService#hasViewPrivilege(Form)
	 * @verifies not authorize if authenticated user does not have view privilege for the specified
	 */
	@Test
	public void hasViewPrivilege_shouldNotAuthorizeIfAuthenticatedUserDoesNotHaveViewPrivilegeForTheSpecified()
	    throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Forms");
		Assert.assertFalse(getFormAccessControlService().hasViewPrivilege(Context.getFormService().getForm(14)));
		Context.removeProxyPrivilege("View Forms");
		Context.logout();
	}
	
	/**
	 * @see FormAccessControlService#hasViewPrivilege(Form)
	 * @verifies not authorize if anonymous user does not have view privilege for the specified form
	 */
	@Test
	public void hasViewPrivilege_shouldNotAuthorizeIfAnonymousUserDoesNotHaveViewPrivilegeForTheSpecifiedForm()
	    throws Exception {
		Context.logout();
		Context.addProxyPrivilege("View Forms");
		Assert.assertFalse(getFormAccessControlService().hasViewPrivilege(Context.getFormService().getForm(14)));
		Context.removeProxyPrivilege("View Forms");
	}
	
	/**
	 * @see FormAccessControlService#saveFormAccessControl(FormAccessControl)
	 * @verifies save given form successfully
	 */
	@Test
	public void saveFormAccessControl_shouldSaveGivenFormSuccessfully() throws Exception {
		Form form = Context.getFormService().getForm(16);
		Role role = Context.getUserService().getRole("Some Role");
		
		FormAccessControl fac = new FormAccessControl();
		fac.setForm(form);
		fac.setRole(role);
		fac.setCanCreate(true);
		
		getFormAccessControlService().saveFormAccessControl(fac);
		
		Assert.assertNotNull(fac.getUuid());
		Assert.assertNotNull(getFormAccessControlService().getFormAccessControl(form, role));
	}
	
	/**
	 * @see FormAccessControlService#saveFormAccessControl(FormAccessControl)
	 * @verifies update an existing form
	 */
	@Test
	public void saveFormAccessControl_shouldUpdateAnExistingForm() throws Exception {
		FormAccessControl fac = getFormAccessControlService().getFormAccessControl(1);
		fac.setCanCreate(true);
		getFormAccessControlService().saveFormAccessControl(fac);
		
		FormAccessControl newFac = getFormAccessControlService().getFormAccessControl(1);
		Assert.assertTrue(newFac.isCanCreate());
	}
	
	/**
	 * @see FormAccessControlService#deleteFormAccessControls(Form)
	 * @verifies delete form access controls for form successfully
	 */
	@Test
	public void deleteFormAccessControls_shouldDeleteFormAccessControlsForFormSuccessfully() throws Exception {
		Form form = Context.getFormService().getForm(14);
		getFormAccessControlService().deleteFormAccessControls(form);
		Assert.assertTrue(getFormAccessControlService().getFormAccessControls(form).isEmpty());
	}
	
	/**
	 * @see FormAccessControlService#deleteFormAccessControls(Role)
	 * @verifies delete form access controls for role successfully
	 */
	@Test
	public void deleteFormAccessControls_shouldDeleteFormAccessControlsForRoleSuccessfully() throws Exception {
		Role role = Context.getUserService().getRole("Some Role");
		getFormAccessControlService().deleteFormAccessControls(role);
		Assert.assertNull(getFormAccessControlService().getFormAccessControl(Context.getFormService().getForm(14), role));
		Assert.assertNull(getFormAccessControlService().getFormAccessControl(Context.getFormService().getForm(15), role));
	}
}
