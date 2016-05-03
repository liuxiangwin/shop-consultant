/**
 *
 */
package com.hybris.core.channel.strategies;

import org.apache.log4j.Logger;

import com.hybris.core.util.SiteUtil;


/**
 * @author Alan Liu
 *
 */
public class ConsultantPriceStrategy
{
	private static final Logger LOG = Logger.getLogger(ConsultantPriceStrategy.class);

	public void determineDomesticOrInternation(boolean isDomesticPrice, boolean isInternationPrice, final String catalogId,
			final de.hybris.platform.core.model.product.ProductModel productModel)
	{

		final String productCountry = productModel.getNationality();
		if (catalogId != null && !catalogId.equals("") && productCountry != null && !productCountry.equals(""))
		{
			if (catalogId.substring(SiteUtil.START_INDEX, SiteUtil.END_INDEX).equalsIgnoreCase(SiteUtil.SITE_UK)
					&& productCountry.equalsIgnoreCase(SiteUtil.UK_REGION))
			{
				isDomesticPrice = true;
			}
			else if (catalogId.substring(SiteUtil.START_INDEX, SiteUtil.END_INDEX).equalsIgnoreCase(SiteUtil.SITE_ZH)
					&& productCountry.equalsIgnoreCase(SiteUtil.ZH_REGION))
			{
				isDomesticPrice = true;
			}
			else
			{
				isInternationPrice = true;
			}
		}
	}
	//CN Or GB
	
	/*final String productCountry = productModel.getNationality();
	if (catalogId != null && !catalogId.equals("") && productCountry != null && !productCountry.equals(""))
	{
		if (catalogId.substring(SiteUtil.START_INDEX, SiteUtil.END_INDEX).equalsIgnoreCase(SiteUtil.SITE_UK)
				&& productCountry.equalsIgnoreCase(SiteUtil.UK_REGION))
		{
			isDomesticPrice = true;
		}
		else if (catalogId.substring(SiteUtil.START_INDEX, SiteUtil.END_INDEX).equalsIgnoreCase(SiteUtil.SITE_ZH)
				&& productCountry.equalsIgnoreCase(SiteUtil.ZH_REGION))
		{
			isDomesticPrice = true;
		}
		else
		{
			isInternationPrice = true;
		}
	}*/
}
