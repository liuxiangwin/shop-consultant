/**
 *
 */
package com.hybris.core.util;

import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.user.User;

import java.util.Comparator;


/**
 * @author Alanliu
 *
 */
public class SortUtil
{
	public static final Comparator<PriceRow> PR_COMP = new Comparator<PriceRow>()
	{
		@Override
		public int compare(final PriceRow object1, final PriceRow object2)
		{
			// USER
			final User user1 = object1.getUser();
			final User user2 = object2.getUser();
			String uid1 = user1 != null ? user1.getUID() : "ZZZZZZZZZZZZZZZZZZ";
			String uid2 = user2 != null ? user2.getUID() : "ZZZZZZZZZZZZZZZZZZ";
			int ret = uid1.compareToIgnoreCase(uid2);
			if (ret != 0)
			{
				return ret;
			}
			// UG
			final EnumerationValue ug1 = object1.getUserGroup();
			final EnumerationValue ug2 = object2.getUserGroup();
			uid1 = ug1 != null ? ug1.getCode() : "ZZZZZZZZZZZZZZZZZZ";
			uid2 = ug2 != null ? ug2.getCode() : "ZZZZZZZZZZZZZZZZZZ";
			ret = uid1.compareToIgnoreCase(uid2);
			if (ret != 0)
			{
				return ret;
			}
			// P
			int product1 = object1.getProduct() != null ? 1 : 0;
			int product2 = object2.getProduct() != null ? 1 : 0;
			ret = product1 - product2;
			if (ret != 0)
			{
				return ret;
			}
			// ProductId
			final int productId1 = object1.getProductId() != null ? 1 : 0;
			final int productId2 = object2.getProductId() != null ? 1 : 0;
			ret = productId1 - productId2;
			if (ret != 0)
			{
				return ret;
			}
			// PG
			product1 = object1.getProductGroup() != null ? 1 : 0;
			product2 = object2.getProductGroup() != null ? 1 : 0;
			ret = product1 - product2;
			if (ret != 0)
			{
				return ret;
			}
			// CURR
			final String currency1 = object1.getCurrency().getIsoCode();
			final String currency2 = object2.getCurrency().getIsoCode();
			ret = currency1.compareToIgnoreCase(currency2);
			if (ret != 0)
			{
				return ret;
			}
			// NET
			product1 = object1.isNetAsPrimitive() ? 1 : 0;
			product2 = object2.isNetAsPrimitive() ? 1 : 0;
			ret = product1 - product2;
			if (ret != 0)
			{
				return ret;
			}
			// UNIT
			final String un1 = object1.getUnit().getCode();
			final String un2 = object2.getUnit().getCode();
			ret = un1.compareToIgnoreCase(un2);
			if (ret != 0)
			{
				return ret;
			}
			// MIN
			final long min1 = object1.getMinqtdAsPrimitive();
			final long min2 = object2.getMinqtdAsPrimitive();
			ret = (int) (min1 - min2);
			if (ret != 0)
			{
				return ret;
			}
			else
			{
				return object1.getPK().compareTo(object2.getPK());
			}
		}
	};
}
