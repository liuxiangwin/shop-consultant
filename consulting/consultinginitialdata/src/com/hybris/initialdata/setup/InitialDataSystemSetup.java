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
package com.hybris.initialdata.setup;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.jalo.Catalog;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.jalo.SyncItemCronJob;
import de.hybris.platform.catalog.jalo.SyncItemJob;
import de.hybris.platform.commerceservices.dataimport.impl.CoreDataImportService;
import de.hybris.platform.commerceservices.dataimport.impl.SampleDataImportService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.initialdata.constants.ConsultingInitialDataConstants;


/**
 * @author Alan Liu
 * @class This provides hooks into the system's initialization and update processes.
 *
 * @see "https://wiki.hybris.com/display/release4/Hooks+for+Initialization+and+Update+Process"
 */
@SystemSetup(extension = ConsultingInitialDataConstants.EXTENSIONNAME)
public class InitialDataSystemSetup extends AbstractSystemSetup
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(InitialDataSystemSetup.class);

	private UserService userService;

	private static final String IMPORT_CORE_DATA = "importCoreData";
	private static final String IMPORT_SAMPLE_DATA = "importSampleData";
	private static final String ACTIVATE_SOLR_CRON_JOBS = "activateSolrCronJobs";

	private static final String ZH_CONSULTING_STORE = "zh-consultingstore";
	private static final String UK_CONSULTING_STORE = "uk-consultingstore";
	private static final String BASE_CONSULTING_CATALOG_PREFIX = "consultingstore";
	private static final String EXTENSION_NAME = "consultinginitialdata";


	private ConsultingSampleDataImportService consultingSampleDataImportService;
	private CoreDataImportService coreDataImportService;
	private SampleDataImportService sampleDataImportService;
	private CronJobService cronJobService;

	private ConsultingCoreDataImportService consultingCoreDataImportService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "cronJobService")
	private CronJobService cronjobService;

	@Resource(name = "catalogService")
	private CatalogService catalogService;

	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();

		params.add(createBooleanSystemSetupParameter(IMPORT_CORE_DATA, "Import Core Data", true));
		params.add(createBooleanSystemSetupParameter(IMPORT_SAMPLE_DATA, "Import Sample Data", true));
		params.add(createBooleanSystemSetupParameter(ACTIVATE_SOLR_CRON_JOBS, "Activate Solr Cron Jobs", true));
		return params;
	}

	/**
	 * Implement this method to create initial objects. This method will be called by system creator during
	 * initialization and system update. Be sure that this method can be called repeatedly.
	 *
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
	public void createEssentialData(final SystemSetupContext context)
	{
		// Add Essential Data here as you require
	}

	/**
	 * Implement this method to create data that is used in your project. This method will be called during the system
	 * initialization.
	 *
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.PROJECT, process = Process.ALL)
	public void createProjectData(final SystemSetupContext context)
	{

		/////////////////////////////////////////////////////////////////////////////////
		/////////////////////////CORE Data //////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////
		// Common
		getConsultingCoreDataImportService().importCommonData(EXTENSION_NAME);

		// 1. Import core base product catalog (Staged only)
		getConsultingCoreDataImportService().importProductCatalog(EXTENSION_NAME, BASE_CONSULTING_CATALOG_PREFIX);

		// 2. Import core base content catalog (Staged)
		getConsultingCoreDataImportService().importContentCatalog(EXTENSION_NAME, BASE_CONSULTING_CATALOG_PREFIX);


		// 3. Import core Country Specific Product catalogs
		getConsultingCoreDataImportService().importProductCatalog(EXTENSION_NAME, UK_CONSULTING_STORE);
		getConsultingCoreDataImportService().importProductCatalog(EXTENSION_NAME, ZH_CONSULTING_STORE);

		// 4. Import core Country Specific Stores
		getConsultingCoreDataImportService().importStore(EXTENSION_NAME, UK_CONSULTING_STORE, "");
		getConsultingCoreDataImportService().importStore(EXTENSION_NAME, ZH_CONSULTING_STORE, "");

		// 5 Import core Solr configuration,job trigger configuration
		getConsultingCoreDataImportService().importSolrIndex(EXTENSION_NAME, UK_CONSULTING_STORE);
		getConsultingCoreDataImportService().importSolrIndex(EXTENSION_NAME, ZH_CONSULTING_STORE);

		// 6 Import Jobs includes (Synchronization job define & JobSearchRestriction)
		getConsultingCoreDataImportService().importJobs(EXTENSION_NAME, UK_CONSULTING_STORE);
		getConsultingCoreDataImportService().importJobs(EXTENSION_NAME, ZH_CONSULTING_STORE);

		/////////////////////////////////////////////////////////////////////////////////
		//////////////////////// SAMPLE Data ////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////
		// 7 Import Sample Common
		getConsultingSampleDataImportService().importCommonData(EXTENSION_NAME);

		// 8. Import sample base product catalog
		getConsultingSampleDataImportService().importProductCatalog(EXTENSION_NAME, BASE_CONSULTING_CATALOG_PREFIX);

		// 9. Import sample base content catalog
		getConsultingSampleDataImportService().importContentCatalog(EXTENSION_NAME, BASE_CONSULTING_CATALOG_PREFIX);

		// 10 Sample Store
		getConsultingSampleDataImportService().importStore(EXTENSION_NAME, UK_CONSULTING_STORE, "");
		getConsultingSampleDataImportService().importStore(EXTENSION_NAME, ZH_CONSULTING_STORE, "");

		// 12. Sync global product catalog to country staged
		executeGlobalCatalogSyncJob(UK_CONSULTING_STORE);
		executeGlobalCatalogSyncJob(ZH_CONSULTING_STORE);

		// 13. Sync each country specific staged version to their online version
		getConsultingSampleDataImportService().synchronizeProductCatalog(this, context, UK_CONSULTING_STORE, true);
		getConsultingSampleDataImportService().synchronizeProductCatalog(this, context, ZH_CONSULTING_STORE, true);

		//Import sample Country specific product catalog
		getConsultingSampleDataImportService().importProductCatalog(EXTENSION_NAME, ZH_CONSULTING_STORE);
		getConsultingSampleDataImportService().importProductCatalog(EXTENSION_NAME, UK_CONSULTING_STORE);


		// 14 Sync base content to online content
		getConsultingSampleDataImportService().synchronizeContentCatalog(this, context, BASE_CONSULTING_CATALOG_PREFIX, true);

		// 15 Import Sample Solr configuration
		getConsultingSampleDataImportService().importSolrIndex(EXTENSION_NAME, UK_CONSULTING_STORE,
				getSolrMode(UK_CONSULTING_STORE));
		getConsultingSampleDataImportService().importSolrIndex(EXTENSION_NAME, ZH_CONSULTING_STORE,
				getSolrMode(ZH_CONSULTING_STORE));

		// 16. Activate Solr cron job with Online&Stage data
		LOG.debug("Activating solr indexes ");
		getConsultingCoreDataImportService().runSolrIndex(EXTENSION_NAME, ZH_CONSULTING_STORE + "Staged");
		getConsultingCoreDataImportService().runSolrIndex(EXTENSION_NAME, UK_CONSULTING_STORE + "Staged");

		getConsultingCoreDataImportService().runSolrIndex(EXTENSION_NAME, UK_CONSULTING_STORE + "Online");
		getConsultingCoreDataImportService().runSolrIndex(EXTENSION_NAME, ZH_CONSULTING_STORE + "Online");

	}

	private String getSolrMode(final String storeName)
	{
		return Config.getParameter(storeName + ".solrMode");
	}

	@SuppressWarnings("deprecation")
	public PerformResult executeGlobalCatalogSyncJob(final String catalogId)
	{
		PerformResult performResult = null;

		// In the case of the Global, do our own custom sync to
		//the staged version of each country specific catalog
		@SuppressWarnings("deprecation")
		final Catalog baseConsultingCatalogue = CatalogManager.getInstance().getCatalog(
				BASE_CONSULTING_CATALOG_PREFIX + "ProductCatalog");
		final Catalog targetConsultingCatalogue = CatalogManager.getInstance().getCatalog(catalogId + "ProductCatalog");

		if (baseConsultingCatalogue != null && targetConsultingCatalogue != null)
		{
			final CatalogVersion globalConsultingSource = baseConsultingCatalogue.getCatalogVersion(CatalogManager.OFFLINE_VERSION);
			final CatalogVersion targetConsultingTarget = targetConsultingCatalogue
					.getCatalogVersion(CatalogManager.OFFLINE_VERSION);

			performResult = executeCatalogSyncJob(
					CatalogManager.getInstance().getSyncJob(globalConsultingSource, targetConsultingTarget), catalogId
							+ "ProductCatalog");

		}
		return performResult;
	}

	@SuppressWarnings("deprecation")
	private PerformResult executeCatalogSyncJob(final SyncItemJob catalogSyncJob, final String catalogName)
	{
		final SyncItemCronJob syncJob = catalogSyncJob.newExecution();
		syncJob.setLogToDatabase(false);
		syncJob.setLogToFile(false);
		syncJob.setForceUpdate(false);

		final User synUser = UserManager.getInstance().getEmployeeByLogin("syn-user");
		if (synUser != null)
		{
			syncJob.setSessionUser(synUser);
		}
		LOG.info("Created cronjob [" + syncJob.getCode() + "] to synchronize catalog [" + BASE_CONSULTING_CATALOG_PREFIX
				+ "] staged to catalog [" + catalogName + "] staged.");
		LOG.info("Configuring full version sync");

		catalogSyncJob.configureFullVersionSync(syncJob);

		LOG.info("Starting synchronization, this may take a while ...");

		catalogSyncJob.perform(syncJob, true);

		LOG.info("Synchronization complete for catalog [" + catalogName + "]");
		final CronJobResult result = getModelService().get(syncJob.getResult());
		final CronJobStatus status = getModelService().get(syncJob.getStatus());
		return new PerformResult(result, status);
	}

	private void addRootTypes()
	{
		//add the Types we want to sync
		final List<ComposedType> rootTypes = new ArrayList<ComposedType>();
		rootTypes.add(TypeManager.getInstance().getComposedType("Category"));
		rootTypes.add(TypeManager.getInstance().getComposedType("Product"));
		rootTypes.add(TypeManager.getInstance().getComposedType("Media"));
	}

	private ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public CoreDataImportService getCoreDataImportService()
	{
		return coreDataImportService;
	}

	@Required
	public void setCoreDataImportService(final CoreDataImportService coreDataImportService)
	{
		this.coreDataImportService = coreDataImportService;
	}

	public SampleDataImportService getSampleDataImportService()
	{
		return sampleDataImportService;
	}

	@Required
	public void setSampleDataImportService(final SampleDataImportService sampleDataImportService)
	{
		this.sampleDataImportService = sampleDataImportService;
	}

	public CronJobService getCronJobService()
	{
		return cronJobService;
	}


	public void setCronJobService(final CronJobService cronJobService)
	{
		this.cronJobService = cronJobService;
	}

	public ConsultingSampleDataImportService getConsultingSampleDataImportService()
	{
		return consultingSampleDataImportService;
	}

	@Required
	public void setConsultingSampleDataImportService(final ConsultingSampleDataImportService consultingSampleDataImportService)
	{
		this.consultingSampleDataImportService = consultingSampleDataImportService;
	}

	public ConsultingCoreDataImportService getConsultingCoreDataImportService()
	{
		return consultingCoreDataImportService;
	}

	@Required
	public void setConsultingCoreDataImportService(final ConsultingCoreDataImportService consultingCoreDataImportService)
	{
		this.consultingCoreDataImportService = consultingCoreDataImportService;
	}

	public CatalogService getCatalogService()
	{
		return catalogService;
	}

	public void setCatalogService(final CatalogService catalogService)
	{
		this.catalogService = catalogService;
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	@SuppressWarnings("unused")
	private void medieConvertProcess()
	{
		// 11. Run media conversion on baseProductCatalog
		// Can't get this working but will post support q's
		//final MediaConversionCronJobModel mediaConversionCronJob = modelService.create(MediaConversionCronJobModel.class);
		//final JobModel jobModel = cronjobService.getJob("mediaConversionJob");
		//mediaConversionCronJob.setJob(jobModel);
		//mediaConversionCronJob.setCode("mediaConversionJob-init");
		//mediaConversionCronJob.setSessionUser(getUserService().getCurrentUser());
		//mediaConversionCronJob.setSessionLanguage(getUserService().getCurrentUser().getSessionLanguage());
		//mediaConversionCronJob.setSessionCurrency(getUserService().getCurrentUser().getSessionCurrency());
		//getModelService().save(mediaConversionCronJob);
		//getCronJobService().performCronJob(mediaConversionCronJob);
	}

}
