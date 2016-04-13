/**
 *
 */
package com.hybris.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
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

import com.hybris.core.channel.strategies.ConsultantPriceService;


/**
 * @author Alan Liu
 *
 */
public class ConsultantProductPriceValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider,
		Serializable
{
	private FieldNameProvider fieldNameProvider;
	private ConsultantPriceService priceService;

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();
		try
		{
			List<String> rangeNameList = null;
			ProductModel product = null;
			if (model instanceof ProductModel)
			{
				product = (ProductModel) model;
			}
			else
			{
				throw new FieldValueProviderException("Cannot evaluate price of non-product item");
			}
			if (indexConfig.getCurrencies().isEmpty())
			{
				final List<PriceInformation> prices = priceService.getPriceInformationsForProduct(product);
				if (prices != null && !prices.isEmpty())
				{
					final PriceInformation price = prices.get(0);
					final Double value = Double.valueOf(price.getPriceValue().getValue());
					rangeNameList = getRangeNameList(indexedProperty, value);
					final Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, price.getPriceValue()
							.getCurrencyIso());
					for (final String fieldName : fieldNames)
					{
						if (rangeNameList.isEmpty())
						{
							fieldValues.add(new FieldValue(fieldName, value));
						}
						else
						{
							for (final String rangeName : rangeNameList)
							{
								fieldValues.add(new FieldValue(fieldName, rangeName == null ? value : rangeName));
							}
						}
					}
				}
			}
			else
			{
				for (final CurrencyModel currency : indexConfig.getCurrencies())
				{
					final CurrencyModel sessionCurrency = i18nService.getCurrentCurrency();
					try
					{
						i18nService.setCurrentCurrency(currency);
						final List<PriceInformation> prices = priceService.getPriceInformationsForProduct(product);
						if (prices != null && !prices.isEmpty())
						{
							final Double value = Double.valueOf(prices.get(0).getPriceValue().getValue());
							rangeNameList = getRangeNameList(indexedProperty, value, currency.getIsocode());
							final Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, currency.getIsocode()
									.toLowerCase());
							for (final String fieldName : fieldNames)
							{
								if (rangeNameList.isEmpty())
								{
									fieldValues.add(new FieldValue(fieldName, value));
								}
								else
								{
									for (final String rangeName : rangeNameList)
									{
										fieldValues.add(new FieldValue(fieldName, rangeName == null ? value : rangeName));
									}
								}
							}
						}
					}
					finally
					{
						i18nService.setCurrentCurrency(sessionCurrency);
					}
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error(e);
			throw new FieldValueProviderException("Cannot evaluate " + indexedProperty.getName() + " using "
					+ this.getClass().getName(), e);
		}
		return fieldValues;
	}


	/**
	 * @param fieldNameProvider
	 *           the fieldNameProvider to set
	 */
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}

	/**
	 * @param priceService
	 *           the priceService to set
	 */
	public void setPriceService(final ConsultantPriceService priceService)
	{
		this.priceService = priceService;
	}



}
