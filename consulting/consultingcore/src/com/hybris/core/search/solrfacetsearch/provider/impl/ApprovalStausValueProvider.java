/**
 *
 */
package com.hybris.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Required;

import com.hybris.core.model.ConsultantModel;


/**
 * @author Alan Liu
 *
 */
public class ApprovalStausValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider, Serializable
{
	private FieldNameProvider fieldNameProvider;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private CatalogService catalogService;

	@Resource
	private FlexibleSearchService flexibleSearchService;


	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{

		final ConsultantModel consultantModel = getConsultantProductModel(model);

		if (consultantModel == null)
		{
			return Collections.emptyList();
		}

		try
		{
			//final Object value = getPropertyValue(model);
			final ArticleApprovalStatus value = getApprovalstatus(consultantModel);
			final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
			if (value != null)
			{
				final String content = value.toString();

				if (indexedProperty.isLocalized())
				{
					final Collection<LanguageModel> languages = indexConfig.getLanguages();
					for (final LanguageModel language : languages)
					{
						fieldValues.addAll(createFieldValue(content, language, indexedProperty));
					}
				}
				else
				{
					fieldValues.addAll(createFieldValue(content, null, indexedProperty));
				}

			}
			return fieldValues;
		}
		catch (final Exception e)
		{
			LOG.error(e);
			throw new FieldValueProviderException("Cannot evaluate " + indexedProperty.getName() + " using "
					+ this.getClass().getName(), e);
		}
	}

	protected List<FieldValue> createFieldValue(final String value, final LanguageModel language,
			final IndexedProperty indexedProperty)
	{
		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();

		if (language != null)
		{
			final Locale locale = i18nService.getCurrentLocale();
			try
			{
				i18nService.setCurrentLocale(commonI18NService.getLocaleForLanguage(language));
			}
			finally
			{
				i18nService.setCurrentLocale(locale);
			}

			final Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, language.getIsocode());
			for (final String fieldName : fieldNames)
			{
				fieldValues.add(new FieldValue(fieldName, value));
			}
		}
		else
		{
			final Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, null);
			for (final String fieldName : fieldNames)
			{
				fieldValues.add(new FieldValue(fieldName, value));
			}
		}
		return fieldValues;
	}

	protected Object getPropertyValue(final Object model)
	{
		return getPropertyValue(model, "approvalstatus");
	}

	protected Object getPropertyValue(final Object model, final String propertyName)
	{
		return modelService.getAttributeValue(model, propertyName);
	}


	protected ConsultantModel getConsultantProductModel(final Object model)
	{
		if (model instanceof ConsultantModel)
		{
			final ConsultantModel consultantModel = (ConsultantModel) model;
			return consultantModel;
		}
		else
		{
			return null;
		}
	}

	protected FieldNameProvider getFieldNameProvider()
	{
		return fieldNameProvider;
	}

	@Required
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}


	public ArticleApprovalStatus getApprovalstatus(final ConsultantModel consultantModel)
	{
		List<ConsultantModel> resultList = null;
		try
		{
			final String queryUserPk = "SELECT {p.pk} FROM {Consultant AS p JOIN CatalogVersion AS cv on {p.catalogVersion}={cv.pk}"
					+ " JOIN Catalog AS c on {cv.catalog} = {c.pk}} WHERE {c.id} =?cId" + " AND {cv.version} =?cvVersion"
					+ " AND  {p:code}=?code";

			final CatalogVersionModel catalogVersionModel = this.getCurrentCatalog();
			final FlexibleSearchQuery query = new FlexibleSearchQuery(queryUserPk);
			query.addQueryParameter("cId", catalogVersionModel.getCatalog().getId());
			query.addQueryParameter("cvVersion", catalogVersionModel.getCatalog().getVersion());
			query.addQueryParameter("code", consultantModel.getCode());
			resultList = flexibleSearchService.<ConsultantModel> search(query).getResult();
		}
		catch (final Exception e)
		{
			LOG.error("Exception" + e.getMessage());
		}
		if (resultList.size() > 0)
		{
			final ConsultantModel consultant = resultList.get(0);
			if (consultant != null)
			{
				return consultant.getApprovalStatus();
			}
		}
		return null;
	}

	private CatalogVersionModel getCurrentCatalog()
	{
		final Set<CatalogVersionModel> cls = catalogService.getSessionCatalogVersions();
		if (cls.size() > 0)
		{
			for (final CatalogVersionModel clm : cls)
			{
				final String catalogName = clm.getCatalog().getId();
				if (catalogName.contains("ProductCatalog"))
				{
					return clm;
				}
			}
		}
		return null;
	}

}
