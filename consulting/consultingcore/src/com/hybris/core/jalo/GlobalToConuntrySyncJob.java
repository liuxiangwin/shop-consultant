package com.hybris.core.jalo;

import de.hybris.platform.catalog.constants.CatalogConstants;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.jalo.SyncItemCronJob;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;

import java.util.Arrays;

import org.apache.log4j.Logger;


@SuppressWarnings("deprecation")
public class GlobalToConuntrySyncJob extends GeneratedGlobalToConuntrySyncJob
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(GlobalToConuntrySyncJob.class.getName());

	@Override
	@SuppressWarnings("deprecation")
	protected SessionContext createSyncSessionContext(final SyncItemCronJob cronJob)
	{
		// All sync CronJobs based on  Country specific CatalogVersionSyncJob should run as the session user syn-user
		final SessionContext ctx = super.createSyncSessionContext(cronJob);

		final User synUser = UserManager.getInstance().getEmployeeByLogin("syn-user");
		if (synUser != null)
		{
			cronJob.setSessionUser(synUser);
			ctx.setUser(synUser);
			// this actually activates restrictions
			ctx.removeAttribute(FlexibleSearch.DISABLE_RESTRICTIONS);
			ctx.removeAttribute(FlexibleSearch.DISABLE_RESTRICTION_GROUP_INHERITANCE);
			ctx.removeAttribute(FlexibleSearch.DISABLE_SESSION_ATTRIBUTES);
		}

		final CatalogVersion targetCatalog = this.getTargetVersion();
		final CatalogVersion sourceCatalog = this.getSourceVersion();
		// Put both catalogs in the session, referenced both in a list as individually
		ctx.setAttribute(CatalogConstants.SESSION_CATALOG_VERSIONS, Arrays.asList(sourceCatalog, targetCatalog));
		ctx.setAttribute(SOURCEVERSION, sourceCatalog);
		ctx.setAttribute(TARGETVERSION, targetCatalog);
		return ctx;
	}

	@Override
	protected Item createItem(final SessionContext ctx, final ComposedType type, final ItemAttributeMap allAttributes)
			throws JaloBusinessException
	{
		// business code placed here will be executed before the item is created
		// then create the item
		final Item item = super.createItem(ctx, type, allAttributes);
		// business code placed here will be executed after the item was created
		// and return the item
		return item;
	}



}
