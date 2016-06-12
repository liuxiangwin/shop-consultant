/**
 *
 */
package com.hybris.core.service.channel.price.factory;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.jalo.CatalogAwareEurope1PriceFactory;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.constants.Europe1Tools;
import de.hybris.platform.europe1.jalo.Europe1PriceFactory;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.hybris.core.service.channel.price.strategies.ConsultantChannelPrice;
import com.hybris.core.util.SiteUtil;
import com.hybris.core.util.SortUtil;


/**
 * This class support customer the EuporeFactory but it powerfully deprecated
 *
 * @author Alan Liu
 *
 */
public class ConsultantAwarePriceFactory extends CatalogAwareEurope1PriceFactory
{
	private final static Logger LOG = Logger.getLogger(ConsultantAwarePriceFactory.class);

	private static final String CHANNEL_DOMETIC = "domestic";

	private static final String CHANNEL_INTERNALTIONAL = "international";

	private static final String catalogPattern = "ProductCatalog";

	@Resource
	private CMSSiteService cmsSiteService;

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogService catalogService;

	@SuppressWarnings("deprecation")
	@Override
	/**
	 * Runs index query for fetching price rows for country specific channel prices. 
	 * Please note that this method may return
	 * @param ctx
	 * @param product
	 *           the product to get prices for
	 * @param productGroup
	 *           the product group to get prices for
	 * @param user
	 *           the user to get prices for
	 * @param userGroup
	 *           the user price group to get prices for
	 * @throws JaloPriceFactoryException
	 */
	public Collection<PriceRow> queryPriceRows4Price(final SessionContext ctx,
			@SuppressWarnings("deprecation") final Product product,
			@SuppressWarnings("deprecation") final EnumerationValue productGroup, final User user, final EnumerationValue userGroup)
	{
		final de.hybris.platform.core.model.product.ProductModel productModel = modelService.get(product.getPK());
		List<PriceRow> domesticList = new ArrayList<PriceRow>();
		List<PriceRow> internationList = new ArrayList<PriceRow>();
		String productCountry = productModel.getNationality();
		boolean isDomesticPrice = false;
		boolean isInternationPrice = false;
		ConsultantChannelPrice  ccp = new ConsultantChannelPrice(isDomesticPrice,isInternationPrice);
		ccp.setDomesticList(domesticList);
		ccp.setInternationList(internationList);
		
		String catalogId = "";

		Preconditions.checkNotNull(product);
		Preconditions.checkNotNull(productModel);

		catalogId = matchCatalogByProduct(catalogId, product);

		matchChannelByCatalog(productCountry, catalogId, ccp);

		getChannelPrice(productModel, ctx, ccp);

		if (ccp.isDomesticPrice())
		{
			return domesticList;
		}
		else if (ccp.isInternationPrice())
		{
			return internationList;
		}
		else
		{
			LOG.error("Not match price row list found");
		}
		return null;
	}

	/**
	 * matchCatalog ByProduct
	 * 
	 * @param catalogId
	 * @param product
	 * @return String
	 */
	public String matchCatalogByProduct(String catalogId, final Product product)
	{
		final Set<CatalogVersionModel> cls = catalogService.getSessionCatalogVersions();

		if (cls.size() > 0)
		{
			for (final CatalogVersionModel clm : cls)
			{
				final String catalogName = clm.getCatalog().getId();
				if (catalogName.contains(catalogPattern))
				{
					catalogId = clm.getCatalog().getId();
				}
			}
		}
		else
		{
			final CatalogVersion clv = CatalogManager.getInstance().getCatalogVersion(product);
			catalogId = clv.getCatalog().getId();
		}
		return catalogId;
	}

	/**
	 * Match CatalogById
	 * 
	 * @param catalogId
	 * @return String
	 */
	public String matchCatalogById(String catalogId)
	{
		final Set<CatalogVersionModel> cls = catalogService.getSessionCatalogVersions();
		if (cls.size() > 0)
		{
			final CatalogVersionModel clm = (CatalogVersionModel) cls.toArray()[0];
			catalogId = clm.getCatalog().getId();
		}
		return catalogId;
	}

	/**
	 * GetChannelPrice with product ,Currency
	 * 
	 * @param product
	 * @param domesticList
	 * @param internationList
	 * @param isDomesticPrice
	 * @param isInternationPrice
	 */
	public void getChannelPriceWithCurrency(final ProductModel product, ConsultantChannelPrice ccp)
	{
		final SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();
		final Currency currentCurr = ctx.getCurrency();
		final Currency base = currentCurr.isBase().booleanValue() ? null : C2LManager.getInstance().getBaseCurrency();

		Collection<PriceRow> priceRowsList = filterPrice(product, ctx);

		for (final PriceRow priceRow : priceRowsList)
		{
			final Currency priceRowCurr = priceRow.getCurrency();

			if (currentCurr.equals(priceRowCurr) && (base == null || !base.equals(priceRowCurr)))
			{
				final PriceRowModel priceRowModel = modelService.get(priceRow);
				final String channel = priceRowModel.getConsultantChannel().getCode();

				if (channel.equalsIgnoreCase(CHANNEL_DOMETIC) && ccp.isDomesticPrice())
				{
					final PriceInformation priceInformation = Europe1Tools.createPriceInformation(priceRow, priceRow.getCurrency());
					ccp.getDomesticPriceInforList().add(priceInformation);
				}
				else if (channel.equalsIgnoreCase(CHANNEL_INTERNALTIONAL) && ccp.isInternationPrice())
				{
					final PriceInformation priceInformation = Europe1Tools.createPriceInformation(priceRow, priceRow.getCurrency());
					ccp.getInternalPriceInforList().add(priceInformation);
				}
			}
		}
	}

	/**
	 * getChannelPrice
	 * 
	 * @param productModel
	 * @param ctx
	 * @param domesticList
	 * @param internationList
	 * @param isDomesticPrice
	 * @param isInternationPrice
	 */
	public void getChannelPrice(final de.hybris.platform.core.model.product.ProductModel productModel, 
			final SessionContext ctx,
			ConsultantChannelPrice ccp)
	{
		Collection<PriceRow> priceRowsList = filterPrice(productModel, ctx);

		for (final PriceRow priceRow : priceRowsList)
		{
			final PriceRowModel priceRowModel = modelService.get(priceRow);
			final String channel = priceRowModel.getConsultantChannel().getCode();

			if (channel.equalsIgnoreCase(CHANNEL_DOMETIC) && ccp.isDomesticPrice())
			{
				ccp.getDomesticList().add(priceRow);
			}
			else if (channel.equalsIgnoreCase(CHANNEL_INTERNALTIONAL) && ccp.isInternationPrice())
			{
				ccp.getInternationList().add(priceRow);
			}
		}
	}

	/**
	 * GetChannelPrice with List<PriceInformation>
	 * 
	 * @param ctx
	 * @param productModel
	 * @param isDomesticPrice
	 * @param isInternationPrice
	 * 
	 */
	public List<PriceInformation> getChannelPriceWithSingleList(final SessionContext ctx, final ProductModel productModel,
			ConsultantChannelPrice ccp)
	{
		final List<PriceInformation> results = new ArrayList<PriceInformation>();
		final Currency currentCurr = ctx.getCurrency();
		final Currency base = currentCurr.isBase().booleanValue() ? null : C2LManager.getInstance().getBaseCurrency();
		final Collection<PriceRow> rows = filterPrice(productModel, ctx);
		List<PriceRow> list = new ArrayList<PriceRow>(rows);

		Collections.sort(list, SortUtil.PR_COMP);

		for (final PriceRow priceRow : list)
		{
			final Currency priceRowCurr = priceRow.getCurrency();

			if (currentCurr.equals(priceRowCurr) && (base == null || !base.equals(priceRowCurr)))
			{
				final PriceRowModel priceRowModel = modelService.get(priceRow);

				final String channel = priceRowModel.getConsultantChannel().getCode();

				if (channel.equalsIgnoreCase(CHANNEL_DOMETIC) && ccp.isDomesticPrice())
				{
					final PriceInformation priceInformation = Europe1Tools.createPriceInformation(priceRow, priceRow.getCurrency());
					results.add(priceInformation);
				}
				else if (channel.equalsIgnoreCase(CHANNEL_INTERNALTIONAL) && ccp.isInternationPrice())
				{
					final PriceInformation priceInformation = Europe1Tools.createPriceInformation(priceRow, priceRow.getCurrency());
					results.add(priceInformation);
				}
				else
				{
					LOG.error("Not match price row list found");
				}
			}
		}
		return results;
	}

	/**
	 * matchChannelByCatalog by productCountry,catalogId
	 * 
	 * @param productCountry
	 * @param catalogId
	 * @param isDomesticPrice
	 * @param isInternationPrice
	 */
	public void matchChannelByCatalog(final String productCountry, final String catalogId, ConsultantChannelPrice ccp)
	{
		if (catalogId != null && !catalogId.equals("") && productCountry != null && !productCountry.equals(""))
		{
			if (verifyCatalog(catalogId, productCountry))
			{
				ccp.setDomesticPrice(true);
			}
			else
			{
				ccp.setInternationPrice(true);
			}
		}
	}

	/**
	 * matchChannelBySite by siteUid ,productCountry
	 * 
	 * @param currentSiteUid
	 * @param productCountry
	 * @param isDomesticPrice
	 * @param isInternationPrice
	 */
	public void matchChannelBySite(final String currentSiteUid, final String productCountry, ConsultantChannelPrice ccp)
	{
		if (currentSiteUid != null && !currentSiteUid.equals("") && productCountry != null && !productCountry.equals(""))
		{
			if (verifyCatalog(currentSiteUid, productCountry))
			{
				ccp.setDomesticPrice(true);
			}
			else
			{
				ccp.setInternationPrice(true);
			}
		}
	}

	/**
	 * filterPrice by productModel and SessionContext
	 * 
	 * @param productModel
	 * @param ctx
	 * @return Collection<PriceRow>
	 */
	public Collection<PriceRow> filterPrice(final de.hybris.platform.core.model.product.ProductModel productModel,
			final SessionContext ctx)
	{
		final EnumerationValue productClass = Europe1PriceFactory.getInstance().getPPG(ctx,
				modelService.<Product> getSource(productModel));

		final Collection<PriceRow> priceRowsList = Europe1PriceFactory.getInstance().getProductPriceRowsFast(ctx,
				modelService.<Product> getSource(productModel), productClass);
		return priceRowsList;
	}

	/**
	 * verifyCatalog with catalogId and productCountry
	 * 
	 * @param catalogId
	 * @param productCountry
	 * @return
	 */
	public boolean verifyCatalog(final String catalogId, final String productCountry)
	{
		boolean catalogMatch = false;

		if (catalogId.substring(SiteUtil.START_INDEX, SiteUtil.END_INDEX).equalsIgnoreCase(SiteUtil.SITE_UK)
				&& productCountry.equalsIgnoreCase(SiteUtil.UK_REGION))
		{
			catalogMatch = true;
			return catalogMatch;
		}
		else if (catalogId.substring(SiteUtil.START_INDEX, SiteUtil.END_INDEX).equalsIgnoreCase(SiteUtil.SITE_ZH)
				&& productCountry.equalsIgnoreCase(SiteUtil.ZH_REGION))
		{
			catalogMatch = true;
			return catalogMatch;
		}
		else
		{
			return catalogMatch;
		}
	}
}
