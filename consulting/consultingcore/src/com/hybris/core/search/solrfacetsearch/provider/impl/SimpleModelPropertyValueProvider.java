/**
 *
 */
package com.hybris.core.search.solrfacetsearch.provider.impl;

/**
 * @author liuxiangwin
 *
 */
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
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


public class SimpleModelPropertyValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider,
		Serializable
{
	private FieldNameProvider fieldNameProvider;
	private CommonI18NService commonI18NService;

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		if (model == null)
		{
			throw new IllegalArgumentException("No model given");
		}

		try
		{
			final List<FieldValue> fieldValues = new ArrayList<FieldValue>();

			if (!indexedProperty.isLocalized())
			{
				addFieldValues(indexedProperty, model, null, fieldValues);
			}
			else if (indexConfig.getLanguages().isEmpty())
			{
				addFieldValues(indexedProperty, model, commonI18NService.getCurrentLanguage(), fieldValues);
			}
			else
			{
				final LanguageModel sessionLanguage = commonI18NService.getCurrentLanguage();
				try
				{
					for (final LanguageModel language : indexConfig.getLanguages())
					{
						commonI18NService.setCurrentLanguage(language);
						addFieldValues(indexedProperty, model, language, fieldValues);
					}
				}
				finally
				{
					commonI18NService.setCurrentLanguage(sessionLanguage);
				}
			}

			return fieldValues;
		}
		catch (final Exception e)
		{
			LOG.error(e);
			throw new FieldValueProviderException("Cannot evaluate " + indexedProperty.getName() + " using "
					+ this.getClass().getName(), e);
		}
	}

	protected void addFieldValues(final IndexedProperty indexedProperty, final Object model, final LanguageModel language,
			final List<FieldValue> fieldValues) throws FieldValueProviderException
	{
		final Object value = getPropertyValue(indexedProperty, model, language);
		if (value != null)
		{
			final String languageIsocode = language == null ? null : language.getIsocode();
			final String rangeName = getRangeName(indexedProperty, value, languageIsocode);
			final Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, languageIsocode);

			for (final String fieldName : fieldNames)
			{
				fieldValues.add(new FieldValue(fieldName, rangeName == null ? value : rangeName));
			}
		}
	}

	@SuppressWarnings("unused")
	protected Object getPropertyValue(final IndexedProperty indexedProperty, final Object model, final LanguageModel language)
	{
		String qualifier = indexedProperty.getValueProviderParameter();
		if ((qualifier == null) || qualifier.trim().isEmpty())
		{
			qualifier = indexedProperty.getName();
		}

		return modelService.getAttributeValue(model, qualifier);
	}

	@Required
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}
}



class BasicModelPropertyValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider, Serializable
{
	private FieldNameProvider fieldNameProvider;

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		if (model == null)
		{
			throw new IllegalArgumentException("No model given");
		}

		try
		{
			final List<FieldValue> returnValues = new ArrayList<FieldValue>();
			addFieldValues(indexedProperty, model, returnValues);
			return returnValues;
		}
		catch (final Exception e)
		{
			LOG.error(e);
			throw new FieldValueProviderException("Cannot evaluate " + indexedProperty.getName() + " using "
					+ this.getClass().getName(), e);
		}
	}

	protected void addFieldValues(final IndexedProperty indexedProperty, final Object model, final List<FieldValue> returnValues)
			throws FieldValueProviderException
	{
		final Object value = getPropertyValue(indexedProperty, model);
		final String rangeName = getRangeName(indexedProperty, value);
		final Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, null);

		for (final String fieldName : fieldNames)
		{
			returnValues.add(new FieldValue(fieldName, rangeName == null ? value : rangeName));
		}
	}

	protected Object getPropertyValue(final IndexedProperty indexedProperty, final Object model)
	{
		String qualifier = indexedProperty.getValueProviderParameter();
		if (qualifier == null || qualifier.trim().isEmpty())
		{
			qualifier = indexedProperty.getName();
		}

		return modelService.getAttributeValue(model, qualifier);
	}

	@Required
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}
}