/*
 *
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
 */
package com.hybris.core.service.channel.price.strategies;

import de.hybris.platform.core.HybrisEnumValue;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Generated enum PriceRowChannel declared at extension europe1.
 */
@SuppressWarnings("PMD")
public class ConsultantCorePriceRowChannel implements HybrisEnumValue
{
	public final static String _TYPECODE = "PriceRowChannel";

	public final static String SIMPLE_CLASSNAME = "PriceRowChannel";
	private static final ConcurrentMap<String, ConsultantCorePriceRowChannel> cache = new ConcurrentHashMap<String, ConsultantCorePriceRowChannel>();
	/**
	 * Generated enum value for PriceRowChannel.desktop declared at extension europe1.
	 */
	public static final ConsultantCorePriceRowChannel DESKTOP = valueOf("desktop");

	/**
	 * Generated enum value for PriceRowChannel.mobile declared at extension europe1.
	 */
	public static final ConsultantCorePriceRowChannel MOBILE = valueOf("mobile");


	/** The code of this enum. */
	private final String code;
	private final String codeLowerCase;

	/**
	 * Creates a new enum value for this enum type.
	 *
	 * @param code
	 *           the enum value code
	 */
	private ConsultantCorePriceRowChannel(final String code)
	{
		this.code = code.intern();
		this.codeLowerCase = this.code.toLowerCase().intern();
	}


	/**
	 * Compares this object to the specified object. The result is <code>true</code> if and only if the argument is not
	 * <code>null</code> and is an <code>PriceRowChannel
	 * </code> object that contains the enum value <code>code</code> as this object.
	 *
	 * @param obj
	 *           the object to compare with.
	 * @return <code>true</code> if the objects are the same; <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj)
	{
		try
		{
			final HybrisEnumValue enum2 = (HybrisEnumValue) obj;
			return this == enum2
					|| (enum2 != null && !this.getClass().isEnum() && !enum2.getClass().isEnum()
							&& this.getType().equalsIgnoreCase(enum2.getType()) && this.getCode().equalsIgnoreCase(enum2.getCode()));
		}
		catch (final ClassCastException e)
		{
			return false;
		}
	}

	/**
	 * Gets the code of this enum value.
	 *
	 * @return code of value
	 */
	@Override
	public String getCode()
	{
		return this.code;
	}

	/**
	 * Gets the type this enum value belongs to.
	 *
	 * @return code of type
	 */
	@Override
	public String getType()
	{
		return SIMPLE_CLASSNAME;
	}

	/**
	 * Returns a hash code for this <code>PriceRowChannel</code>.
	 *
	 * @return a hash code value for this object, equal to the enum value <code>code</code> represented by this
	 *         <code>PriceRowChannel</code> object.
	 */
	@Override
	public int hashCode()
	{
		return this.codeLowerCase.hashCode();
	}

	/**
	 * Returns the code representing this enum value.
	 *
	 * @return a string representation of the value of this object.
	 */
	@Override
	public String toString()
	{
		return this.code.toString();
	}

	/**
	 * Returns a <tt>PriceRowChannel</tt> instance representing the specified enum value.
	 *
	 * @param code
	 *           an enum value
	 * @return a <tt>PriceRowChannel</tt> instance representing <tt>value</tt>.
	 */
	public static ConsultantCorePriceRowChannel valueOf(final String code)
	{
		final String key = code.toLowerCase();
		ConsultantCorePriceRowChannel result = cache.get(key);
		if (result == null)
		{
			final ConsultantCorePriceRowChannel newValue = new ConsultantCorePriceRowChannel(code);
			final ConsultantCorePriceRowChannel previous = cache.putIfAbsent(key, newValue);
			result = previous != null ? previous : newValue;
		}
		return result;
	}

}
