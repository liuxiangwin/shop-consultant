/**
 *
 */
package com.hybris.facades.stocklevel;

import de.hybris.platform.commerceservices.stock.strategies.WarehouseSelectionStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.List;

import javax.annotation.Resource;


/**
 * @author Alan Liu
 *
 */


public class DefaultStockLevelFacade
{

	@Resource
	private CartService cartService;

	@Resource
	private StockService stockService;

	@Resource
	private BaseStoreService baseStoreService;

	@Resource
	private WarehouseSelectionStrategy warehouseSelectionStrategy;


	@Resource
	private ProductService productService;



	public ProductModel prepareRealseStockLevel(final long entryNumber)
	{

		final CartModel cartModel = cartService.getSessionCart();
		final AbstractOrderEntryModel entryToUpdate = getEntryForNumber(cartModel, (int) entryNumber);
		final ProductModel productModel = entryToUpdate.getProduct();
		return productModel;

	}


	public void realseStockLevel(final ProductModel productModel)
	{
		final BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
		stockService.release(productModel, baseStore.getWarehouses().get(0), 1, "" + "Relase stock level with consultant");

	}



	public void reserveStockLevel(final String code) throws InsufficientStockLevelException
	{
		final ProductModel product = productService.getProductForCode(code);
		final BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();

		stockService.reserve(product, baseStore.getWarehouses().get(0), 1, "Add Reserver Stock Level");

	}

	protected AbstractOrderEntryModel getEntryForNumber(final AbstractOrderModel order, final int number)
	{
		final List<AbstractOrderEntryModel> entries = order.getEntries();
		if (entries != null && !entries.isEmpty())
		{
			final Integer requestedEntryNumber = Integer.valueOf(number);
			for (final AbstractOrderEntryModel entry : entries)
			{
				if (entry != null && requestedEntryNumber.equals(entry.getEntryNumber()))
				{
					return entry;
				}
			}
		}
		return null;
	}
}
