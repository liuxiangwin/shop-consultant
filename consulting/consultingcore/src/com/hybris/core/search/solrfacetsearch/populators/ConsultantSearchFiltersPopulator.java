/**
 *
 */
package com.hybris.core.search.solrfacetsearch.populators;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchFiltersPopulator;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchQuery.Operator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.core.services.ConsultantService;


/**
 * @author Alan Liu
 *
 */



public class ConsultantSearchFiltersPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_SORT_TYPE> extends
		SearchFiltersPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_SORT_TYPE>
{

	private ConsultantService consultantService;
	private SessionService sessionService;

	private final Logger LOG = Logger.getLogger(ConsultantSearchFiltersPopulator.class);


	@Override
	public void populate(
			final SearchQueryPageableData<SolrSearchQueryData> source,
			final SolrSearchRequest<FACET_SEARCH_CONFIG_TYPE, IndexedType, IndexedProperty, SearchQuery, INDEXED_TYPE_SORT_TYPE> target)
	{
		super.populate(source, target);


		/* Uncomment 3 lines below for pre-filtering option */
		final String selectedValue = getConsultantService().getCountrySelectedForSession(true);
		LOG.info("Filtering search filter for active country : " + selectedValue);
		target.getSearchQuery().searchInField("activeCountries", selectedValue, Operator.OR);

	}

	/**
	 * @return the consultantService
	 */
	public ConsultantService getConsultantService()
	{
		return consultantService;
	}

	/**
	 * @param consultantService
	 *           the consultantService to set
	 */
	@Required
	public void setConsultantService(final ConsultantService consultantService)
	{
		this.consultantService = consultantService;
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
