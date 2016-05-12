/**
 *
 */
package com.hybris.initialdata.setup;

import de.hybris.platform.commerceservices.dataimport.impl.SampleDataImportService;
import de.hybris.platform.commerceservices.util.ResponsiveUtils;

import org.apache.log4j.Logger;


/**
 * @author Alan Liu
 *
 */
public class ConsultingSampleDataImportService extends SampleDataImportService
{

	private static final Logger LOG = Logger.getLogger(ConsultingSampleDataImportService.class);

	@Override
	protected void importCommonData(final String extensionName)
	{
		if (isExtensionLoaded(CMS_COCKPIT_EXTENSION_NAME))
		{
			getSetupImpexService().importImpexFile(
					String.format("/%s/import/sampledata/cockpits/cmscockpit/cmscockpit-users.impex", extensionName), false);
		}

		if (isExtensionLoaded(PRODUCT_COCKPIT_EXTENSION_NAME))
		{
			getSetupImpexService().importImpexFile(
					String.format("/%s/import/sampledata/cockpits/productcockpit/productcockpit-users.impex", extensionName), false);
		}

		if (isExtensionLoaded(CS_COCKPIT_EXTENSION_NAME))
		{
			getSetupImpexService().importImpexFile(
					String.format("/%s/import/sampledata/cockpits/cscockpit/cscockpit-users.impex", extensionName), false);
		}

		if (isExtensionLoaded(REPORT_COCKPIT_EXTENSION_NAME))
		{
			getSetupImpexService().importImpexFile(
					String.format("/%s/import/sampledata/cockpits/reportcockpit/reportcockpit-users.impex", extensionName), false);

			if (isExtensionLoaded(MCC_EXTENSION_NAME))
			{
				getSetupImpexService().importImpexFile(
						String.format("/%s/import/sampledata/cockpits/reportcockpit/reportcockpit-mcc-links.impex", extensionName),
						false);
			}
		}

		//Load Demo User Data
		getSetupImpexService().importImpexFile(String.format("/%s/import/sampledata/userData/user-demo-data.impex", extensionName),
				true);

	}

	@Override
	protected void importProductCatalog(final String extensionName, final String productCatalogName)
	{
		// Load Units
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/classifications-units.impex", extensionName,
						productCatalogName), false);

		// Load Categories
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/categories.impex", extensionName,
						productCatalogName), false);

		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/categories-classifications.impex",
						extensionName, productCatalogName), false);

		// Load Suppliers
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/suppliers.impex", extensionName,
						productCatalogName), false);
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/suppliers-media.impex", extensionName,
						productCatalogName), false);

		// Load medias for Categories as Suppliers loads some new Categories
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/categories-media.impex", extensionName,
						productCatalogName), false);

		// Load Products
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products.impex", extensionName,
						productCatalogName), false);
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-media.impex", extensionName,
						productCatalogName), false);
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-classifications.impex", extensionName,
						productCatalogName), false);

		// Load Products Relations
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-relations.impex", extensionName,
						productCatalogName), false);

		// Load Products Fixes
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-fixup.impex", extensionName,
						productCatalogName), false);

		// Load Prices
		//getSetupImpexService()
		//		.importImpexFile(String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-prices.impex",
		//				extensionName, productCatalogName), false);

		//getSetupImpexService().importImpexFile(
		//		String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/product-prices-class.impex", extensionName,
		//				productCatalogName), false);
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-prices.impex", extensionName,
						productCatalogName), false);

		//Load Product Price Group
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-prices-price-group.impex",
						extensionName, productCatalogName), false);

		// Load Stock Levels
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-stocklevels.impex", extensionName,
						productCatalogName), false);
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-pos-stocklevels.impex", extensionName,
						productCatalogName), false);

		// Load Taxes
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-tax.impex", extensionName,
						productCatalogName), false);
	}

	@Override
	protected void importContentCatalog(final String extensionName, final String contentCatalogName)
	{
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/contentCatalogs/%sContentCatalog/cms-content.impex", extensionName,
						contentCatalogName), false);
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/contentCatalogs/%sContentCatalog/cms-mobile-content.impex", extensionName,
						contentCatalogName), false);
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/contentCatalogs/%sContentCatalog/email-content.impex", extensionName,
						contentCatalogName), false);
		if (ResponsiveUtils.isResponsive())
		{
			getSetupImpexService().importImpexFile(
					String.format("/%s/import/sampledata/contentCatalogs/%sContentCatalog/cms-responsive-content.impex",
							extensionName, contentCatalogName), false);
		}
	}

	@Override
	protected void importStore(final String extensionName, final String storeName, final String productCatalogName)
	{
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/stores/%s/points-of-service-media.impex", extensionName, storeName), false);
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/stores/%s/points-of-service.impex", extensionName, storeName), false);

		if (isExtensionLoaded(BTG_EXTENSION_NAME))
		{
			getSetupImpexService().importImpexFile(
					String.format("/%s/import/sampledata/stores/%s/btg.impex", extensionName, storeName), false);
		}

		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/stores/%s/warehouses.impex", extensionName, storeName), false);

		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/reviews.impex", extensionName,
						productCatalogName), false);

		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/stores/%s/promotions.impex", extensionName, storeName), false);
	}

	@Override
	protected void importJobs(final String extensionName, final String storeName)
	{
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/stores/%s/jobs.impex", extensionName, storeName), false);
	}

	//@Override
	protected void importSolrIndex(final String extensionName, final String storeName, final String solrMode)
	{
		LOG.debug("Importing solr index for " + storeName);

		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/stores/%s/solr_" + solrMode + ".impex", extensionName, storeName), false);

		getSetupSolrIndexerService().createSolrIndexerCronJobs(String.format("%sIndex", storeName));
	}

}
