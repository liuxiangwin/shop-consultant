/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.hybris.storefront.countryselector.web.interceptors.beforeview;

import de.hybris.platform.acceleratorstorefrontcommons.forms.GuestForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.LoginForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.RegisterForm;
import de.hybris.platform.servicelayer.session.SessionService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hybris.platform.posselector.model.PosSelectorComponentModel;
import com.hybris.storefront.controllers.cms.AbstractCMSComponentController;
import com.hybris.storefront.countryselector.strategies.CountrySelectorStrategy;




@Controller("CountrySelectorComponentController")
@RequestMapping("/view/CountrySelectorComponentController")
public class CountrySelectorComponentController extends AbstractCMSComponentController<PosSelectorComponentModel>
{
	@Autowired
	private CountrySelectorStrategy countrySelectorStrategy;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final PosSelectorComponentModel component)
	{
		model.addAttribute("sites", countrySelectorStrategy.getAllCMMSite());
		//model.addAttribute("currentPos", localStoreFacade.currentBaseStore());
		model.addAttribute(CountrySelectorStrategy.SESSION_SELECT_COUNTYR,
				sessionService.getAttribute(CountrySelectorStrategy.SESSION_SELECT_COUNTYR));

		/*final LoginForm loginForm = new LoginForm();
		model.addAttribute(loginForm);
		model.addAttribute(new RegisterForm());
		model.addAttribute(new GuestForm());*/

	}
}
