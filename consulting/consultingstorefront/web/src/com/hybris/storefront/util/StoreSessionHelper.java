/**
 *
 */
package com.hybris.storefront.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author Alan Liu
 *
 */
public interface StoreSessionHelper
{
	public String selectLanguage(@RequestParam("code") final String isoCode, final HttpServletRequest request);

	String getReturnRedirectUrlForUrlEncoding(final HttpServletRequest request, final String old, final String current);

	String getReturnRedirectUrlWithoutReferer(final HttpServletRequest request);



}
