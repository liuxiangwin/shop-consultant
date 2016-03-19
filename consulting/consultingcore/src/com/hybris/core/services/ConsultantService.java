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
	public Collection<CountryModel> getActiveCountries(ConsultantModel consultantModel);


	/**
	 * @param sessionService
	 * @return String
	 */
	public String getCountrySelectedForSession(boolean returnSafeDefault);


	/**
	 * @param sessionService
	 * @param countryCode
	 */
	public void setCountryCodeSelectedForSession(String countryCode);


	/**
	 * @param sessionService
	 * @param countryCode
	 */
	public void setCountryPkSelectionForSession(String countryCode);


	public Language getDefaultLanguageForCountryIsocode(String countryIsocode);


	public List<String> getExtraInfo(String code);

	public List<ConsultantModel> getTutoContentProduct();

	public String getTutoContentProduct(ConsultantModel consultantModel);
}
