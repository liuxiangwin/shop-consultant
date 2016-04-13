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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.hybris.storefront.interceptors.BeforeViewHandler;



public class CountrySelectorBeforeViewHandler implements BeforeViewHandler
{

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;


	public static final String REDIRECT_PREFIX = "redirect:";

	@Override
	public void beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelAndView modelAndView)
			throws Exception
	{

		//


	}
}
