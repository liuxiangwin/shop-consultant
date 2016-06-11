/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.hybris.storefront.countryselector.web.interceptors.beforeview;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.method.HandlerMethod;

import com.hybris.storefront.interceptors.BeforeControllerHandler;



public class CountrySelectorBeforeViewHandler implements BeforeControllerHandler
{

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	private RedirectStrategy redirectStrategy;

	public static final String REDIRECT_PREFIX = "redirect:";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hybris.storefront.interceptors.BeforeControllerHandler#beforeController(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, org.springframework.web.method.HandlerMethod)
	 */
	@Override
	public boolean beforeController(final HttpServletRequest request, final HttpServletResponse response,
			final HandlerMethod handler) throws Exception
	{

		final Cookie[] cookies = request.getCookies();

		Cookie cook;
		String selectCountry = "";
		if (cookies != null)
		{
			for (int i = 0; i < cookies.length; i++)
			{
				cook = cookies[i];
				if (cook.getName().equalsIgnoreCase("country-selected"))
				{
					selectCountry = cook.getValue();
				}

			}
		}
		if (selectCountry.equalsIgnoreCase(""))
		{
			response.sendRedirect("/consultingstorefront/main");
			return false;
		}
		return true;
	}


	protected RedirectStrategy getRedirectStrategy()
	{
		return redirectStrategy;
	}

	@Required
	public void setRedirectStrategy(final RedirectStrategy redirectStrategy)
	{
		this.redirectStrategy = redirectStrategy;
	}
}
