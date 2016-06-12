/**
 *
 */
package com.hybris.core.event;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.event.events.AfterItemCreationEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import javax.annotation.Resource;

import com.hybris.core.model.ConsultantModel;


/**
 * @author Alan Liu
 *
 */
public class ConsultantApprovalCheckEventListener extends AbstractEventListener<AfterItemCreationEvent>
{

	@Resource(name = "modelService")
	private ModelService modelService;

	private static final String CONSULTANT_APPROVAL_KEY = "consultant.approval";

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
		if (event.getTypeCode().endsWith(ConsultantModel._TYPECODE))
		{
			final PK ConsultantPK = (PK) event.getSource();
			final ConsultantModel consultantModel = modelService.get(ConsultantPK);

			// Ensure newly created consultants are initially set as per config
			consultantModel.setApprovalStatus(ArticleApprovalStatus.valueOf(Config.getParameter(CONSULTANT_APPROVAL_KEY)));
			modelService.save(consultantModel);
		}
	}
}
