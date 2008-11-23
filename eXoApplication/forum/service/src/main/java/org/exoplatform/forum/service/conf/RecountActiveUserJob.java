/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 **/
package org.exoplatform.forum.service.conf;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.exoplatform.commons.utils.ISO8601;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.forum.service.ForumService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class RecountActiveUserJob implements Job{
	private static Log log_ = ExoLogger.getLogger("job.RecordsJob");
	
	public RecountActiveUserJob() throws Exception {}
	
	@SuppressWarnings("deprecation")
  public void execute(JobExecutionContext context) throws JobExecutionException {
		try{
			ExoContainer exoContainer = ExoContainerContext.getCurrentContainer() ;
	    Object obj = exoContainer.getComponentInstanceOfType(ForumService.class) ;
	    if(obj != null) {
	    	ForumService forumService = (ForumService)obj ;
	    	JobDataMap jdatamap = context.getJobDetail().getJobDataMap();
		    String lastPost = jdatamap.getString("lastPost") ;
		    if(lastPost != null && lastPost.length() > 0) {
	    		int days = Integer.parseInt(lastPost) ;
	    		if(days > 0) {
	    			long oneDay = 86400000 ; //milliseconds of one day
	    			Calendar calendar = GregorianCalendar.getInstance() ;
	    			long currentDay = calendar.getTimeInMillis() ;
	    			currentDay = currentDay - (days * oneDay) ;
	    			calendar.setTimeInMillis(currentDay) ;
	    			SessionProvider sysProvider = SessionProvider.createSystemProvider();
	    			StringBuilder stringBuilder = new StringBuilder();
	    			stringBuilder.append("//element(*,exo:userProfile)[");
	    			stringBuilder.append("@exo:lastPostDate >= xs:dateTime('"+ISO8601.format(calendar)+"')]") ;
	    			forumService.evaluateActiveUsers(sysProvider, stringBuilder.toString()) ;
	    			if (log_.isDebugEnabled()) {
    		  		log_.debug("\n\n The RecoundActiveUserJob have been done");
    		  	}
	    		}
		    }
	    }	    
		}catch(NumberFormatException nfe) {
  		nfe.printStackTrace() ;
  	}catch(Exception e) {
  		e.printStackTrace() ;
		}	  
  }
}
