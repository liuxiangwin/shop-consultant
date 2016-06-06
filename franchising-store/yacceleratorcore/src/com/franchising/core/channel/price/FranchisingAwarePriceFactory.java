/**
 *
 */
package com.franchising.core.channel.price;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.jalo.CatalogAwareEurope1PriceFactory;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.jalo.Europe1PriceFactory;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;


/**
 * This class support customer the EuporeFactory but it powerfully deprecated
 *
 * @author Alan Liu
 *
 */
public class FranchisingAwarePriceFactory extends CatalogAwareEurope1PriceFactory
{
	private final static Logger LOG = Logger.getLogger(FranchisingAwarePriceFactory.class);

	private static final String catalogPattern = "ProductCatalog";

	@Resource
	private CMSSiteService cmsSiteService;

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogService catalogService;

	@SuppressWarnings("deprecation")
	public List<PriceInformation> queryFranchisingPrice(final ProductModel productModel)
	{
		//final de.hybris.platform.core.model.product.ProductModel productModel = modelService.get(product.getPK());
		//Preconditions.checkNotNull(product);
		Preconditions.checkNotNull(productModel);
		List<PriceInformation> priceList = new ArrayList<PriceInformation>();
		try
		{
			priceList = Europe1PriceFactory.getInstance().getProductPriceInformations(
					modelService.<Product> getSource(productModel), Calendar.getInstance().getTime(), false);
		}
		catch (final JaloPriceFactoryException e)
		{
			LOG.error("Not franchising price found by FranchisingAwarePriceFactory");
		}



		//final EnumerationValue productClass = Europe1PriceFactory.getInstance().getPPG(ctx,
		//		modelService.<Product> getSource(productModel));

		//final Collection<PriceRow> priceRowsList = Europe1PriceFactory.getInstance().getProductPriceRowsFast(ctx,
		//		modelService.<Product> getSource(productModel), productClass);
		//for (final PriceRow priceRow : priceRowsList)
		//{
		//final Currency priceRowCurr = priceRow.getCurrency();
		//if (currentCurr.equals(priceRowCurr) && (base == null || !base.equals(priceRowCurr)))
		//{
		//final PriceRowModel priceRowModel = modelService.get(priceRow);
		//}
		//}
		return priceList;
	}

	public static FranchisingAwarePriceFactory getInstance()
	{
		return (FranchisingAwarePriceFactory) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension("yacceleratorcore");
	}

}
