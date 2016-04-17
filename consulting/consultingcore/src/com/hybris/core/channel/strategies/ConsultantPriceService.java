/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.hybris.core.channel.strategies;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.commerceservices.util.AbstractComparator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.constants.Europe1Tools;
import de.hybris.platform.europe1.jalo.Europe1PriceFactory;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.google.common.base.Preconditions;
import com.hybris.core.util.SiteUtil;
import com.hybris.core.util.SortUtil;


/**
 * Default implementation of {@link CommercePriceService}
 */
public class ConsultantPriceService implements CommercePriceService
{
	private final static Logger LOG = Logger.getLogger(ConsultantPriceService.class);

	@Resource
	private ModelService modelService;

	@Resource
	private SessionService sessionService;

	@Resource
	private CMSSiteService cmsSiteService;
	@Resource
	private CatalogService catalogService;

	private PriceService priceService;


	@Override
	public PriceInformation getFromPriceForProduct(final ProductModel product)
	{
		validateParameterNotNull(product, "Product model cannot be null");
		if (CollectionUtils.isNotEmpty(product.getVariants()))
		{
			// Find the variant with the lowest price
			return getLowestVariantPrice(product);
		}
		return getWebPriceForProduct(product);
	}

	@Override
	public PriceInformation getWebPriceForProduct(final ProductModel product)
	{
		validateParameterNotNull(product, "Product model cannot be null");
		//final List<PriceInformation> prices = getPriceService().getPriceInformationsForProduct(product);
		final PriceInformation pInfo = findMatchPriceRows(product);

		return pInfo;
	}

	@SuppressWarnings("deprecation")
	public PriceInformation findMatchPriceRows(final ProductModel product)
	{
		Preconditions.checkNotNull(product);
		final String currentSiteUid = cmsSiteService.getCurrentSite().getUid();
		boolean isDomesticPrice = false;
		boolean isInternationPrice = false;
		final String productCountry = product.getNationality();
		if (currentSiteUid != null && !currentSiteUid.equals("") && productCountry != null && !productCountry.equals(""))
		{
			if (currentSiteUid.substring(SiteUtil.START_INDEX, SiteUtil.END_INDEX).equalsIgnoreCase(SiteUtil.SITE_UK)
					&& productCountry.equalsIgnoreCase(SiteUtil.UK_REGION))
			{
				isDomesticPrice = true;
			}
			else if (currentSiteUid.substring(SiteUtil.START_INDEX, SiteUtil.END_INDEX).equalsIgnoreCase(SiteUtil.SITE_ZH)
					&& productCountry.equalsIgnoreCase(SiteUtil.ZH_REGION))
			{
				isDomesticPrice = true;
			}
			else
			{
				isInternationPrice = true;
			}
		}

		final SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();
		final Currency currentCurr = ctx.getCurrency();
		final Currency base = currentCurr.isBase().booleanValue() ? null : C2LManager.getInstance().getBaseCurrency();

		final Collection<PriceRow> priceRowsList = Europe1PriceFactory.getInstance().getProductPriceRowsFast(ctx,
				modelService.<Product> getSource(product), null);
		final List<PriceInformation> domesticList = new ArrayList<PriceInformation>();
		final List<PriceInformation> internationList = new ArrayList<PriceInformation>();

		for (final PriceRow priceRow : priceRowsList)
		{
			final Currency priceRowCurr = priceRow.getCurrency();

			if (currentCurr.equals(priceRowCurr) && (base == null || !base.equals(priceRowCurr)))
			{
				final PriceRowModel priceRowModel = modelService.get(priceRow);
				final String channel = priceRowModel.getConsultantChannel().getCode();

				if (channel.equalsIgnoreCase("domestic") && isDomesticPrice)
				{
					final PriceInformation priceInformation = Europe1Tools.createPriceInformation(priceRow, priceRow.getCurrency());
					domesticList.add(priceInformation);
				}
				else if (channel.equalsIgnoreCase("international") && isInternationPrice)
				{
					final PriceInformation priceInformation = Europe1Tools.createPriceInformation(priceRow, priceRow.getCurrency());
					internationList.add(priceInformation);
				}
			}
		}
		if (!domesticList.isEmpty() && isDomesticPrice)
		{
			return getMinPriceForLowestQunatity(domesticList);
		}
		else if (!internationList.isEmpty() && isInternationPrice)
		{
			return getMinPriceForLowestQunatity(internationList);
		}
		return null;

	}

	/**
	 * @param domesticList
	 * @return
	 */
	private PriceInformation getMinPriceForLowestQunatity(final List<PriceInformation> domesticList)
	{
		PriceInformation minPriceForLowestQuantity = null;
		for (final PriceInformation price : domesticList)
		{
			if (minPriceForLowestQuantity == null
					|| (((Long) minPriceForLowestQuantity.getQualifierValue("minqtd")).longValue() > ((Long) price
							.getQualifierValue("minqtd")).longValue()))
			{
				minPriceForLowestQuantity = price;
			}
		}
		return minPriceForLowestQuantity;
	}

	protected PriceInformation getLowestVariantPrice(final ProductModel product)
	{
		final Collection<PriceInformation> allVariantPrices = getAllVariantPrices(product);
		if (CollectionUtils.isNotEmpty(allVariantPrices))
		{
			return Collections.min(allVariantPrices, PriceInformationComparator.INSTANCE);
		}
		return null;
	}

	protected Collection<PriceInformation> getAllVariantPrices(final ProductModel product)
	{
		final Collection<PriceInformation> prices = new LinkedList<PriceInformation>();
		fillAllVariantPrices(product, prices);
		return prices;
	}

	protected void fillAllVariantPrices(final ProductModel product, final Collection<PriceInformation> prices)
	{
		// Get the price for the current product
		final PriceInformation priceInformation = getWebPriceForProduct(product);
		if (priceInformation != null)
		{
			prices.add(priceInformation);
		}

		final Collection<VariantProductModel> variants = product.getVariants();
		if (CollectionUtils.isNotEmpty(variants))
		{
			for (final VariantProductModel variant : variants)
			{
				fillAllVariantPrices(variant, prices);
			}
		}
	}

	protected PriceService getPriceService()
	{
		return priceService;
	}

	@Required
	public void setPriceService(final PriceService priceService)
	{
		this.priceService = priceService;
	}

	/**
	 * Comparator to naturally order PriceInformation by price value ascending
	 */
	public static class PriceInformationComparator extends AbstractComparator<PriceInformation>
	{
		public static final PriceInformationComparator INSTANCE = new PriceInformationComparator();

		@Override
		protected int compareInstances(final PriceInformation price1, final PriceInformation price2)
		{
			Assert.isTrue(price1.getPriceValue().getCurrencyIso().equals(price2.getPriceValue().getCurrencyIso()),
					"differing currency of web prices");
			return compareValues(price1.getPriceValue().getValue(), price2.getPriceValue().getValue());
		}
	}


	//Solr indexing would be trigger
	public List<PriceInformation> getPriceInformationsForProduct(final ProductModel productModel)
	{
		Preconditions.checkNotNull(productModel);
		final Set<CatalogVersionModel> cls = catalogService.getSessionCatalogVersions();
		String catalogId = "";
		if (cls.size() > 0)
		{
			final CatalogVersionModel clm = (CatalogVersionModel) cls.toArray()[0];
			catalogId = clm.getCatalog().getId();
		}
		boolean isDomesticPrice = false;
		boolean isInternationPrice = false;
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
		}//Europe1PriceFactoryDiscountsIntegrationTest
		 //productGroup(TEST_PRODUCT_GROUP)
		final List<PriceInformation> results = new ArrayList<PriceInformation>();

		final SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();
		final Currency currentCurr = ctx.getCurrency();
		final Currency base = currentCurr.isBase().booleanValue() ? null : C2LManager.getInstance().getBaseCurrency();
		final Collection<PriceRow> rows = Europe1PriceFactory.getInstance().getProductPriceRowsFast(ctx,
				modelService.<Product> getSource(productModel), null);

		final List<PriceRow> list = new ArrayList<PriceRow>(rows);

		Collections.sort(list, SortUtil.PR_COMP);

		for (final PriceRow priceRow : list)
		{
			final Currency priceRowCurr = priceRow.getCurrency();

			if (currentCurr.equals(priceRowCurr) && (base == null || !base.equals(priceRowCurr)))
			{
				final PriceRowModel priceRowModel = modelService.get(priceRow);

				final String channel = priceRowModel.getConsultantChannel().getCode();

				if (channel.equalsIgnoreCase("domestic") && isDomesticPrice)
				{
					final PriceInformation priceInformation = Europe1Tools.createPriceInformation(priceRow, priceRow.getCurrency());
					results.add(priceInformation);
				}
				else if (channel.equalsIgnoreCase("international") && isInternationPrice)
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

	private void originalGetMinPriceForLowestQunatity()
	{
		/*
		 * if (CollectionUtils.isNotEmpty(prices)) { PriceInformation minPriceForLowestQuantity = null; for (final
		 * PriceInformation price : prices) { if (minPriceForLowestQuantity == null || (((Long)
		 * minPriceForLowestQuantity.getQualifierValue("minqtd")).longValue() > ((Long) price
		 * .getQualifierValue("minqtd")).longValue())) { minPriceForLowestQuantity = price; } } return
		 * minPriceForLowestQuantity; }
		 */
	}
}
