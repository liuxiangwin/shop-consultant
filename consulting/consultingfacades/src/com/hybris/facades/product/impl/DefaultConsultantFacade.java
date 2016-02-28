/**
 *
 */
package com.hybris.facades.product.impl;

import de.hybris.platform.commercefacades.order.data.ConsultantData;
import de.hybris.platform.jalo.c2l.Language;

import java.util.ArrayList;
import java.util.List;

import com.hybris.core.services.ConsultantService;
import com.hybris.facades.product.ConsultantFacade;


/**
 * @author Alan Liu
 *
 */
public class DefaultConsultantFacade implements ConsultantFacade
{

	private ConsultantService consultantService;



	@Override
	public Language getDefaultLanguageForCountryIsocode(final String countryIsocode)
	{
		return consultantService.getDefaultLanguageForCountryIsocode(countryIsocode);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.hybris.facades.product.ConsultantFacade#getCountrySelectedForSession(de.hybris.platform.servicelayer.session.
	 * SessionService)
	 */
	@Override
	public String getCountrySelectedForSession(final boolean returnSafeDefault)
	{
		return consultantService.getCountrySelectedForSession(returnSafeDefault);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.hybris.facades.product.ConsultantFacade#setCountryCodeSelectedForSession(de.hybris.platform.servicelayer.
	 * session.SessionService, java.lang.String)
	 */
	@Override
	public void setCountryCodeSelectedForSession(final String countryCode)
	{
		this.getConsultantService().setCountryCodeSelectedForSession(countryCode);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.hybris.facades.product.ConsultantFacade#setCountryPkSelectionForSession(de.hybris.platform.servicelayer.
	 * session.SessionService, java.lang.String)
	 */
	@Override
	public void setCountryPkSelectionForSession(final String countryCode)
	{
		this.getConsultantService().setCountryPkSelectionForSession(countryCode);
	}


	/**
	 * @return the consultantService
	 */
	public ConsultantService getConsultantService()
	{
		return consultantService;
	}

	/**
	 * @param consultantService
	 *           the consultantService to set
	 */
	public void setConsultantService(final ConsultantService consultantService)
	{
		this.consultantService = consultantService;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hybris.facades.product.ConsultantFacade#getExtraInfo(java.lang.String)
	 */
	@Override
	public List<ConsultantData> getExtraInfo(final String code)
	{

		final List<String> result = consultantService.getExtraInfo(code);

		final List<ConsultantData> datas = new ArrayList<ConsultantData>();
		final ConsultantData consultantData = new ConsultantData();
		if (result.get(0) != null)
		{
			consultantData.setForName(result.get(0));
		}
		if (result.get(1) != null)
		{
			consultantData.setSurName(result.get(1));
		}

		datas.add(consultantData);
		return datas;
	}
}
