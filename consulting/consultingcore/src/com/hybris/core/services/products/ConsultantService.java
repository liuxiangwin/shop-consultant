/**
 *
 */
package com.hybris.core.services.products;

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
	 * ConsultantService
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

	/**
	 * getDefaultLanguageForCountryIsocode
	 * @param countryIsocode
	 * @return
	 */
	public Language getDefaultLanguageForCountryIsocode(String countryIsocode);

	/**
	 * 
	 * @param code
	 * @return List<String>
	 */
	public List<String> getExtraInfo(String code);

	/**
	 * getTutoContentProduct
	 * 
	 * @return List<ConsultantModel>
	 */
	public List<ConsultantModel> getTutoContentProduct();

	/**
	 * getTutoContentProduct
	 * 
	 * @param consultantModel
	 * @return String
	 */
	public String getTutoContentProduct(ConsultantModel consultantModel);
}
