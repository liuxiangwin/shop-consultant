/**
 *
 */
package com.hybris.core.services.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.core.constants.ConsultingCoreConstants;
import com.hybris.core.model.ConsultantModel;
import com.hybris.core.services.ConsultantService;


/**
 * @author Alan Liu
 *
 */
public class DefaultConsultantService implements ConsultantService
{
	private FlexibleSearchService flexibleSearchService;

	private SessionService sessionService;

	@Autowired
	protected SearchRestrictionService searchRestrictionService;

	@Autowired
	private CatalogVersionService catalogVersionService;

	@Resource
	private CMSSiteService cmsSiteService;

	public static final Logger LOG = Logger.getLogger(DefaultConsultantService.class);



	@Override
	public Language getDefaultLanguageForCountryIsocode(final String countryIsocode)
	{
		final CountryModel exampleCountryModel = new CountryModel();
		exampleCountryModel.setIsocode(countryIsocode);
		final CountryModel countryModel = flexibleSearchService.getModelByExample(exampleCountryModel);

		final Language langauge = C2LManager.getInstance().getLanguageByIsoCode(
				countryModel.getDefaultLanguageIsocode().toLowerCase());
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


	/*
	 * (non-Javadoc)
	 *
	 * @see com.hybris.core.services.ConsultantService#getExtraInfo(java.lang.String)
	 */
	@Override
	public List<String> getExtraInfo(final String code)
	{
		final ConsultantModel exampleConsultant = new ConsultantModel();
		exampleConsultant.setCode(code);
		List<ConsultantModel> resultList = null;
		try
		{
			//searchRestrictionService.disableSearchRestrictions();
			final String queryUserPk = "SELECT {p:" + ConsultantModel.PK + "} " + "FROM {" + ConsultantModel._TYPECODE + " AS p} "
					+ "WHERE " + "{p:" + ConsultantModel.CODE + "}=?code";

			final CatalogVersionModel catalogVersionModel = cmsSiteService.getCurrentCatalogVersion();
			//final String catalogId = catalogVersionModel.getCatalog().getId(); // e.g. "mycatalog"
			//final String catalogVersion = catalogVersionModel.getVersion(); // e.g. "Staged"
			//catalogVersionService.setSessionCatalogVersion(catalogId, catalogVersion);

			final FlexibleSearchQuery query = new FlexibleSearchQuery(queryUserPk);
			//query.setCatalogVersions(catalogVersionService.getCatalogVersion(catalogId, catalogVersion));
			query.setCatalogVersions(catalogVersionModel);
			query.addQueryParameter("code", code);
			resultList = flexibleSearchService.<ConsultantModel> search(query).getResult();

		}
		catch (final Exception e)
		{
			LOG.error("Exception" + e.getMessage());
		}
		finally
		{
			//searchRestrictionService.enableSearchRestrictions();
		}

		final List<String> list = new ArrayList<String>();

		if (resultList.size() > 0)
		{

			list.add(resultList.get(0).getSurname());
			list.add(resultList.get(0).getForname());
			return list;
		}
		else
		{
			return Collections.emptyList();
		}
	}


	@Override
	public List<ConsultantModel> getTutoContentProduct()
	{
		List<ConsultantModel> foundContentList = null;
		try
		{
			final String queryUserPk = "SELECT {p:" + ConsultantModel.PK + "} " + "FROM {" + ConsultantModel._TYPECODE + " AS p} ";
			//final CatalogVersionModel catalogVersionModel = cmsSiteService.getCurrentCatalogVersion();
			final FlexibleSearchQuery query = new FlexibleSearchQuery(queryUserPk);
			//query.setCatalogVersions(catalogVersionModel);
			foundContentList = flexibleSearchService.<ConsultantModel> search(query).getResult();
		}
		catch (final Exception e)
		{
			LOG.error("Exception" + e.getMessage());
		}
		return foundContentList;
	}


	@Override
	public String getTutoContentProduct(final ConsultantModel consultantModel)
	{
		final ConsultantModel exampleConsultant = new ConsultantModel();
		exampleConsultant.setCode(consultantModel.getCode());
		final ConsultantModel consultant = flexibleSearchService.getModelByExample(exampleConsultant);

		if (consultant != null)
		{
			return consultant.getContent();
		}
		else
		{
			return "";
		}
	}
}
