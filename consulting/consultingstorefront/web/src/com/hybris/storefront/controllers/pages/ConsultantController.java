/**
 *
 */
package com.hybris.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hybris.facades.product.ConsultantFacade;
import com.hybris.storefront.controllers.ControllerConstants;
import com.hybris.storefront.security.cookie.CountrySelectedCookieGenerator;


/**
 * @author Alan Liu
 *
 */
@Controller
@Scope
@RequestMapping("/consultant")
public class ConsultantController extends AbstractPageController
{

	@Resource(name = "consultantFacade")
	private ConsultantFacade consultantFacade;

	@Resource(name = "countrySelectedCookieGenerator")
	private CountrySelectedCookieGenerator countrySelectedCookieGenerator;

	//@Resource(name = "storeSessionHelper")
	//private StoreSessionHelper storeSessionHelper;


	@RequestMapping(method = RequestMethod.GET, value = "/{country}/")
	public String setCountry(@PathVariable String country)
	{
		if (country != null)
		{
			// All these really server for the non classical MC package approach
			// Left in situ but not relevant to the multi-site solution
			FilterCountryMap(country);


			/* Uncomment code below for Single site session solution only */
			// Country code serves the search page
			//this.getConsultantFacade().setCountryCodeSelectedForSession(country);
			// Country PK serves the need for the PDP (and other non-solr pages) - searchRestriction
			//this.getConsultantFacade().setCountryPkSelectionForSession(country);
			//final Language language = consultantFacade.getDefaultLanguageForCountryIsocode(country);

			// Ensure language for country is set
			//JaloSession.getCurrentSession().getSessionContext().setLanguage(language);
		}

		country = country.equalsIgnoreCase("gb") ? "uk" : country;

		final String redirect_url = ControllerConstants.Url.REDIRECT_PREFIX + "https://" + country.toLowerCase()
				+ "-consultingsite.local:9002/consultingstorefront/" + country.toLowerCase()
				+ "-consultingsite/en/search?q=%3Arelevance&show=All";

		LOG.debug("redirecting to " + redirect_url);


		return redirect_url;
	}


	// Ok - its a hack but saves having to rename impex etc
	private String FilterCountryMap(final String country)
	{
		String filteredString = "";
		switch (country.toLowerCase())
		{
			case "uk":
				filteredString = "gb";
				break;
			default:
				filteredString = country;
		}

		return filteredString;

	}

	/**
	 * @return the consultantFacade
	 */
	//@Override
	public ConsultantFacade getConsultantFacade()
	{
		return consultantFacade;
	}

	/**
	 * @param consultantFacade
	 *           the consultantFacade to set
	 */
	//@Override
	public void setConsultantFacade(final ConsultantFacade consultantFacade)
	{
		this.consultantFacade = consultantFacade;
	}

}
