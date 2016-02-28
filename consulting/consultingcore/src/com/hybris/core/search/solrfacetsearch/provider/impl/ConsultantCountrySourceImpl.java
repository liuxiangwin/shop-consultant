/**
 *
 */
package com.hybris.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Required;

import com.hybris.core.model.ConsultantModel;
import com.hybris.core.search.solrfacetsearch.provider.ConsultantCountrySource;
import com.hybris.core.services.ConsultantService;


/**
 * @author Alan Liu
 *
 */
public class ConsultantCountrySourceImpl implements ConsultantCountrySource
{

	private ConsultantService consultantService;


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


	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.hybris.core.search.solrfacetsearch.provider.ConsultantCountrySource#getConsultantCountiesForConfigAndProperty(
	 * de.hybris.platform.solrfacetsearch.config.IndexConfig, de.hybris.platform.solrfacetsearch.config.IndexedProperty,
	 * java.lang.Object)
	 */
	@Override
	public Collection<CountryModel> getConsultantCountiesForConfigAndProperty(final IndexConfig indexConfig,
			final IndexedProperty indexedProperty, final Object model)
	{
		Collection<CountryModel> countries = null;

		if (model instanceof ConsultantModel)
		{
			final ConsultantModel consultantModel = (ConsultantModel) model;
			countries = this.getCountries(consultantModel);
		}


		if (countries != null && !countries.isEmpty())
		{
			return countries;
		}
		else
		{
			return Collections.emptyList();
		}
	}


	private Collection<CountryModel> getCountries(final ConsultantModel consultantModel)
	{
		//final Collection<CountryModel> countries = getModelService().getAttributeValue(consultantModel, "activecountries");
		final Collection<CountryModel> countries = getConsultantService().getActiveCountries(consultantModel);

		return countries;
	}
}
