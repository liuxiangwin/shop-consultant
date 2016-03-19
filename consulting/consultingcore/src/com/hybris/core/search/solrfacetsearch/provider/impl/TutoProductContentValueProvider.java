/**
 *
 */
package com.hybris.core.search.solrfacetsearch.provider.impl;


import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.hybris.core.model.ConsultantModel;
import com.hybris.core.services.ConsultantService;


/**
 * @author Alan Liu
 *
 */
public class TutoProductContentValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider,
		Serializable
{
	private FieldNameProvider fieldNameProvider;


	private ConsultantService consultantService;

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();
		final List<ConsultantModel> foundContentList = consultantService.getTutoContentProduct();
		for (final ConsultantModel consultantModel : foundContentList)
		{
			if (consultantModel != null)
			{

				final Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, null);
				for (final String fieldName : fieldNames)
				{
					//fieldValues.add(new FieldValue("contentValue", consultantModel.getContent()));
					fieldValues.add(new FieldValue(fieldName, consultantModel.getContent()));
				}




			}
		}

		return fieldValues;
	}

	protected FieldNameProvider getFieldNameProvider()
	{
		return fieldNameProvider;
	}

	@Required
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
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
	public void setConsultantService(final ConsultantService consultantService)
	{
		this.consultantService = consultantService;
	}
}
