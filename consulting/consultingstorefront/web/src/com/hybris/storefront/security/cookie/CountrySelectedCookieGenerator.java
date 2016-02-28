/**
 *
 */
package com.hybris.storefront.security.cookie;

import de.hybris.platform.site.BaseSiteService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * @author Alan Liu
 *
 */
public class CountrySelectedCookieGenerator extends EnhancedCookieGenerator
{

	private final static String COUNTRY_SELECTED_COOKIENAME_SUFFIX = "country-selected";
	private BaseSiteService baseSiteService;

	@Override
	public String getCookieName()
	{
		return StringUtils.deleteWhitespace(getBaseSiteService().getCurrentBaseSite().getUid())
				+ COUNTRY_SELECTED_COOKIENAME_SUFFIX;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}
}
