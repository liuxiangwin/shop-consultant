/**
 *
 */
package com.hybris.core.services;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.jalo.c2l.Language;

import java.util.Collection;
import java.util.List;

import com.hybris.core.model.ConsultantModel;


/**
 * @author Alan Liu
 *
 */
public interface ConsultantService
{
	/**
	 * @param consultantModel
	 * @return Collection<CountryModel>
	 */
	Collection<CountryModel> getActiveCountries(ConsultantModel consultantModel);


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


	Language getDefaultLanguageForCountryIsocode(String countryIsocode);


	List<String> getExtraInfo(String code);
}
