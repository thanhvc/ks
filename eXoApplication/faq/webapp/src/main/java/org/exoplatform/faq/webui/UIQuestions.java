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

import org.exoplatform.faq.webui.popup.UICategoryForm;
import org.exoplatform.faq.webui.popup.UIPopupAction;
import org.exoplatform.faq.webui.popup.UIPopupActionContainer;
import org.exoplatform.forum.webui.popup.UIForumUserSettingForm;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Hung Nguyen
 *					hung.nguyen@exoplatform.com
 * Aus 01, 2007 2:48:18 PM 
 */
@ComponentConfig(
		template =	"app:/templates/faq/webui/UIQuestions.gtmpl" ,
		events = {
				@EventConfig(listeners = UIQuestions.AddCategoryActionListener.class),
	      @EventConfig(listeners = UIQuestions.AddQuestionActionListener.class)
		}
)
public class UIQuestions extends UIContainer {
	
	public UIQuestions()throws Exception {
		
	}
	
	static  public class AddCategoryActionListener extends EventListener<UIQuestions> {
    public void execute(Event<UIQuestions> event) throws Exception {
    	UIQuestions uiActionBar = event.getSource() ; 
    	System.out.println("\n\n==== AddCategoryActionListenerest");
      UIFAQPortlet uiPortlet = uiActionBar.getAncestorOfType(UIFAQPortlet.class);
      UIPopupAction popupAction = uiPortlet.getChild(UIPopupAction.class);
      UICategoryForm categoryForm = popupAction.createUIComponent(UICategoryForm.class, null, null) ;
			popupAction.activate(categoryForm, 510, 20) ;
			event.getRequestContext().addUIComponentToUpdateByAjax(popupAction) ;
  	}
  }
	
	static  public class AddQuestionActionListener extends EventListener<UIQuestions> {
    public void execute(Event<UIQuestions> event) throws Exception {
    	UIQuestions uiActionBar = event.getSource() ; 
    	System.out.println("\n\n==== AddQuestionActionListener");
    	}
    }
}