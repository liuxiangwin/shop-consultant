/**
 *
 */
package com.hybris.storefront.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hybris.facades.product.ConsultantFacade;
import com.hybris.storefront.security.cookie.CountrySelectedCookieGenerator;


/**
 * @author I320189
 *
 */
public class CountrySelectedRestorationFilter extends OncePerRequestFilter
{
	private ConsultantFacade consultantFacade;
	private CountrySelectedCookieGenerator countrySelectedCookieGenerator;
	public static final Logger LOG = Logger.getLogger(CountrySelectedRestorationFilter.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{

		// Check session data first for request - is it null ?
		final String selectedCountryInSession = getConsultantFacade().getCountrySelectedForSession(false);
		if (selectedCountryInSession != null && selectedCountryInSession.isEmpty())
		{
			final Cookie[] cookies = request.getCookies();

			if (cookies != null)
			{
				for (final Cookie cookie : cookies)
				{
					if (getCountrySelectedCookieGenerator().getCookieName().equals(cookie.getName()))
					{

						LOG.debug("Found cookie " + cookie.getName() + " with value " + cookie.getValue());

						final String countryCode = cookie.getValue();

						// Need to ensure code and PK is set of country too in case inbound request is direct to solr pages
						getConsultantFacade().setCountryCodeSelectedForSession(countryCode);
						getConsultantFacade().setCountryPkSelectionForSession(countryCode);

						break;
					}
				}
			}
		}

		filterChain.doFilter(request, response);


	}

	/**
	 * @return the consultantFacade
	 */
	public ConsultantFacade getConsultantFacade()
	{
		return consultantFacade;
	}

	/**
	 * @param consultantFacade
	 *           the consultantFacade to set
	 */
	public void setConsultantFacade(final ConsultantFacade consultantFacade)
	{
		this.consultantFacade = consultantFacade;
	}


	/**
	 * @return the countrySelectedCookieGenerator
	 */
	public CountrySelectedCookieGenerator getCountrySelectedCookieGenerator()
	{
		return countrySelectedCookieGenerator;
	}

	/**
	 * @param countrySelectedCookieGenerator
	 *           the countrySelectedCookieGenerator to set
	 */
	public void setCountrySelectedCookieGenerator(final CountrySelectedCookieGenerator countrySelectedCookieGenerator)
	{
		this.countrySelectedCookieGenerator = countrySelectedCookieGenerator;
	}

}
