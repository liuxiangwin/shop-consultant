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
package com.hybris.core.price.cart;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceUpdateCartEntryStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;


public class ConsultantCommerceUpdateCartEntryStrategy extends DefaultCommerceUpdateCartEntryStrategy

{

	@Override
	public CommerceCartModification updateQuantityForCartEntry(final CommerceCartParameter parameters)
			throws CommerceCartModificationException
	{
		beforeUpdateCartEntry(parameters);
		final CartModel cartModel = parameters.getCart();
		final long newQuantity = parameters.getQuantity();
		final long entryNumber = parameters.getEntryNumber();

		validateParameterNotNull(cartModel, "Cart model cannot be null");
		CommerceCartModification modification;

		final AbstractOrderEntryModel entryToUpdate = getEntryForNumber(cartModel, (int) entryNumber);
		validateEntryBeforeModification(newQuantity, entryToUpdate);
		final Integer maxOrderQuantity = entryToUpdate.getProduct().getMaxOrderQuantity();
		// removing items)
		//这里算出来的可能是负数//设定为1就是不更新了

		//final long quantityToAdd = newQuantity - entryToUpdate.getQuantity().longValue();

		if (entryToUpdate.getDeliveryPointOfService() != null)
		{
			final long actualAllowedQuantityChange = getAllowedCartAdjustmentForProduct(cartModel, entryToUpdate.getProduct(), 1,
					entryToUpdate.getDeliveryPointOfService());
			//Now do the actual cartModification
			modification = modifyEntry(cartModel, entryToUpdate, actualAllowedQuantityChange, newQuantity, maxOrderQuantity);
			return modification;
		}
		else
		{
			final long actualAllowedQuantityChange;
			//Update quantity but no need update
			if (newQuantity > 0)
			{
				actualAllowedQuantityChange = getAllowedCartAdjustmentForProduct(cartModel, entryToUpdate.getProduct(), 0, null);
				modification = modifyEntry(cartModel, entryToUpdate, actualAllowedQuantityChange, newQuantity, maxOrderQuantity);
			}
			else
			{
				//Remove the stock
				actualAllowedQuantityChange = getAllowedCartAdjustmentForProduct(cartModel, entryToUpdate.getProduct(), -1, null);
				modification = modifyEntry(cartModel, entryToUpdate, actualAllowedQuantityChange, 0, maxOrderQuantity);
			}


			afterUpdateCartEntry(parameters, modification);
			return modification;
		}
	}



	@Override
	protected CommerceCartModification modifyEntry(final CartModel cartModel, final AbstractOrderEntryModel entryToUpdate,
			final long actualAllowedQuantityChange, final long newQuantity, final Integer maxOrderQuantity)
	{
		// Now work out how many that leaves us with on this entry
		//final long entryNewQuantity = entryToUpdate.getQuantity().longValue();

		final ModelService modelService = getModelService();

		if (newQuantity <= 0)
		{
			final CartEntryModel entry = new CartEntryModel();
			entry.setProduct(entryToUpdate.getProduct());

			// The allowed new entry quantity is zero or negative
			// just remove the entry
			modelService.remove(entryToUpdate);
			modelService.refresh(cartModel);
			normalizeEntryNumbers(cartModel);
			getCommerceCartCalculationStrategy().calculateCart(cartModel);

			// Return an empty modification
			final CommerceCartModification modification = new CommerceCartModification();
			modification.setEntry(entry);
			modification.setQuantity(0);
			// We removed all the quantity from this row
			modification.setQuantityAdded(-1);

			if (newQuantity == 0)
			{
				modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
			}
			else
			{
				modification.setStatusCode(CommerceCartModificationStatus.LOW_STOCK);
			}

			return modification;
		}
		else
		{
			// Adjust the entry quantity to the new value
			entryToUpdate.setQuantity(Long.valueOf(newQuantity));
			modelService.save(entryToUpdate);
			modelService.refresh(cartModel);
			getCommerceCartCalculationStrategy().calculateCart(cartModel);
			modelService.refresh(entryToUpdate);

			// Return the modification data
			final CommerceCartModification modification = new CommerceCartModification();

			//返回除原来购物车中已有的数据外，还能放进去的数量
			modification.setQuantityAdded(0);
			modification.setEntry(entryToUpdate);
			modification.setQuantity(newQuantity);

			modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);

			/*
			 * if (isMaxOrderQuantitySet(maxOrderQuantity) && entryNewQuantity == maxOrderQuantity.longValue()) {
			 * modification.setStatusCode(CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED); } else if
			 * (newQuantity == entryNewQuantity) { modification.setStatusCode(CommerceCartModificationStatus.SUCCESS); }
			 * else { modification.setStatusCode(CommerceCartModificationStatus.LOW_STOCK); }
			 */

			return modification;
		}
	}


}
