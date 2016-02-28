/**
 *
 */
package com.hybris.facades.product;

import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.commercefacades.order.data.ConsultantData;

import java.util.List;


/**
 * @author Alan Liu
 *
 */
public interface ConsultantFacade
{
	/**
	 * @param sessionService
	 * @return String
	 */
	String getCountrySelectedForSession(boolean returnSafeDefault);


	/**
	 * @param sessionService
	 * @param countryCode
	 */
	void setCountryCodeSelectedForSession(String countryCode);


	/**
	 * @param sessionService
	 * @param countryCode
	 */
	void setCountryPkSelectionForSession(String countryCode);


	/**
	 * @param countryIsocode
	 * @return Language
	 */
	Language getDefaultLanguageForCountryIsocode(String countryIsocode);


	List<ConsultantData> getExtraInfo(String code);

}
