/**
 *
 */
package com.hybris.facades.product.impl;

import de.hybris.platform.jalo.c2l.Language;

import com.hybris.core.services.ConsultantService;
import com.hybris.facades.product.ConsultantFacade;


/**
 * @author Steve
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
}
