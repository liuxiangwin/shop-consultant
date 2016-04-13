package com.hybris.storefront.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by d065429 on 08.03.16.
 */
public class CookieUtils
{

	/**
	 * Return a cookie given a particular key
	 *
	 * @param httpServletRequest
	 *           Request
	 * @param cookieKey
	 *           Cookie key
	 * @return <code>Cookie</code> of the requested key or <code>null</code> if no cookie under that name is found
	 */
	public static Cookie getCookie(final HttpServletRequest httpServletRequest, final String cookieKey)
	{
		final Cookie[] cookies = httpServletRequest.getCookies();
		if (cookies == null)
		{
			return null;
		}

		for (int i = 0; i < cookies.length; i++)
		{
			final Cookie cookie = cookies[i];
			if (cookie.getName().equals(cookieKey))
			{
				return cookie;
			}
		}

		return null;
	}

	/**
	 * Add a cookie with a key and value to the response
	 *
	 * @param httpServletResponse
	 *           Response
	 * @param cookieKey
	 *           Cookie key
	 * @param cookieValue
	 *           Cookie value
	 */
	public static void addCookie(final HttpServletResponse httpServletResponse, final int cookieExpiration,
			final String cookieKey, final String cookieValue)
	{
		final Cookie cookie = new Cookie(cookieKey, cookieValue);
		cookie.setMaxAge(cookieExpiration);
		httpServletResponse.addCookie(cookie);
	}
}
