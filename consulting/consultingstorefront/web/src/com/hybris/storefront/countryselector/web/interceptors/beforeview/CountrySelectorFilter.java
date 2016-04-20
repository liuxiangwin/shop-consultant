/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
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

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.CookieGenerator;


/**
 * Filter that resolves base site id from the requested url and activates it.
 */
public class CountrySelectorFilter extends OncePerRequestFilter
{

	@Resource(name = "sessionCookieGenerator")
	private CookieGenerator cookieGenerator;

	@Override
	public void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		//https://localhost:9002/consultingstorefront/zh-consultingsite/en/?clear=true&site=zh-consultingsite
		//response.sendRedirect("/consultingstorefront/main");
		/*
		 * final Cookie[] cookies = request.getCookies();
		 *
		 * Cookie cook; String selectCountry = ""; if (cookies != null) { for (int i = 0; i < cookies.length; i++) { cook
		 * = cookies[i]; if (cook.getName().equalsIgnoreCase("country-selected")) { selectCountry = cook.getValue(); }
		 *
		 * } } response.sendRedirect("/consultingstorefront/main");
		 */
		//filterChain.doFilter(request, response);

		/*final Cookie[] cookies = request.getCookies();

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
			//getRedirectStrategy().sendRedirect(request, response, "/consultingstorefront/main");
			response.sendRedirect("/consultingstorefront/main");
			//return REDIRECT_PREFIX + "/consultingstorefront/main";
			//request.getRequestDispatcher("/consultingstorefront/main").forward(request, response);
		}
		else
		{
			filterChain.doFilter(request, response);
		}*/
	}
}
