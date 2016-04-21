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
package com.hybris.core.search.solrfacetsearch.populators;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.commercefacades.product.converters.populator.ProductBasicPopulator;

/**
 * Populate the product data with the most basic product data
 */
public class ConsultantProductPopulator<SOURCE extends ProductModel, TARGET extends ProductData>
extends
		ProductBasicPopulator<SOURCE, TARGET>

{	@Override
	public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException
	{
		productData.setName((String) getProductAttribute(productModel, ProductModel.NAME));
		productData.setManufacturer((String) getProductAttribute(productModel, ProductModel.MANUFACTURERNAME));

		productData.setAverageRating(productModel.getAverageRating());
		if (productModel.getVariantType() != null)
		{
			productData.setVariantType(productModel.getVariantType().getCode());
		}
		if (productModel instanceof VariantProductModel)
		{
			final VariantProductModel variantProduct = (VariantProductModel) productModel;
			productData.setBaseProduct(variantProduct.getBaseProduct() != null ? variantProduct.getBaseProduct().getCode() : null);
		}
		productData.setPurchasable(isApproved(productModel));
	}

	protected boolean isApproved(final SOURCE productModel)
	{
		final ArticleApprovalStatus approvalStatus = productModel.getApprovalStatus();
		return ArticleApprovalStatus.APPROVED.equals(approvalStatus);
	}

}
