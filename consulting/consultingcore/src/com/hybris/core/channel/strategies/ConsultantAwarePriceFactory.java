/**
 *
 */
package com.hybris.core.channel.strategies;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.jalo.CatalogAwareEurope1PriceFactory;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.PK;
import de.hybris.platform.europe1.constants.Europe1Constants;
import de.hybris.platform.europe1.jalo.Europe1PriceFactory;
import de.hybris.platform.europe1.jalo.PDTRowsQueryBuilder;
import de.hybris.platform.europe1.jalo.PDTRowsQueryBuilder.QueryWithParams;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.hybris.core.util.SiteUtil;


/**
 * This class support customer the EuporeFactory but it powerfully deprecated
 *
 * @author AlanLiu
 *
 */
//@Deprecated
public class ConsultantAwarePriceFactory extends CatalogAwareEurope1PriceFactory
//extends Europe1PriceFactory
{
	private final static Logger LOG = Logger.getLogger(ConsultantAwarePriceFactory.class);

	@Resource
	private CMSSiteService cmsSiteService;

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogService catalogService;

	@Override
	public Collection<PriceRow> queryPriceRows4Price(final SessionContext ctx, final Product product,
			final EnumerationValue productGroup, final User user, final EnumerationValue userGroup)
	{
		final de.hybris.platform.core.model.product.ProductModel productModel = modelService.get(product.getPK());

		Preconditions.checkNotNull(product);
		//final String currentSiteUid = cmsSiteService.getCurrentSite().getUid();

		Preconditions.checkNotNull(productModel);
		final Set<CatalogVersionModel> cls = catalogService.getSessionCatalogVersions();
		String catalogId = "";
		if (cls.size() > 0)
		{
			final CatalogVersionModel clm = (CatalogVersionModel) cls.toArray()[0];
			catalogId = clm.getCatalog().getId();
		}

		//CN Or GB
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
		}
		//final Currency currentCurr = ctx.getCurrency();
		//final Currency base = currentCurr.isBase().booleanValue() ? null : C2LManager.getInstance().getBaseCurrency();

		final Collection<PriceRow> priceRowsList = Europe1PriceFactory.getInstance().getProductPriceRowsFast(ctx,
				modelService.<Product> getSource(productModel), null);

		final List<PriceRow> domesticList = new ArrayList<PriceRow>();
		final List<PriceRow> internationList = new ArrayList<PriceRow>();

		for (final PriceRow priceRow : priceRowsList)
		{
			//final Currency priceRowCurr = priceRow.getCurrency();
			//if (currentCurr.equals(priceRowCurr) && (base == null || !base.equals(priceRowCurr)))
			//{
			final PriceRowModel priceRowModel = modelService.get(priceRow);
			final String channel = priceRowModel.getConsultantChannel().getCode();

			if (channel.equalsIgnoreCase("domestic") && isDomesticPrice)
			{
				domesticList.add(priceRow);
			}
			else if (channel.equalsIgnoreCase("international") && isInternationPrice)
			{
				internationList.add(priceRow);
			}
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

	private Collection<PriceRow> orignalQuery(final SessionContext ctx, final Product product,
			final EnumerationValue productGroup, final User user, final EnumerationValue userGroup)
	{

		final PK productPk = product == null ? null : product.getPK();
		final PK productGroupPk = productGroup == null ? null : productGroup.getPK();
		final PK userPk = user == null ? null : user.getPK();
		final PK userGroupPk = userGroup == null ? null : userGroup.getPK();
		final String productId = extractProductId(ctx, product);


		final PDTRowsQueryBuilder builder = getPDTRowsQueryBuilderFor(Europe1Constants.TC.PRICEROW);
		final QueryWithParams queryAndParams = builder.withAnyProduct().withAnyUser().withProduct(productPk)
				.withProductId(productId).withProductGroup(productGroupPk).withUser(userPk).withUserGroup(userGroupPk).build();
		return FlexibleSearch.getInstance().search(ctx, queryAndParams.getQuery(), queryAndParams.getParams(), PriceRow.class)
				.getResult();
	}
}
