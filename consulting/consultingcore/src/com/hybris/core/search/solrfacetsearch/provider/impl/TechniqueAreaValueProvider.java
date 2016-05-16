/**
 *
 */
package com.hybris.core.search.solrfacetsearch.provider.impl;

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
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Required;

import com.hybris.core.services.ConsultantService;


/**
 * @author Alan Liu
 *
 */
public class TechniqueAreaValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider, Serializable
{
	private FieldNameProvider fieldNameProvider;


	private ConsultantService consultantService;

	@Resource
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
			//final String content = consultantService.getTutoContentProduct((ConsultantModel) model);

			final Object value = getPropertyValue(model);
			final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
			if (value != null)
			{
				final String content = value.toString();


				if (indexedProperty.isLocalized())
				{
					final Collection<LanguageModel> languages = indexConfig.getLanguages();
					for (final LanguageModel language : languages)
					{
						fieldValues.addAll(createFieldValue(content, language, indexedProperty));
					}
				}
				else
				{
					fieldValues.addAll(createFieldValue(content, null, indexedProperty));
				}

			}
			/*
			 * else { throw new FieldValueProviderException("Content value is null " + indexedProperty.getName() +
			 * " using " + this.getClass().getName()); }
			 */
			return fieldValues;
		}
		catch (final Exception e)
		{
			LOG.error(e);
			throw new FieldValueProviderException("Cannot evaluate " + indexedProperty.getName() + " using "
					+ this.getClass().getName(), e);
		}
	}

	protected List<FieldValue> createFieldValue(final String value, final LanguageModel language,
			final IndexedProperty indexedProperty)
	{
		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();

		if (language != null)
		{
			final Locale locale = i18nService.getCurrentLocale();
			try
			{
				i18nService.setCurrentLocale(commonI18NService.getLocaleForLanguage(language));
			}
			finally
			{
				i18nService.setCurrentLocale(locale);
			}

			final Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, language.getIsocode());
			for (final String fieldName : fieldNames)
			{
				fieldValues.add(new FieldValue(fieldName, value));
			}
		}
		else
		{
			final Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, null);
			for (final String fieldName : fieldNames)
			{
				fieldValues.add(new FieldValue(fieldName, value));
			}
		}
		return fieldValues;
	}

	protected Object getPropertyValue(final Object model)
	{
		return getPropertyValue(model, "content");
	}

	protected Object getPropertyValue(final Object model, final String propertyName)
	{
		return modelService.getAttributeValue(model, propertyName);
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
