/**
 *
 */
package com.hybris.core.services.products.impl;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.GenericSearchConstants.LOG;
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
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.hybris.core.constants.ConsultingCoreConstants;
import com.hybris.core.model.ConsultantModel;
import com.hybris.core.services.products.ConsultantService;


/**
 * @author Alan Liu
 *
 */
public class DefaultConsultantService implements ConsultantService
{
	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private SessionService sessionService;

	@Resource
	protected SearchRestrictionService searchRestrictionService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CatalogService catalogService;

	@Resource
	private CMSSiteService cmsSiteService;

	public static final Logger LOG = Logger.getLogger(DefaultConsultantService.class);

	/**
	 * getDefaultLanguageForCountryIsocode
	 * @param countryIsocode
	 * @return
	 */
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

	@Override
	/**
	 * @param consultantModel
	 * @return Collection<CountryModel>
	 */
	public Collection<CountryModel> getActiveCountries(final ConsultantModel consultantModel)
	{

		List<ConsultantModel> resultList = null;
		try
		{
			final String queryUserPk = "SELECT {p.pk} FROM {Consultant AS p JOIN CatalogVersion AS cv on {p.catalogVersion}={cv.pk}"
					+ " JOIN Catalog AS c on {cv.catalog} = {c.pk}} WHERE {c.id} =?cId" + " AND {cv.version} =?cvVersion"
					+ " AND  {p:code}=?code";

			final CatalogVersionModel catalogVersionModel = this.getCurrentCatalog();
			final FlexibleSearchQuery query = new FlexibleSearchQuery(queryUserPk);
			query.addQueryParameter("cId", catalogVersionModel.getCatalog().getId());
			query.addQueryParameter("cvVersion", catalogVersionModel.getCatalog().getVersion());
			query.addQueryParameter("code", consultantModel.getCode());
			resultList = flexibleSearchService.<ConsultantModel> search(query).getResult();
		}
		catch (final Exception e)
		{
			LOG.error("Exception" + e.getMessage());
		}
		if (resultList.size() > 0)
		{
			final ConsultantModel consultant = resultList.get(0);
			if (consultant != null)
			{
				return consultant.getActiveCountries();
			}
		}
		return Collections.emptyList();

	}
  /**
   * getCurrentCatalog
   * @return CatalogVersionModel
   */
	private CatalogVersionModel getCurrentCatalog()
	{
		final Set<CatalogVersionModel> cls = catalogService.getSessionCatalogVersions();
		if (cls.size() > 0)
		{
			for (final CatalogVersionModel clm : cls)
			{
				final String catalogName = clm.getCatalog().getId();
				if (catalogName.contains("ProductCatalog"))
				{
					return clm;
				}
			}
		}
		return null;
	}


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

	@Override
	public void setCountryCodeSelectedForSession(final String countryCode)
	{
		if (sessionService != null)
		{
			sessionService.setAttribute(ConsultingCoreConstants.SELECTED_CONSULTANT_COUNTRY, countryCode);
		}
	}

	@Override
	public void setCountryPkSelectionForSession(final String countryCode)
	{
		if (sessionService != null)
		{
			final CountryModel exampleCountryModel = new CountryModel();
			exampleCountryModel.setIsocode(countryCode);
			final CountryModel countryModel = flexibleSearchService.getModelByExample(exampleCountryModel);
			final PK pk = countryModel.getPk();
			sessionService.setAttribute(ConsultingCoreConstants.SELECTED_CONSULTANT_COUNTRY_PK, pk.getLongValue());
		}
	}

	
	/**
	 * getExtraInfo(final String code)
	 * @param  String code
	 * @return List<String>
	 */
	@Override
	public List<String> getExtraInfo(final String code)
	{
		final ConsultantModel exampleConsultant = new ConsultantModel();
		exampleConsultant.setCode(code);
		List<ConsultantModel> resultList = null;
		try
		{
			final String queryUserPk = "SELECT {p:" + ConsultantModel.PK + "} " + "FROM {" + ConsultantModel._TYPECODE + " AS p} "
					+ "WHERE " + "{p:" + ConsultantModel.CODE + "}=?code";

			final CatalogVersionModel catalogVersionModel = cmsSiteService.getCurrentCatalogVersion();

			final FlexibleSearchQuery query = new FlexibleSearchQuery(queryUserPk);
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

   
	/**
	 * getTutoContentProduct
	 * @param consultantModel
	 * @return String
	 */
	@Override
	public List<ConsultantModel> getTutoContentProduct()
	{
		List<ConsultantModel> foundContentList = null;
		try
		{
			final String queryUserPk = "SELECT {p:" + ConsultantModel.PK + "} " + "FROM {" + ConsultantModel._TYPECODE + " AS p} ";
			final FlexibleSearchQuery query = new FlexibleSearchQuery(queryUserPk);
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
