/**
 *
 */
package com.hybris.test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author Alan Liu
 *
 */
@IntegrationTest
public class InitialDataSetupTest extends ServicelayerTransactionalTest
{
	@Resource
	private CronJobService cronJobService;

	@Resource
	ModelService modelService;

	@Resource
	UserService userService;



	@BeforeClass
	public static void beforeClass()
	{
		Registry.setCurrentTenantByID("junit");
	}

	@Test
	public void testMediaConversionJob()
	{
		//
		// 11. Run media conversion on baseProductCatalog
		// Can't get this working but will post support q's
		/*
		 * final MediaConversionCronJobModel mediaConversionCronJob =
		 * modelService.create(MediaConversionCronJobModel.class); final JobModel jobModel =
		 * cronJobService.getJob("mediaConversionJob"); mediaConversionCronJob.setJob(jobModel);
		 * mediaConversionCronJob.setCode("MediaConversionCronjobConsult");
		 */
		//mediaConversionCronJob.setSessionUser(getUserService().getCurrentUser());
		//mediaConversionCronJob.setSessionLanguage(getUserService().getCurrentUser().getSessionLanguage());
		//mediaConversionCronJob.setSessionCurrency(getUserService().getCurrentUser().getSessionCurrency());

		/*
		 * modelService.save(mediaConversionCronJob); cronJobService.performCronJob(mediaConversionCronJob);
		 */
	}
}
