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
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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

	@Resource(name = "cmsPageService")
	private CMSPageService cmsPageService;

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@Resource(name = "commerceCategoryService")
	private CommerceCategoryService commerceCategoryService;

	@Resource(name = "categoryPageController")
	private CategoryPageController categoryPageController;

	@Resource
	private SessionService sessionService;

	//@RequestMapping(method = RequestMethod.GET, value = "/{country}/")
	@RequestMapping(method = RequestMethod.GET)
	public String home(@RequestParam(value = "logout", defaultValue = "false") final boolean logout, final Model model,
			final RedirectAttributes redirectModel, final HttpServletResponse response, final HttpServletRequest request)
			throws CMSItemNotFoundException, IOException
	{

		//final Cookie[] cookies = request.getCookies();
		//Cookie cook;
		//String selectCountry = "";
		//if (cookies != null)
		//{
		//	for (int i = 0; i < cookies.length; i++)
		//	{
		//		cook = cookies[i];
		//		if (cook.getName().equalsIgnoreCase("country-selected"))
		//		{
		//			selectCountry = cook.getValue();
		//		}
		//	}
		//}
		//if (selectCountry.equalsIgnoreCase(""))
		//{
		//	return REDIRECT_PREFIX + "/main";
		//}
		if (request.getQueryString() != null)
		{
			if (!request.getQueryString().contains("consultingsite"))
			{
				//return REDIRECT_PREFIX + "/main";
				response.sendRedirect("/main");
			}
		}
		if (logout)
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER, "account.confirmation.signout.title");
			//return REDIRECT_PREFIX + ROOT;
			response.sendRedirect(ROOT);
		}

		//storeCmsPageInModel(model, getContentPageForLabelOrId(null));
		//setUpMetaDataForContentPage(model, getContentPageForLabelOrId(null));
		//updatePageTitle(model, getContentPageForLabelOrId(null));

		final CategoryModel category = commerceCategoryService.getCategoryForCode("Development");

		CategoryPageModel catalogPageModel = null;
		try
		{
			catalogPageModel = cmsPageService.getPageForCategory(category);
		}
		catch (final CMSItemNotFoundException e)
		{
			LOG.debug(e);
		}
		storeCmsPageInModel(model, catalogPageModel);
		updatePageTitle(model, catalogPageModel);

		return getViewForPage(model);

		//return FORWARD_PREFIX + "/Development/c/Development";
		//return categoryPageController.category("Development", "", 0, ShowMode.Page, "", model, request, response);
		//return getViewForPage(catalogPageModel);


		//final String redirect_url = ControllerConstants.Url.REDIRECT_PREFIX + "https://" + country.toLowerCase()
		//		+ "-consultingsite.local:9002/consultingstorefront/" + country.toLowerCase()
		//		+ "-consultingsite/en/search?q=%3Arelevance&show=All";

		//LOG.debug("redirecting to " + redirect_url);

		//return REDIRECT_PREFIX + "/Development/c/Development";
	}

	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveHomePageTitle(cmsPage.getTitle()));
	}

	protected CategoryPageModel getCategoryPage(final CategoryModel category)
	{
		try
		{
			return getCmsPageService().getPageForCategory(category);
		}
		catch (final CMSItemNotFoundException ignore)
		{
			// Ignore
		}
		return null;
	}

	@Override
	protected String getViewForPage(final AbstractPageModel page)
	{
		if (page != null)
		{
			final PageTemplateModel masterTemplate = page.getMasterTemplate();
			if (masterTemplate != null)
			{
				final String targetPage = cmsPageService.getFrontendTemplateName(masterTemplate);
				if (targetPage != null && !targetPage.isEmpty())
				{
					return PAGE_ROOT + targetPage;
				}
			}
		}
		return null;
	}


	@SuppressWarnings("deprecation")
	public String getViewForPage(final String absoluteURL, final Model model, final String locale,
			@SuppressWarnings("unused") final HttpServletResponse response)
	{
		initializeSiteFromRequest(absoluteURL, locale);
		CategoryPageModel catalogPageModel = null;
		try
		{
			catalogPageModel = cmsPageService.getPageByCategoryCode("Development");
		}
		catch (final CMSItemNotFoundException e)
		{
			LOG.debug(e);
		}
		storeCmsPageInModel(model, catalogPageModel);
		updatePageTitle(model, catalogPageModel);
		return REDIRECT_PREFIX + "/Development/c/Development";
	}

	private CMSSiteModel initializeSiteFromRequest(final String absoluteURL, final String locale)
	{
		try
		{
			final URL currentURL = new URL(absoluteURL);
			final CMSSiteModel cmsSiteModel = cmsSiteService.getSiteForURL(currentURL);
			if (cmsSiteModel != null)
			{
				cmsSiteModel.setLocale(locale);
				baseSiteService.setCurrentBaseSite(cmsSiteModel, true);
				return cmsSiteModel;
			}
		}
		catch (final MalformedURLException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Cannot find CMSSite associated with current URL ( " + absoluteURL
						+ " - check whether this is correct URL) !");
			}
		}
		catch (final CMSItemNotFoundException e)
		{
			LOG.warn("Cannot find CMSSite associated with current URL (" + absoluteURL + ")!");
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e);
			}
		}
		return null;
	}
}
