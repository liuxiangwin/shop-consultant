/**
 *
 */
package com.hybris.storefront.util.impl;

/**
 * @author Alan Liu
 *
 */


import de.hybris.platform.acceleratorservices.urlencoder.UrlEncoderService;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.user.UserFacade;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import com.hybris.storefront.filters.StorefrontFilter;
import com.hybris.storefront.util.StoreSessionHelper;


/**
 * @author Alan Liu
 *
 */
public class DefaultStoreSessionHelper implements StoreSessionHelper
{
	@Resource(name = "urlEncoderService")
	private UrlEncoderService urlEncoderService;

	@Resource(name = "storeSessionFacade")
	private StoreSessionFacade storeSessionFacade;

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	private static final String REDIRECT_PREFIX = "redirect:";



	@Override
	public String selectLanguage(@RequestParam("code") final String isoCode, final HttpServletRequest request)
	{
		final String previousLanguage = storeSessionFacade.getCurrentLanguage().getIsocode();
		storeSessionFacade.setCurrentLanguage(isoCode);
		if (!userFacade.isAnonymousUser())
		{
			userFacade.syncSessionLanguage();
		}
		return (urlEncoderService.isLanguageEncodingEnabled())
				? getReturnRedirectUrlForUrlEncoding(request, previousLanguage, storeSessionFacade.getCurrentLanguage().getIsocode())
				: getReturnRedirectUrlWithoutReferer(request);

	}

	@Override
	public String getReturnRedirectUrlForUrlEncoding(final HttpServletRequest request, final String old, final String current)
	{
		final String originalReferer = (String) request.getSession().getAttribute(StorefrontFilter.ORIGINAL_REFERER);
		if (StringUtils.isNotBlank(originalReferer))
		{
			return REDIRECT_PREFIX + StringUtils.replace(originalReferer, "/" + old + "/", "/" + current + "/");
		}

		String referer = StringUtils.remove(request.getRequestURL().toString(), request.getServletPath());
		if (!StringUtils.endsWith(referer, "/"))
		{
			referer = referer + "/";
		}
		if (referer != null && !referer.isEmpty() && StringUtils.contains(referer, "/" + old + "/"))
		{
			return REDIRECT_PREFIX + StringUtils.replace(referer, "/" + old + "/", "/" + current + "/");
		}
		return REDIRECT_PREFIX + referer;
	}



	@Override
	public String getReturnRedirectUrlWithoutReferer(final HttpServletRequest request)
	{
		final String originalReferer = (String) request.getSession().getAttribute(StorefrontFilter.ORIGINAL_REFERER);
		if (StringUtils.isNotBlank(originalReferer))
		{
			return REDIRECT_PREFIX + originalReferer;
		}

		final String referer = StringUtils.remove(request.getRequestURL().toString(), request.getServletPath());
		if (referer != null && !referer.isEmpty())
		{
			return REDIRECT_PREFIX + referer;
		}
		return REDIRECT_PREFIX + '/';
	}

}
