/**
 *
 */
package com.hybris.core.services.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.core.constants.ConsultingCoreConstants;
import com.hybris.core.model.ConsultantModel;
import com.hybris.core.services.ConsultantService;


/**
 * @author Steve Barnacle
 *
 */
public class DefaultConsultantService implements ConsultantService
{
	private FlexibleSearchService flexibleSearchService;

	private SessionService sessionService;


	public static final Logger LOG = Logger.getLogger(DefaultConsultantService.class);



	@Override
	public Language getDefaultLanguageForCountryIsocode(final String countryIsocode)
	{
		final CountryModel exampleCountryModel = new CountryModel();
		exampleCountryModel.setIsocode(countryIsocode);
		final CountryModel countryModel = flexibleSearchService.getModelByExample(exampleCountryModel);

		final Language langauge = C2LManager.getInstance()
				.getLanguageByIsoCode(countryModel.getDefaultLanguageIsocode().toLowerCase());
		return langauge;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.hybris.core.services.ConsultantService#getActiveCountries(com.hybris.core.model.ConsultantModel)
	 */
	@Override
	public Collection<CountryModel> getActiveCountries(final ConsultantModel consultantModel)
	{
		final ConsultantModel exampleConsultant = new ConsultantModel();
		exampleConsultant.setCode(consultantModel.getCode());
		final ConsultantModel consultant = flexibleSearchService.getModelByExample(exampleConsultant);

		if (consultant != null)
		{
			return consultant.getActiveCountries();
		}
		else
		{
			return Collections.emptyList();
		}
	}



	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.hybris.core.services.ConsultantService#getCountrySelectedForSession(de.hybris.platform.servicelayer.session.
	 * SessionService)
	 */
	@Override
	public String getCountrySelectedForSession(final boolean returnSafeDefault)
	{
		String countryInSession = "";
		if (sessionService != null)
		{
			countryInSession = sessionService.getAttribute(ConsultingCoreConstants.SELECTED_CONSULTANT_COUNTRY);
		}


		if (countryInSession != null && (countryInSession.isEmpty() & returnSafeDefault))
		{
			LOG.error("Unable to determine consultant country selected in session - default value applied of : "
					+ ConsultingCoreConstants.DEFAULT_CONSULTANT_COUNTRY);
			return ConsultingCoreConstants.DEFAULT_CONSULTANT_COUNTRY;
		}
		else
		{
			return countryInSession;
		}
	}




	/*
	 * (non-Javadoc)
	 *
	 * @see com.hybris.core.services.ConsultantService#setCountryCodeSelectedForSession(de.hybris.platform.servicelayer.
	 * session.SessionService, java.lang.String)
	 */
	@Override
	public void setCountryCodeSelectedForSession(final String countryCode)
	{
		if (getSessionService() != null)
		{
			getSessionService().setAttribute(ConsultingCoreConstants.SELECTED_CONSULTANT_COUNTRY, countryCode);
		}
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.hybris.core.services.ConsultantService#setCountryPkSelectionForSession(de.hybris.platform.servicelayer.session
	 * .SessionService, java.lang.String)
	 */
	@Override
	public void setCountryPkSelectionForSession(final String countryCode)
	{
		if (getSessionService() != null)
		{
			final CountryModel exampleCountryModel = new CountryModel();
			exampleCountryModel.setIsocode(countryCode);

			// Counties aren't catalog aware - so only expecting single entity at most
			final CountryModel countryModel = flexibleSearchService.getModelByExample(exampleCountryModel);


			final PK pk = countryModel.getPk();

			getSessionService().setAttribute(ConsultingCoreConstants.SELECTED_CONSULTANT_COUNTRY_PK, pk.getLongValue());
		}
	}


	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}




	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}


	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

}
