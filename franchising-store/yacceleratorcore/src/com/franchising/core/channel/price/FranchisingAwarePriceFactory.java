/**
 *
 */
package com.franchising.core.channel.price;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.jalo.CatalogAwareEurope1PriceFactory;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.europe1.jalo.Europe1PriceFactory;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
	public Collection<PriceRow> queryPrice(final SessionContext ctx, @SuppressWarnings("deprecation") final Product product,
			@SuppressWarnings("deprecation") final EnumerationValue productGroup, final User user, final EnumerationValue userGroup)
	{
		final de.hybris.platform.core.model.product.ProductModel productModel = modelService.get(product.getPK());

		Preconditions.checkNotNull(product);
		Preconditions.checkNotNull(productModel);
		final boolean isDomesticPrice = false;
		final boolean isInternationPrice = false;

		try
		{
			final List<PriceInformation> priceList = Europe1PriceFactory.getInstance().getProductPriceInformations(
					modelService.<Product> getSource(productModel), Calendar.getInstance().getTime(), false);
		}
		catch (final JaloPriceFactoryException e)
		{
			e.printStackTrace();
		}



		final EnumerationValue productClass = Europe1PriceFactory.getInstance().getPPG(ctx,
				modelService.<Product> getSource(productModel));

		final Collection<PriceRow> priceRowsList = Europe1PriceFactory.getInstance().getProductPriceRowsFast(ctx,
				modelService.<Product> getSource(productModel), productClass);

		final List<PriceRow> domesticList = new ArrayList<PriceRow>();
		final List<PriceRow> internationList = new ArrayList<PriceRow>();

		for (final PriceRow priceRow : priceRowsList)
		{
			//final Currency priceRowCurr = priceRow.getCurrency();
			//if (currentCurr.equals(priceRowCurr) && (base == null || !base.equals(priceRowCurr)))
			//{
			final PriceRowModel priceRowModel = modelService.get(priceRow);
			//}
		}
		if (isDomesticPrice)
		{
			return domesticList;
		}
		else if (isInternationPrice)
		{
			return internationList;
		}
		else
		{
			LOG.error("Not match price row list found");
		}
		return null;
	}

	public static FranchisingAwarePriceFactory getInstance()
	{
		return (FranchisingAwarePriceFactory) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension("yacceleratorcore");
	}

}
