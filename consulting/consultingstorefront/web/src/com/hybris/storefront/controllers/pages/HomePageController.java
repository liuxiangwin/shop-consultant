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
package com.hybris.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hybris.storefront.countryselector.strategies.CountrySelectorStrategy;


/**
 * Controller for home page
 */
@Controller
@Scope("tenant")
@RequestMapping("/")
public class HomePageController extends AbstractPageController
{

	private static final String SELECTED_COUNTRY_CODE = "selectedcountry";

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	@Autowired
	private CountrySelectorStrategy countrySelectorStrategy;

	@Resource
	private SessionService sessionService;

	//@RequestMapping(method = RequestMethod.GET, value = "/{country}/")
	@RequestMapping(method = RequestMethod.GET)
	public String home(@RequestParam(value = "logout", defaultValue = "false") final boolean logout, final Model model,
			final RedirectAttributes redirectModel, final HttpServletResponse response, final HttpServletRequest request)
			throws CMSItemNotFoundException, IOException
	{

		final Cookie[] cookies = request.getCookies();

		Cookie cook;
		String selectCountry = "";
		if (cookies != null)
		{
			for (int i = 0; i < cookies.length; i++)
			{
				cook = cookies[i];
				if (cook.getName().equalsIgnoreCase("country-selected"))
				{
					selectCountry = cook.getValue();
				}

			}
		}
		if (selectCountry.equalsIgnoreCase(""))
		{
			//getRedirectStrategy().sendRedirect(request, response, "/consultingstorefront/main");
			//response.sendRedirect("/consultingstorefront/main");
			return REDIRECT_PREFIX + "/main";
			//return REDIRECT_PREFIX + "/consultingstorefront/main";
			//request.getRequestDispatcher("/consultingstorefront/main").forward(request, response);
		}




		final SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();
		ctx.setAttribute("c-channel", "city");
		ctx.setAttribute("c-intro", "frist_manager");

		if (logout)
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER, "account.confirmation.signout.title");
			return REDIRECT_PREFIX + ROOT;
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(null));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(null));
		updatePageTitle(model, getContentPageForLabelOrId(null));

		return getViewForPage(model);
	}

	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveHomePageTitle(cmsPage.getTitle()));
	}
}
