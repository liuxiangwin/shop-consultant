/**
 *
 */
package com.hybris.core.channel.strategies;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.jalo.Europe1PriceFactory;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;


/**
 * Created by Matt Rossner on 8/31/15.
 */
public class SingleFindUpgPriceStrategy
{
	private final static Logger LOG = Logger.getLogger(SingleFindUpgPriceStrategy.class);

	private SessionService sessionService;

	private ModelService modelService;

	//@Override
	//@SuppressWarnings("deprecation")
	// those fields won't be going away any time soon :(
	// using Europe1PriceFactory forces us to use deprecated code
	// when Europe1PriceFactory moves to service layer, we can too
	public Collection<PriceRowModel> findPriceRows(final ProductModel product)
	{
		Preconditions.checkNotNull(product);
		final String upg = "";//sessionService.getAttribute(INDEX_UPG);
		Preconditions.checkNotNull(upg);

		final EnumerationValue value = Europe1PriceFactory.getInstance().getUserPriceGroup(upg);
		final Collection<PriceRow> priceRows = Europe1PriceFactory.getInstance().getProductPriceRows(
				modelService.<Product> getSource(product), value);

		for (final PriceRow priceRow : priceRows)
		{
			final PriceRowModel priceRowModel = modelService.get(priceRow);
			if (priceRowModel.getUg() == null)
			{
				continue;
			}
			if (value.getCode().equals(priceRowModel.getUg().getCode()))
			{
				return Collections.singleton(priceRowModel);
			}
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("No price found for MOO \"" + upg + "\" and product " + product.getCode() + "/" + product.getName());
		}

		return Collections.emptyList();
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}