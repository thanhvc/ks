/***************************************************************************
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
 ***************************************************************************/
package org.exoplatform.faq.webui;

import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;

import org.exoplatform.faq.service.FAQSetting;
import org.exoplatform.faq.webui.popup.UISettingForm;
import org.exoplatform.ks.common.webui.UIPopupAction;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPopupMessages;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * Author : Hung Nguyen Quang
 *          hung.nguyen@exoplatform.com
 * Mar 04, 2008
 */

@ComponentConfig(
   lifecycle = UIApplicationLifecycle.class,
   template = "app:/templates/faq/webui/UIAnswersPortlet.gtmpl"
)
public class UIAnswersPortlet extends UIPortletApplication {
	private boolean isFirstTime = true;
  public UIAnswersPortlet() throws Exception {
  	addChild(UIAnswersContainer.class, null, null) ;
  	UIPopupAction uiPopup =  addChild(UIPopupAction.class, null, null) ;
    uiPopup.setId("UIAnswersPopupAction") ;
    uiPopup.getChild(UIPopupWindow.class).setId("UIAnswersPopupWindow");
  }
  
  public void processRender(WebuiApplication app, WebuiRequestContext context) throws Exception {    
    PortletRequestContext portletReqContext = (PortletRequestContext)  context ;
    if(portletReqContext.getApplicationMode() == PortletMode.VIEW) {
    	isFirstTime = true;
    	if(getChild(UIAnswersContainer.class) == null){
    		if(getChild(UISettingForm.class) != null) {
    			removeChild(UISettingForm.class);
    		}
	    	addChild(UIAnswersContainer.class, null, null) ;
    	} 
    }else if(portletReqContext.getApplicationMode() == PortletMode.EDIT) {
    	try{
    		if(isFirstTime){
    			isFirstTime = false;
		    	UIQuestions questions = getChild(UIAnswersContainer.class).getChild(UIQuestions.class);
		  		FAQSetting faqSetting = questions.getFAQSetting();
		    	if(getChild(UISettingForm.class) == null) {
		    		if(faqSetting.isAdmin()){
			    		removeChild(UIAnswersContainer.class);
				    	UISettingForm settingForm = addChild(UISettingForm.class, null, "FAQPortletSetting");
				    	settingForm.setRendered(true);
				    	settingForm.setIsEditPortlet(true);
				    	settingForm.init();
			    	}
		    	}
    		}
    	} catch (Exception e) { e.printStackTrace();}
    }
    
    super.processRender(app, context) ;
  }
  
  public void renderPopupMessages() throws Exception {
    UIPopupMessages popupMess = getUIPopupMessages();
    if(popupMess == null)  return ;
    WebuiRequestContext  context =  WebuiRequestContext.getCurrentInstance() ;
    popupMess.processRender(context);
  }
  
  public void cancelAction() throws Exception {
		WebuiRequestContext context = RequestContext.getCurrentInstance() ;
		UIPopupAction popupAction = getChild(UIPopupAction.class) ;
		popupAction.deActivate() ;
		context.addUIComponentToUpdateByAjax(popupAction) ;
	}
  
  public static String getPreferenceDisplay() {
    PortletRequestContext pcontext = (PortletRequestContext)WebuiRequestContext.getCurrentInstance() ;
    PortletPreferences portletPref = pcontext.getRequest().getPreferences() ;
    String repository = portletPref.getValue("display", "") ;
    return repository ;
  }
} 
