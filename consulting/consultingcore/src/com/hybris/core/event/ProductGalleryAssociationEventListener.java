/**
 *
 */
package com.hybris.core.event;

import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.event.events.AfterItemCreationEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.hybris.core.model.ConsultantModel;


/**
 * @author Alan Liu
 *
 */
public class ProductGalleryAssociationEventListener extends AbstractEventListener<AfterItemCreationEvent>
{

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "impersonationService")
	private ImpersonationService impersonationService;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;


	@Resource(name = "searchRestrictionService")
	private SearchRestrictionService searchRestrictionService;

	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}


	/**
	 * @return the searchRestrictionService
	 */
	public SearchRestrictionService getSearchRestrictionService()
	{
		return searchRestrictionService;
	}


	/**
	 * @param searchRestrictionService
	 *           the searchRestrictionService to set
	 */
	public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService)
	{
		this.searchRestrictionService = searchRestrictionService;
	}


	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}


	/**
	 * @return the impersonationService
	 */
	public ImpersonationService getImpersonationService()
	{
		return impersonationService;
	}


	/**
	 * @param impersonationService
	 *           the impersonationService to set
	 */
	public void setImpersonationService(final ImpersonationService impersonationService)
	{
		this.impersonationService = impersonationService;
	}



	private static final String MAGIC_PREFIX = "Magik";
	private static final String CONVERTED_MEDIA_PREFIX = "master-consultant-";
	private static final String PRODUCT_CODE_FORMAT = "00000000";


	private enum ImageType
	{
		PICTURE, THUMB
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.servicelayer.event.impl.AbstractEventListener#onEvent(de.hybris.platform.servicelayer.event.
	 * events.AbstractEvent)
	 */
	@Override
	protected void onEvent(final AfterItemCreationEvent event)
	{
		if (event.getTypeCode().equals(MediaModel._TYPECODE))
		{
			final PK MediaPK = (PK) event.getSource();
			final MediaModel mediaModel = getModelService().get(MediaPK);

			if (mediaModel.getMediaFormat().getName().contains(MAGIC_PREFIX))
			{
				boolean mediaContainerFound = false;


				// Check if media container is already associate to product, if not then add

				final ConsultantModel consultantModel = getConsultantFromMedia(mediaModel);
				final List<MediaContainerModel> galleryImages = consultantModel.getGalleryImages();

				// Iterate media containers - we have one but there could be many
				if (galleryImages != null)
				{


					for (final MediaContainerModel mediaContainer : galleryImages)
					{

						if (mediaModel.getMediaContainer().getQualifier().equals(mediaContainer.getQualifier()))
						{
							mediaContainerFound = true;
							break;
						}
					}
				}

				// Media found ?
				if (!mediaContainerFound)
				{
					final List<MediaContainerModel> medias = new ArrayList<MediaContainerModel>();
					medias.add(mediaModel.getMediaContainer());

					// Set Gallery images
					consultantModel.setGalleryImages(medias);


					// Set picture, thumbnail
					consultantModel.setPicture(getImage(medias.get(0), ImageType.PICTURE));
					consultantModel.setThumbnail(getImage(medias.get(0), ImageType.THUMB));

					modelService.save(consultantModel);
				}

			}
		}

	}




	private MediaModel getImage(final MediaContainerModel medias, final ImageType imageType)
	{
		MediaModel imageMedia = null;

		for (final MediaModel media : medias.getMedias())
		{
			switch (imageType)
			{
				case PICTURE:
					imageMedia = media;
					break;

				case THUMB:
					imageMedia = media;
					break;
			}
		}

		return imageMedia;
	}




	private ConsultantModel getConsultantFromMedia(final MediaModel mediaModel)
	{
		final String consultantCode = mediaModel.getCode().substring(CONVERTED_MEDIA_PREFIX.length(),
				CONVERTED_MEDIA_PREFIX.length() + PRODUCT_CODE_FORMAT.length());
		final ConsultantModel exampleConsultantModel = new ConsultantModel();
		exampleConsultantModel.setCode(consultantCode);

		// Set current catalog version too
		exampleConsultantModel.setCatalogVersion(mediaModel.getCatalogVersion());

		// Disable search restrictions
		getSearchRestrictionService().disableSearchRestrictions();

		final ConsultantModel consultantModel = getFlexibleSearchService().getModelByExample(exampleConsultantModel);

		// Enable search restrictions
		getSearchRestrictionService().enableSearchRestrictions();
		return consultantModel;

	}



	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}



	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}


}
