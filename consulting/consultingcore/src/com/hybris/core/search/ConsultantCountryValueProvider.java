/**
 *
 */
package com.hybris.core.search;



import de.hybris.platform.core.model.c2l.CountryModel;
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
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;

import com.hybris.core.search.solrfacetsearch.provider.ConsultantCountrySource;


/**
 * Consutant country value provider. Value provider that generates field values for country codes. This implementation
 * uses a {@link ConsultantCountrySource} to provide the list of countries.
 */

public class ConsultantCountryValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider, Serializable
{
	private ConsultantCountrySource consultantCountrySource;
	private FieldNameProvider fieldNameProvider;
	private CommonI18NService commonI18NService;


	protected ConsultantCountrySource getConsultantCountrySource()
	{
		return consultantCountrySource;
	}

	@Required
	public void setConsultantCountrySource(final ConsultantCountrySource consultantCountrySource)
	{
		this.consultantCountrySource = consultantCountrySource;
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

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}



	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{

		// TODO - get consultants here
		final Collection<CountryModel> countries = getConsultantCountrySource()
				.getConsultantCountiesForConfigAndProperty(indexConfig, indexedProperty, model);
		if (countries != null && !countries.isEmpty())
		{
			final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();

			if (indexedProperty.isLocalized())
			{
				final Collection<LanguageModel> languages = indexConfig.getLanguages();
				for (final LanguageModel language : languages)
				{
					for (final CountryModel country : countries)
					{
						fieldValues.addAll(createFieldValue(country, language, indexedProperty));
					}
				}
			}
			else
			{
				for (final CountryModel country : countries)
				{
					fieldValues.addAll(createFieldValue(country, null, indexedProperty));
				}
			}
			return fieldValues;
		}
		else
		{
			return Collections.emptyList();
		}
	}

	protected List<FieldValue> createFieldValue(final CountryModel country, final LanguageModel language,
			final IndexedProperty indexedProperty)
	{
		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();

		if (language != null)
		{
			final Locale locale = i18nService.getCurrentLocale();
			Object value = null;
			try
			{
				i18nService.setCurrentLocale(getCommonI18NService().getLocaleForLanguage(language));
				value = getPropertyValue(country);
			}
			finally
			{
				i18nService.setCurrentLocale(locale);
			}

			final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty, language.getIsocode());
			for (final String fieldName : fieldNames)
			{
				fieldValues.add(new FieldValue(fieldName, value));
			}
		}
		else
		{
			final Object value = getPropertyValue(country);
			final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty, null);
			for (final String fieldName : fieldNames)
			{
				fieldValues.add(new FieldValue(fieldName, value));
			}
		}

		return fieldValues;
	}

	protected Object getPropertyValue(final Object model)
	{
		return getPropertyValue(model, "isocode");
	}

	protected Object getPropertyValue(final Object model, final String propertyName)
	{
		return modelService.getAttributeValue(model, propertyName);
	}
}
