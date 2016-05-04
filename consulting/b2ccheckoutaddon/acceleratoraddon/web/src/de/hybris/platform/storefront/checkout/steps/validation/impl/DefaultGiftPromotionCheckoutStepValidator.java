/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */

package de.hybris.platform.storefront.checkout.steps.validation.impl;

import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;
import de.hybris.platform.servicelayer.session.SessionService;

import org.springframework.beans.factory.annotation.Required;


public class DefaultGiftPromotionCheckoutStepValidator
//extends DefaultDeliveryMethodCheckoutStepValidator
{
	private SessionService sessionService;

	/*public ValidationResults validateOnExit()
	{
		final String giftWrapSelection = getSessionService().getAttribute("GIFT_WRAP");
		if (giftWrapSelection != null && giftWrapSelection.equals("YES"))
		{
			return ValidationResults.REDIRECT_TO_DUMMY_STEP;
		}
		if (getCheckoutFacade().hasPickUpItems())
		{
			return ValidationResults.REDIRECT_TO_PICKUP_LOCATION;
		}
		return ValidationResults.SUCCESS;
	}*/

	public SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}