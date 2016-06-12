/**
 *
 */
package com.hybris.core.search.solrfacetsearch.provider;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;

import java.util.Collection;


/**
 * ConsultantCountrySource. Retrieves a collection of counties to index for a specific model & index configuration.
 */
public interface ConsultantCountrySource
{
	/**
	 * Returns a collection of {@link CountyModel} of a given indexedProperty that are fetched from the model based on
	 * the indexConfig.
	 *
	 * @param indexConfig
	 *           index config
	 * @param indexedProperty
	 *           indexed property
	 * @param model
	 *           model
	 * @return Collection of countries
	 */
	Collection<CountryModel> getConsultantCountiesForConfigAndProperty(IndexConfig indexConfig, IndexedProperty indexedProperty,
			Object model);
}

