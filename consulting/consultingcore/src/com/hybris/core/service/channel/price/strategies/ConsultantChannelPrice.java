/**
 * 
 */
package com.hybris.core.service.channel.price.strategies;

import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.order.price.PriceInformation;

import java.util.List;


/**
 * @author Alan Liu
 *
 */
public class ConsultantChannelPrice
{

	private boolean isDomesticPrice;

	private boolean isInternationPrice;
	
	private List<PriceRow> domesticList;
	private List<PriceRow> internationList;

	private List<PriceInformation> internalPriceInforList;

	private List<PriceInformation> domesticPriceInforList;
	
	/**
	 * @param isDomesticPrice
	 * @param isInternationPrice
	 */
	public ConsultantChannelPrice(boolean isDomesticPrice, boolean isInternationPrice)
	{
		super();
		this.isDomesticPrice = isDomesticPrice;
		this.isInternationPrice = isInternationPrice;
	}


	/**
	 * @return the domesticList
	 */
	public List<PriceRow> getDomesticList()
	{
		return domesticList;
	}


	/**
	 * @param domesticList the domesticList to set
	 */
	public void setDomesticList(List<PriceRow> domesticList)
	{
		this.domesticList = domesticList;
	}


	/**
	 * @return the internationList
	 */
	public List<PriceRow> getInternationList()
	{
		return internationList;
	}


	/**
	 * @param internationList the internationList to set
	 */
	public void setInternationList(List<PriceRow> internationList)
	{
		this.internationList = internationList;
	}


	/**
	 * @return the internalPriceInforList
	 */
	public List<PriceInformation> getInternalPriceInforList()
	{
		return internalPriceInforList;
	}


	/**
	 * @param internalPriceInforList the internalPriceInforList to set
	 */
	public void setInternalPriceInforList(List<PriceInformation> internalPriceInforList)
	{
		this.internalPriceInforList = internalPriceInforList;
	}


	/**
	 * @return the domesticPriceInforList
	 */
	public List<PriceInformation> getDomesticPriceInforList()
	{
		return domesticPriceInforList;
	}


	/**
	 * @param domesticPriceInforList the domesticPriceInforList to set
	 */
	public void setDomesticPriceInforList(List<PriceInformation> domesticPriceInforList)
	{
		this.domesticPriceInforList = domesticPriceInforList;
	}


	/**
	 * @return the isDomesticPrice
	 */
	public boolean isDomesticPrice()
	{
		return isDomesticPrice;
	}

	/**
	 * @param isDomesticPrice
	 *           the isDomesticPrice to set
	 */
	public void setDomesticPrice(boolean isDomesticPrice)
	{
		this.isDomesticPrice = isDomesticPrice;
	}

	/**
	 * @return the isInternationPrice
	 */
	public boolean isInternationPrice()
	{
		return isInternationPrice;
	}

	/**
	 * @param isInternationPrice
	 *           the isInternationPrice to set
	 */
	public void setInternationPrice(boolean isInternationPrice)
	{
		this.isInternationPrice = isInternationPrice;
	}


}
