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
package com.hybris.core.service.channel.price;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.commerceservices.util.AbstractComparator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.google.common.base.Preconditions;
import com.hybris.core.service.channel.price.factory.ConsultantAwarePriceFactory;
import com.hybris.core.service.channel.price.strategies.ConsultantChannelPrice;


/**
 * @author Alan Liu
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

	@Resource
	private ConsultantAwarePriceFactory consultantAwarePriceFactory;


	@Override
	/**
	 * Retrieve the minimum price for Product
	 * 
	 * @param product
	 *           the product
	 * @return PriceInformation
	 */
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

	/**
	 * Retrieve the web price returned by ProductItem
	 * 
	 * @param product
	 *           the product
	 * @return PriceInformation
	 */
	@Override
	public PriceInformation getWebPriceForProduct(final ProductModel product)
	{
		validateParameterNotNull(product, "Product model cannot be null");
		final PriceInformation pInfo = findMatchPriceRows(product);

		return pInfo;
	}

	/**
	 * findMatchPriceRows
	 * 
	 * @param product
	 * @return PriceInformation
	 */
	@SuppressWarnings("deprecation")
	public PriceInformation findMatchPriceRows(final ProductModel product)
	{
		Preconditions.checkNotNull(product);
		final String currentSiteUid = cmsSiteService.getCurrentSite().getUid();
		boolean isDomesticPrice = false;
		boolean isInternationPrice = false;
		ConsultantChannelPrice ccp = new ConsultantChannelPrice(isDomesticPrice, isInternationPrice);
		String productCountry = product.getNationality();
		consultantAwarePriceFactory.matchChannelBySite(currentSiteUid, productCountry, ccp);

		List<PriceInformation> domesticList = new ArrayList<PriceInformation>();
		List<PriceInformation> internationList = new ArrayList<PriceInformation>();
		ccp.setDomesticPriceInforList(domesticList);
		ccp.setInternalPriceInforList(internationList);
		
		consultantAwarePriceFactory.getChannelPriceWithCurrency(product,ccp);

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
	 * getMinPriceForLowestQunatity
	 * 
	 * @param domesticList
	 * @return PriceInformation
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

	/**
	 * getLowestVariantPrice
	 * 
	 * @param product
	 * @return PriceInformation
	 */
	protected PriceInformation getLowestVariantPrice(final ProductModel product)
	{
		final Collection<PriceInformation> allVariantPrices = getAllVariantPrices(product);
		if (CollectionUtils.isNotEmpty(allVariantPrices))
		{
			return Collections.min(allVariantPrices, PriceInformationComparator.INSTANCE);
		}
		return null;
	}

	/**
	 * getAllVariantPrices
	 * 
	 * @param product
	 * @return Collection<PriceInformation>
	 */
	protected Collection<PriceInformation> getAllVariantPrices(final ProductModel product)
	{
		final Collection<PriceInformation> prices = new LinkedList<PriceInformation>();
		fillAllVariantPrices(product, prices);
		return prices;
	}

	/**
	 * fillAllVariantPrices
	 * 
	 * @param product
	 * @param prices
	 */
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

	/**
	 * Solr indexing would be trigger
	 * 
	 * @param productModel
	 * @return
	 */
	public List<PriceInformation> getPriceInformationsForProduct(final ProductModel productModel)
	{
		Preconditions.checkNotNull(productModel);
		boolean isDomesticPrice = false;
		boolean isInternationPrice = false;
		String catalogId = "";
		catalogId = consultantAwarePriceFactory.matchCatalogById(catalogId);
		String productCountry = productModel.getNationality();
		ConsultantChannelPrice ccp = new ConsultantChannelPrice(isDomesticPrice, isInternationPrice);
		consultantAwarePriceFactory.matchChannelByCatalog(productCountry, catalogId, ccp);
		SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();
		List<PriceInformation> results = consultantAwarePriceFactory.getChannelPriceWithSingleList(ctx, productModel, ccp);
		return results;
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

}
