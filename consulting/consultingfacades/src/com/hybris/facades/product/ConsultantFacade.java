/**
 *
 */
package com.hybris.facades.product;

import de.hybris.platform.jalo.c2l.Language;


/**
 * @author I320189
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

}
