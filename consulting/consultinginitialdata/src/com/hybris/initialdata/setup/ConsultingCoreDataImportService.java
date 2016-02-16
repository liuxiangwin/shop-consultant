/**
 *
 */
package com.hybris.initialdata.setup;

import de.hybris.platform.commerceservices.dataimport.impl.CoreDataImportService;
import de.hybris.platform.commerceservices.util.ResponsiveUtils;


/**
 * @author I320189
 *
 */
public class ConsultingCoreDataImportService extends CoreDataImportService
{

	@Override
	public void importCommonData(final String extensionName)
	{
		getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/essential-data.impex", extensionName),
				true);
	}

	@Override
	protected void importProductCatalog(final String extensionName, final String productCatalogName)
	{
		getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/productCatalogs/%sProductCatalog/catalog.impex",
				extensionName, productCatalogName), false);
	}

	@Override
	protected void importContentCatalog(final String extensionName, final String contentCatalogName)
	{
		getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/contentCatalogs/%sContentCatalog/catalog.impex",
				extensionName, contentCatalogName), false);
		getSetupImpexService().importImpexFile(String.format(
				"/%s/import/coredata/contentCatalogs/%sContentCatalog/cms-content.impex", extensionName, contentCatalogName), false);
		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/coredata/contentCatalogs/%sContentCatalog/cms-mobile-content.impex",
						extensionName, contentCatalogName), false);
		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/coredata/contentCatalogs/%sContentCatalog/email-content.impex",
						extensionName, contentCatalogName), false);
		if (ResponsiveUtils.isResponsive())
		{
			getSetupImpexService()
					.importImpexFile(String.format("/%s/import/coredata/contentCatalogs/%sContentCatalog/cms-responsive-content.impex",
							extensionName, contentCatalogName), false);
		}
	}

	@Override
	protected void importStore(final String extensionName, final String storeName, final String productCatalogName)
	{
		getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/stores/%s/store.impex", extensionName, storeName),
				false);
		getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/stores/%s/site.impex", extensionName, storeName),
				false);
		if (ResponsiveUtils.isResponsive())
		{
			getSetupImpexService().importImpexFile(
					String.format("/%s/import/coredata/stores/%s/store-responsive.impex", extensionName, storeName), false);
			getSetupImpexService().importImpexFile(
					String.format("/%s/import/coredata/stores/%s/site-responsive.impex", extensionName, storeName), false);
		}
	}

	@Override
	protected void importSolrIndex(final String extensionName, final String storeName)
	{
		getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/stores/%s/solr.impex", extensionName, storeName),
				false);

		getSetupSolrIndexerService().createSolrIndexerCronJobs(String.format("%sIndex", storeName));

		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/coredata/stores/%s/solrtrigger.impex", extensionName, storeName), false);
	}

	@Override
	protected void importJobs(final String extensionName, final String storeName)
	{
		getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/stores/%s/jobs.impex", extensionName, storeName),
				false);
	}



}
