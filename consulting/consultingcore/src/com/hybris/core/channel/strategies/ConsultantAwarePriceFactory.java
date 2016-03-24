/**
 *
 */
package com.hybris.core.channel.strategies;

import de.hybris.platform.catalog.jalo.CatalogAwareEurope1PriceFactory;
import de.hybris.platform.europe1.channel.strategies.RetrieveChannelStrategy;
import de.hybris.platform.europe1.constants.Europe1Tools;
import de.hybris.platform.europe1.enums.PriceRowChannel;
import de.hybris.platform.europe1.jalo.PDTRow;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.util.DateRange;
import de.hybris.platform.util.PriceValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author AlanLiu
 *
 */
public class ConsultantAwarePriceFactory extends CatalogAwareEurope1PriceFactory
//extends Europe1PriceFactory
{

	private RetrieveChannelStrategy retrieveChannelStrategy;

	@Override
	public List getPriceInformations(final SessionContext ctx, @SuppressWarnings("deprecation") final Product product,
			@SuppressWarnings("deprecation") final EnumerationValue productGroup, final User user,
			@SuppressWarnings("deprecation") final EnumerationValue userGroup, final Currency curr, final boolean net,
			final Date date, final Collection taxValues) throws JaloPriceFactoryException
	{
		// compute price info rows ( filter out unreachable rows first )
		final Collection<PriceRow> priceRows = filterPriceRows(matchPriceRowsForInfo(ctx, product, productGroup, user, userGroup,
				curr, date, net));
		final List<PriceInformation> priceInfos = new ArrayList<PriceInformation>(priceRows.size());
		Collection theTaxValues = taxValues;

		final List<PriceInformation> defaultPriceInfos = new ArrayList<PriceInformation>(priceRows.size());
		final PriceRowChannel channel = retrieveChannelStrategy.getChannel(ctx);

		for (final PriceRow row : priceRows)
		{
			PriceInformation pInfo = Europe1Tools.createPriceInformation(row, curr);
			// convert net/gross if necessary
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
			}
			//YTODO: We need to cross verify this logic. It works fine but is redundant.
			// always creates a list with the default PriceRows
			if (row.getChannel() == null)
			{
				defaultPriceInfos.add(pInfo);
			}

			// if its coming from a default channel should return only default PriceRows (default is null channel)
			if (channel == null && row.getChannel() == null)
			{
				priceInfos.add(pInfo);
			}
			// if its coming from a specific channel then it should match with the PriceRow channel
			else if (channel != null && row.getChannel() != null && row.getChannel().getCode().equalsIgnoreCase(channel.getCode()))
			{
				priceInfos.add(pInfo);
			}
		}
		// If no PriceRow was found for the specified channel then it should return the default list.
		if (priceInfos.size() == 0)
		{
			return defaultPriceInfos;
		}
		else
		{
			return priceInfos;
		}
	}


	public static final PriceInformation createPriceInformation(final PriceRow row, final Currency currency)
	{
		final Map qualifiers = new HashMap();
		qualifiers.put(PriceRow.MINQTD, Long.valueOf(row.getMinQuantity()));
		qualifiers.put(PriceRow.UNIT, row.getUnit());
		qualifiers.put(PriceRow.PRICEROW, row);

		//row.getp
		final DateRange dateRange = row.getDateRange();


		if (dateRange != null)
		{
			qualifiers.put(PDTRow.DATERANGE, dateRange);
		}
		final Currency act_curr = row.getCurrency();
		// get base price ( convert if necessary )
		final double basePrice = currency.equals(act_curr) ? row.getPriceAsPrimitive() / row.getUnitFactorAsPrimitive() : act_curr
				.convert(currency, row.getPriceAsPrimitive() / row.getUnitFactorAsPrimitive());
		// if base price is not in requested net/gross state, compute it
		return new PriceInformation(qualifiers, new PriceValue(currency.getIsoCode(), basePrice, row.isNetAsPrimitive()));
	}
}
