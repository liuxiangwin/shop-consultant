/**
 *
 */
package com.hybris.facades.populators;



import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.converters.populator.SearchResultProductPopulator;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * @author Alan Liu
 *
 */
public class TutoSearchResultProductPopulator extends SearchResultProductPopulator
//implements Populator<SearchResultValueData, ConsultantData>
{
	@Override
	public void populate(final SearchResultValueData source, final ProductData target) throws ConversionException
	{
		target.setContent(this.<String> getValue(source, "content"));
	}


	@Override
	protected <T> T getValue(final SearchResultValueData source, final String propertyName)
	{
		if (source.getValues() == null)
		{
			return null;
		}
		return (T) source.getValues().get(propertyName);
	}
}
