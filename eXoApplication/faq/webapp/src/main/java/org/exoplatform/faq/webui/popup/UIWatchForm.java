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
package org.exoplatform.faq.webui.popup;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.faq.service.FAQService;
import org.exoplatform.faq.webui.FAQUtils;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormMultiValueInputSet;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTextAreaInput;

@SuppressWarnings({ "unused", "unchecked" })
@ComponentConfig(
		lifecycle = UIFormLifecycle.class ,
		template =	"app:/templates/faq/webui/popup/UIWatchForm.gtmpl",
		events = {
				@EventConfig(listeners = UIWatchForm.SaveActionListener.class),
				@EventConfig(listeners = UIWatchForm.CancelActionListener.class)
		}
)
public class UIWatchForm extends UIForm	implements UIPopupComponent{
	private UIFormMultiValueInputSet uiFormMultiValue = new UIFormMultiValueInputSet(FIELD_OPTIONS,FIELD_OPTIONS) ;
	public static final String USER_NAME = "userName" ; 
	public static final String EMAIL_ADDRESS = "emailAddress" ;
	private String categoryId_ = "";
	public UIWatchForm() throws Exception {}
  public void init() throws Exception {
  	UIFormStringInput userName = new UIFormStringInput(USER_NAME, USER_NAME, null);
  	UIFormTextAreaInput listEmail = new UIFormTextAreaInput(EMAIL_ADDRESS, null, null );
  	addUIFormInput(userName);
		addUIFormInput(listEmail);
  }
	
	public String[] getActions() { return new String[] {"Save","Cancel"} ; }
  public void activate() throws Exception {}
  public void deActivate() throws Exception {}
  
  public String getCategoryID() { return categoryId_; }
  public void setCategoryID(String s) { categoryId_ = s ; }
  
	static public class SaveActionListener extends EventListener<UIWatchForm> {
    public void execute(Event<UIWatchForm> event) throws Exception {
			UIWatchForm uiWatchForm = event.getSource() ;
			UIApplication uiApp = uiWatchForm.getAncestorOfType(UIApplication.class) ;
      String name = uiWatchForm.getUIStringInput(UIWatchForm.USER_NAME).getValue() ;
      String listEmail = uiWatchForm.getUIFormTextAreaInput(UIWatchForm.EMAIL_ADDRESS).getValue() ;
      if (FAQUtils.isFieldEmpty(listEmail)) {
        uiApp.addMessage(new ApplicationMessage("UIWatchForm.msg.to-field-empty", null)) ;
        event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
        return ;
      } else if (!FAQUtils.isValidEmailAddresses(listEmail)) {
        uiApp.addMessage(new ApplicationMessage("UIWatchForm.msg.invalid-to-field", null)) ;
        event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
        return ;
      }
      String categoryId = uiWatchForm.getCategoryID() ;
      String watchId = categoryId.substring(0, 4) ;
      if (categoryId != null) {
      	FAQService faqService =	(FAQService)PortalContainer.getInstance().getComponentInstanceOfType(FAQService.class) ;
      	if(watchId.equals("Cate")) {
      		faqService.addWatch(1, 1, categoryId , listEmail, FAQUtils.getSystemProvider()) ;
      	} else {
      		faqService.addWatch(2, 1, categoryId , listEmail, FAQUtils.getSystemProvider()) ;
      	}	
      	uiApp.addMessage(new ApplicationMessage("UIWatchForm.msg.successful", null,
      			ApplicationMessage.INFO)) ;
       	 event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
      }
      UIPopupAction uiPopupAction = uiWatchForm.getAncestorOfType(UIPopupAction.class) ;
      uiPopupAction.deActivate() ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPopupAction) ; 
      return ;
		}
	}

	static public class CancelActionListener extends EventListener<UIWatchForm> {
    public void execute(Event<UIWatchForm> event) throws Exception {
			UIWatchForm uiWatchForm = event.getSource() ;						
      UIPopupAction uiPopupAction = uiWatchForm.getAncestorOfType(UIPopupAction.class) ;
      uiPopupAction.deActivate() ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPopupAction) ;
		}
	}
	
	
	
}