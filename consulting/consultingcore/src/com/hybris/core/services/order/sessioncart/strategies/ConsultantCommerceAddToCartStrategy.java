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
package com.hybris.core.services.order.sessioncart.strategies;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceAddToCartStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;


public class ConsultantCommerceAddToCartStrategy extends DefaultCommerceAddToCartStrategy

{
	@Override
	/**
	 * 
	 */
	/**
	 * Modify the OOTB Add cart validation with variant ConsultantModel  
	 * since the OOTB model does not  allow the product model with VariantType
	 * could add the session cart 
	 * @param CommerceCartParameter parameters
	 */
	public void validateAddToCart(final CommerceCartParameter parameters) throws CommerceCartModificationException
	{
		final CartModel cartModel = parameters.getCart();
		final ProductModel productModel = parameters.getProduct();

		validateParameterNotNull(cartModel, "Cart model cannot be null");
		validateParameterNotNull(productModel, "Product model cannot be null");

		if (parameters.getQuantity() < 1)
		{
			throw new CommerceCartModificationException("Quantity must not be less than one");
		}
	}
}
