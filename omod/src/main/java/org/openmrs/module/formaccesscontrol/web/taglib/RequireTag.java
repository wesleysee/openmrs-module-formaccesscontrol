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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.formaccesscontrol.api.FormAccessControlService;
import org.openmrs.web.WebConstants;
import org.openmrs.web.user.UserProperties;
import org.springframework.util.StringUtils;

public class RequireTag extends TagSupport {
	
	public static final long serialVersionUID = 122998L;
	
	private final Log log = LogFactory.getLog(getClass());
	
	private Form form;
	
	private String privilege;
	
	private String allPrivileges;
	
	private String anyPrivilege;
	
	private String otherwise;
	
	private String redirect;
	
	private boolean errorOccurred;
	
	/**
	 * This is where all the magic happens. The privileges are checked and the user is redirected if
	 * need be. <br/>
	 * <br/>
	 * Returns SKIP_PAGE if the user doesn't have the privilege and SKIP_BODY if it does.
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 * @should allow user with the privilege
	 * @should allow user to have any privilege
	 * @should allow user with all privileges
	 * @should reject user without the privilege
	 * @should reject user without any of the privileges
	 * @should reject user without all of the privileges
	 */
	@Override
	public int doStartTag() {
		if (form == null) {
			return SKIP_BODY;
		}
		
		errorOccurred = false;
		HttpServletResponse httpResponse = (HttpServletResponse) pageContext.getResponse();
		HttpSession httpSession = pageContext.getSession();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String request_ip_addr = request.getLocalAddr();
		String session_ip_addr = (String) httpSession.getAttribute(WebConstants.OPENMRS_CLIENT_IP_HTTPSESSION_ATTR);
		
		UserContext userContext = Context.getUserContext();
		
		if (userContext == null && privilege != null) {
			log.error("userContext is null. Did this pass through a filter?");
			//httpSession.removeAttribute(WebConstants.OPENMRS_CONTEXT_HTTPSESSION_ATTR);
			//TODO find correct error to throw 
			throw new APIException("The context is currently null.  Please try reloading the site.");
		}
		
		// Parse comma-separated list of privileges in allPrivileges and anyPrivileges attributes
		String[] allPrivilegesArray = StringUtils.commaDelimitedListToStringArray(allPrivileges);
		String[] anyPrivilegeArray = StringUtils.commaDelimitedListToStringArray(anyPrivilege);
		
		FormAccessControlService svc = Context.getService(FormAccessControlService.class);
		boolean hasPrivilege = hasPrivileges(svc, form, privilege, allPrivilegesArray, anyPrivilegeArray);
		if (!hasPrivilege) {
			errorOccurred = true;
			if (userContext.isAuthenticated()) {
				String referer = request.getHeader("Referer");
				// If the user has just authenticated, but is still not authorized to see the page.
				if (referer != null && referer.contains("login.")) {
					try {
						httpResponse.sendRedirect(request.getContextPath()); // Redirect to the home page.
						return SKIP_PAGE;
					}
					catch (IOException e) {
						// oops, cannot redirect
						log.error("Unable to redirect to the home page", e);
						throw new APIException(e);
					}
				}
				
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "require.unauthorized");
				log.warn("The user: '" + Context.getAuthenticatedUser() + "' has attempted to access: " + redirect
				        + " which requires privilege: " + privilege + " or one of: " + allPrivileges + " or any of "
				        + anyPrivilege);
			} else {
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "require.login");
			}
		} else if (hasPrivilege && userContext.isAuthenticated()) {
			// redirect users to password change form
			User user = userContext.getAuthenticatedUser();
			log.debug("Login redirect: " + redirect);
			if (new UserProperties(user.getUserProperties()).isSupposedToChangePassword()
			        && !redirect.contains("options.form")) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "User.password.change");
				errorOccurred = true;
				redirect = request.getContextPath() + "/options.form#Change Login Info";
				otherwise = redirect;
				try {
					httpResponse.sendRedirect(redirect);
					return SKIP_PAGE;
				}
				catch (IOException e) {
					// oops, cannot redirect
					log.error("Unable to redirect for password change: " + redirect, e);
					throw new APIException(e);
				}
			}
		}
		
		if (differentIpAddresses(session_ip_addr, request_ip_addr)) {
			errorOccurred = true;
			// stops warning message in IE when refreshing repeatedly
			if ("0.0.0.0".equals(request_ip_addr) == false) {
				log.warn("Invalid ip addr: expected " + session_ip_addr + ", but found: " + request_ip_addr);
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "require.ip_addr");
			}
		}
		
		log.debug("session ip addr: " + session_ip_addr);
		
		if (errorOccurred) {
			
			String url = "";
			if (redirect != null && !redirect.equals("")) {
				url = request.getContextPath() + redirect;
			} else {
				url = request.getRequestURI();
			}
			
			if (request.getQueryString() != null) {
				url = url + "?" + request.getQueryString();
			}
			httpSession.setAttribute(WebConstants.OPENMRS_LOGIN_REDIRECT_HTTPSESSION_ATTR, url);
			try {
				httpResponse.sendRedirect(request.getContextPath() + otherwise);
				return SKIP_PAGE;
			}
			catch (IOException e) {
				// oops, cannot redirect
				throw new APIException(e);
			}
		}
		
		return SKIP_BODY;
	}
	
	/**
	 * Determines if the given ip addresses are the same.
	 * 
	 * @param session_ip_addr
	 * @param request_ip_addr
	 * @return true/false whether these IPs are different
	 */
	private boolean differentIpAddresses(String sessionIpAddr, String requestIpAddr) {
		if (sessionIpAddr == null || requestIpAddr == null) {
			return false;
		}
		
		// IE7 and firefox store "localhost" IP addresses differently.
		// To accomodate switching from firefox browing to IE taskpane,
		// we assume these addresses to be equivalent
		List<String> equivalentAddresses = new ArrayList<String>();
		equivalentAddresses.add("127.0.0.1");
		equivalentAddresses.add("0.0.0.0");
		
		// if the addresses are equal, all is well
		if (sessionIpAddr.equals(requestIpAddr)) {
			return false;
		} else if (equivalentAddresses.contains(sessionIpAddr) && equivalentAddresses.contains(requestIpAddr)) {
			return false;
		}
		
		// the IP addresses were not equal, (don't continue with this user)
		return true;
	}
	
	/**
	 * Returns true if all of the following three are true:
	 * <ul>
	 * <li>privilege is not defined OR user has privilege</li>
	 * <li>allPrivileges is not defined OR user has every privilege in allPrivileges</li>
	 * <li>anyPrivilege is not defined OR user has at least one of the privileges in anyPrivileges</li>
	 * </ul>
	 * 
	 * @param svc form access control service
	 * @param form form
	 * @param privilege a single required privilege
	 * @param allPrivilegesArray an array of required privileges
	 * @param anyPrivilegeArray an array of privileges, at least one of which is required
	 * @return true if privilege conditions are met
	 */
	private boolean hasPrivileges(FormAccessControlService svc, Form form, String privilege, String[] allPrivilegesArray,
	                              String[] anyPrivilegeArray) {
		
		if (privilege != null && !svc.hasPrivilege(form, privilege.trim())) {
			return false;
		}
		if (allPrivilegesArray.length > 0 && !hasAllPrivileges(svc, form, allPrivilegesArray)) {
			return false;
		}
		if (anyPrivilegeArray.length > 0 && !hasAnyPrivilege(svc, form, anyPrivilegeArray)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns true if user has all privileges
	 * 
	 * @param svc form access control service
	 * @param form form
	 * @param anyPriviegeArray list of privileges
	 * @return true if user has at least one of the privileges
	 */
	private boolean hasAllPrivileges(FormAccessControlService svc, Form form, String[] allPrivilegesArray) {
		for (String p : allPrivilegesArray) {
			if (!svc.hasPrivilege(form, p.trim())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns true if user has any of the privileges
	 * 
	 * @param svc form access control service
	 * @param form form
	 * @param anyPriviegeArray list of privileges
	 * @return true if user has at least one of the privileges
	 */
	private boolean hasAnyPrivilege(FormAccessControlService svc, Form form, String[] anyPriviegeArray) {
		for (String p : anyPriviegeArray) {
			if (svc.hasPrivilege(form, p.trim())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() {
		if (errorOccurred) {
			return SKIP_PAGE;
		} else {
			return EVAL_PAGE;
		}
	}
	
	public String getPrivilege() {
		return privilege;
	}
	
	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}
	
	public String getAllPrivileges() {
		return allPrivileges;
	}
	
	public void setAllPrivileges(String allPrivileges) {
		this.allPrivileges = allPrivileges;
	}
	
	public String getAnyPrivilege() {
		return anyPrivilege;
	}
	
	public void setAnyPrivilege(String anyPrivilege) {
		this.anyPrivilege = anyPrivilege;
	}
	
	public String getOtherwise() {
		return otherwise;
	}
	
	public void setOtherwise(String otherwise) {
		this.otherwise = otherwise;
	}
	
	public String getRedirect() {
		return redirect;
	}
	
	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}
	
	public Form getForm() {
		return form;
	}
	
	public void setForm(Form form) {
		this.form = form;
	}
	
}
