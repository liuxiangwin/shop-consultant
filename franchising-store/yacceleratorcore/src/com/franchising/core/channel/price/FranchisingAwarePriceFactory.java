/**
 *
 */
package com.franchising.core.channel.price;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.jalo.CatalogAwareEurope1PriceFactory;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.constants.Europe1Tools;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

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

	private static FranchisingAwarePriceFactory instance;

	public boolean franchisingChannel;

	public PriceData priceData;


	@SuppressWarnings("deprecation")
	public PriceData queryFranchisingPrice(final ProductModel productModel, final PointOfServiceModel pointOfServiceModel)
	{
		Preconditions.checkNotNull(productModel);
		PriceInformation priceInformation = null;
		try
		{
			final SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();

			priceInformation = getPriceInformations(ctx, modelService.<Product> getSource(productModel),
					getPPG(ctx, modelService.<Product> getSource(productModel)), ctx.getUser(), getUPG(ctx, ctx.getUser()),
					ctx.getCurrency(), false, Calendar.getInstance().getTime(), null, pointOfServiceModel);

		}
		catch (final JaloPriceFactoryException e)
		{
			LOG.error("Not franchising price found by FranchisingAwarePriceFactory");
		}
		final PriceData priceData = new PriceData();
		if (priceInformation == null)
		{
			//The franchising has not set yet
			priceData.setCurrencyIso("");
			priceData.setFormattedValue(String.valueOf(""));
		}
		else
		{
			priceData.setCurrencyIso(priceInformation.getPriceValue().getCurrencyIso());
			priceData.setFormattedValue(String.valueOf(priceInformation.getPriceValue().getValue()));
			priceData.setValue(BigDecimal.valueOf((priceInformation.getPriceValue().getValue())));
		}
		return priceData;
	}

	protected PriceInformation getPriceInformations(final SessionContext ctx,
			@SuppressWarnings("deprecation") final Product product,
			@SuppressWarnings("deprecation") final EnumerationValue productGroup, final User user,
			@SuppressWarnings("deprecation") final EnumerationValue userGroup, final Currency curr, final boolean net,
			final Date date, final Collection taxValues, final PointOfServiceModel pointOfServiceModel)
			throws JaloPriceFactoryException
	{
		final Collection<PriceRow> priceRows = matchPriceRowsForInfo(ctx, product, productGroup, user, userGroup, curr, date, net);
		Collection theTaxValues = taxValues;
		PriceInformation pInfo = null;

		for (final PriceRow priceRow : priceRows)
		{

			final PriceRowModel priceRowModel = modelService.get(priceRow);
			final PointOfServiceModel franchising = priceRowModel.getPointsOfService();

			if (franchising != null)
			{
				if (franchising.getName().equals(pointOfServiceModel.getName()))
				{
					pInfo = Europe1Tools.createPriceInformation(priceRow, curr);
					if (pInfo.getPriceValue().isNet() != net)
					{
						// lazy load taxes if prices have to be converted
						if (theTaxValues == null)
						{
							theTaxValues = Europe1Tools.getTaxValues(getTaxInformations(product, getPTG(ctx, product), user,
									getUTG(ctx, user), date));
						}
						// we have to create a new info object since it is immutable
						pInfo = new PriceInformation(pInfo.getQualifiers(), pInfo.getPriceValue().getOtherPrice(theTaxValues));
						//priceInfos.add(pInfo);
					}
				}
			}
		}
		return pInfo;
	}

	/**
	 * @return the franchisingChannel
	 */
	public boolean isFranchisingChannel()
	{
		return franchisingChannel;
	}

	/**
	 * @param franchisingChannel
	 *           the franchisingChannel to set
	 */
	public void setFranchisingChannel(final boolean franchisingChannel)
	{
		this.franchisingChannel = franchisingChannel;
	}


	/**
	 * @return the priceData
	 */
	public PriceData getPriceData()
	{
		return priceData;
	}

	/**
	 * @param priceData
	 *           the priceData to set
	 */
	public void setPriceData(final PriceData priceData)
	{
		this.priceData = priceData;
	}


	public static FranchisingAwarePriceFactory getInstance()

	{
		if (instance == null)
		{
			instance = new FranchisingAwarePriceFactory();
		}
		return instance;

		//return (FranchisingAwarePriceFactory) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
		//		.getExtension("yacceleratorcore");
	}
}
