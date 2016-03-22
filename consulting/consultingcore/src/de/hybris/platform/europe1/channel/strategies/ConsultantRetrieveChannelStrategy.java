/**
 *
 */
package de.hybris.platform.europe1.channel.strategies;

import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.europe1.channel.strategies.impl.DefaultRetrieveChannelStrategy;
import de.hybris.platform.europe1.enums.PriceRowChannel;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * @author Alan Liu
 *
 */
public class ConsultantRetrieveChannelStrategy implements RetrieveChannelStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultRetrieveChannelStrategy.class);
	protected static final String CHANNEL = "channel";
	protected static final String DETECTED_UI_EXPERIENCE_LEVEL = "UiExperienceService-Detected-Level";

	private EnumerationService enumerationService;

	@Override
	public PriceRowChannel getChannel(final SessionContext ctx)
	{
		LOG.debug("Inside ChannelRetrievalStrategy.");
		PriceRowChannel priceRowChannel = null;
		if (ctx == null || ctx.getAttribute(DETECTED_UI_EXPERIENCE_LEVEL) == null)
		{
			//If the channel is null, the client caller will treat this as default price row.
			return priceRowChannel;
		}
		else
		{
			priceRowChannel = ctx.getAttribute(CHANNEL);
			if (priceRowChannel == null)
			{
				final EnumerationValue enumUIExpLevel = ctx.getAttribute(DETECTED_UI_EXPERIENCE_LEVEL);
				priceRowChannel = getEnumValueForCode(enumUIExpLevel.getCode().toLowerCase());
				if (priceRowChannel != null)
				{
					ctx.setAttribute(CHANNEL, priceRowChannel);
				}
			}
		}
		return priceRowChannel;
	}

	@Override
	public List<PriceRowChannel> getAllChannels()
	{
		return enumerationService.getEnumerationValues(PriceRowChannel._TYPECODE);
	}

	//Get the dynamic enum values for the PriceRowChannel code and the exact value for DETECTED_UI_EXPERIENCE_LEVEL
	private PriceRowChannel getEnumValueForCode(final String channel)
	{
		PriceRowChannel channelFromDb = null;
		try
		{
			channelFromDb = enumerationService.getEnumerationValue(PriceRowChannel._TYPECODE, channel);
		}
		catch (final UnknownIdentifierException unknownIdentifierException)
		{
			//Intentionally Left Blank
			LOG.debug("This Enum is not setup in PriceRowChannel dynamic enum");
		}
		return channelFromDb;
	}

	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}
}
