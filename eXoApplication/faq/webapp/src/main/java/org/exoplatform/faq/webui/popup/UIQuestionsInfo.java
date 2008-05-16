/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
 */
package org.exoplatform.faq.webui.popup;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.faq.service.Category;
import org.exoplatform.faq.service.FAQService;
import org.exoplatform.faq.service.JCRPageList;
import org.exoplatform.faq.service.Question;
import org.exoplatform.faq.webui.FAQUtils;
import org.exoplatform.faq.webui.UIFAQPageIterator;
import org.exoplatform.faq.webui.UIFAQPortlet;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;

/**
 * Created by The eXo Platform SAS
 * Author : Mai Van Ha
 *          ha_mai_van@exoplatform.com
 * May 15, 2008 ,4:09:44 AM 
 */

@ComponentConfig(
    lifecycle = UIFormLifecycle.class ,
    template =  "app:/templates/faq/webui/popup/UIQuestionsInfo.gtmpl",
    events = {
      @EventConfig(listeners = UIQuestionsInfo.CloseActionListener.class),
      @EventConfig(listeners = UIQuestionsInfo.EditQuestionActionListener.class),
      @EventConfig(listeners = UIQuestionsInfo.DeleteQuestionActionListener.class),
      @EventConfig(listeners = UIQuestionsInfo.ChangeTabActionListener.class),
      @EventConfig(listeners = UIQuestionsInfo.ResponseQuestionActionListener.class)
    }
)

public class UIQuestionsInfo extends UIForm implements UIPopupComponent {
  private static final String LIST_QUESTION_INTERATOR = "FAQUserPageIteratorTab1" ;
  private static final String LIST_QUESTION_NOT_ANSWERED_INTERATOR = "FAQUserPageIteratorTab2" ;
  
  private static FAQService faqService_ =(FAQService)PortalContainer.getInstance().getComponentInstanceOfType(FAQService.class) ; 
  private JCRPageList pageList ;
  private JCRPageList pageListNotAnswer ;
  private UIFAQPageIterator pageIterator ;
  private UIFAQPageIterator pageQuesNotAnswerIterator ;
  private List<Question> listQuestion_ = new ArrayList<Question>() ;
  private List<Question> listQuestionNotYetAnswered_ = new ArrayList<Question>() ;
  private long pageSelect = 0 ;
  private long pageSelectNotAnswer = 0 ;
  
  private boolean isEditTab_ = true ;
  private boolean isResponseTab_ = false ;
  
  public void activate() throws Exception { }
  public void deActivate() throws Exception { }
  
  public UIQuestionsInfo() throws Exception {
    isEditTab_ = true ;
    isResponseTab_ = false ;
    addChild(UIFAQPageIterator.class, null, LIST_QUESTION_INTERATOR) ;
    addChild(UIFAQPageIterator.class, null, LIST_QUESTION_NOT_ANSWERED_INTERATOR) ;
    setListQuestion() ;
    setActions(new String[]{""}) ;
  }
  
  @SuppressWarnings("unused")
  private String[] getQuestionActions(){
    return new String[]{"AddLanguage", "Attachment", "Save", "Close"} ;
  }
  
  @SuppressWarnings("unused")
  private String[] getQuestionNotAnsweredActions() {
    return new String[]{"QuestionRelation", "Attachment", "Save", "Close"} ;
  }
  
  @SuppressWarnings("unused")
  private String[] getTab() {
    return new String[]{"Question managerment", "Question not yet answered"} ;
  }
  
  @SuppressWarnings("unused")
  private boolean getIsEdit(){
    return isEditTab_;
  }
  
  @SuppressWarnings("unused")
  private boolean getIsResponse() {
    return isResponseTab_ ;
  }
  
  private void setListQuestion() throws Exception {
    listQuestion_.clear() ;
    listQuestionNotYetAnswered_.clear() ;
    String user = FAQUtils.getCurrentUser() ;
    pageIterator = this.getChildById(LIST_QUESTION_INTERATOR) ;
    pageQuesNotAnswerIterator = this.getChildById(LIST_QUESTION_NOT_ANSWERED_INTERATOR) ;
    SessionProvider sProvider = FAQUtils.getSystemProvider() ;
    if(!user.equals("root")) {
      List<String> listCateId = new ArrayList<String>() ;
      listCateId.addAll(faqService_.getListCateIdByModerator(user, sProvider)) ;
      int i = 0 ;
      while(i < listCateId.size()) {
        for(Category category : faqService_.getSubCategories(listCateId.get(i), sProvider)) {
          if(!listCateId.contains(category.getId())) {
            listCateId.add(category.getId()) ;
          }
        }
        i ++ ;
      }
      if(!listCateId.isEmpty()) {
        this.pageList = faqService_.getQuestionsByListCatetory(listCateId, false, sProvider) ;
        this.pageList.setPageSize(5);
        pageIterator.updatePageList(this.pageList) ;
        
        this.pageListNotAnswer = faqService_.getQuestionsByListCatetory(listCateId, true, sProvider) ;
        this.pageListNotAnswer.setPageSize(5);
        pageQuesNotAnswerIterator.updatePageList(this.pageListNotAnswer) ;
      } else {
        this.pageList = null ;
        this.pageList.setPageSize(5);
        pageIterator.updatePageList(this.pageList) ;
        
        this.pageListNotAnswer = null ;
        this.pageListNotAnswer.setPageSize(5);
        pageQuesNotAnswerIterator.updatePageList(this.pageListNotAnswer) ;
      }
    } else {
      this.pageList = faqService_.getAllQuestions(sProvider) ;
      this.pageList.setPageSize(5);
      pageIterator.updatePageList(this.pageList) ;
      
      pageListNotAnswer = faqService_.getQuestionsNotYetAnswer(sProvider) ;
      pageListNotAnswer.setPageSize(5);
      pageQuesNotAnswerIterator.updatePageList(pageListNotAnswer) ;
    }
  }
  
  @SuppressWarnings("unused")
  private List<Question> getListQuestion() {
    pageSelect = pageIterator.getPageSelected() ;
    listQuestion_.clear() ;
    try {
      listQuestion_.addAll(this.pageList.getPage(pageSelect, null)) ;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return listQuestion_ ;
  }
  
  @SuppressWarnings("unused")
  private List<Question> getListQuestionNotAnswered() {
    pageSelectNotAnswer = pageQuesNotAnswerIterator.getPageSelected() ;
    listQuestionNotYetAnswered_.clear() ;
    try {
      listQuestionNotYetAnswered_.addAll(this.pageListNotAnswer.getPage(pageSelectNotAnswer, null)) ;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return listQuestionNotYetAnswered_ ;
  }
  
  static public class EditQuestionActionListener extends EventListener<UIQuestionsInfo> {
    public void execute(Event<UIQuestionsInfo> event) throws Exception {
      UIQuestionsInfo questionsInfo = event.getSource() ;
      String quesId = event.getRequestContext().getRequestParameter(OBJECTID) ;
      
      UIQuestionManagerForm questionManagerForm = questionsInfo.getAncestorOfType(UIQuestionManagerForm.class) ;
      questionManagerForm.isViewEditQuestion = true ;
      questionManagerForm.isViewResponseQuestion = false ;
      questionManagerForm.isEditQuestion = true ;
      UIQuestionForm questionForm = questionManagerForm.getChildById(questionManagerForm.UI_QUESTION_FORM) ;
      questionForm.setIsChildOfManager(true) ;
      questionForm.setQuestionId(quesId) ;
    }
  }
  
  static public class ResponseQuestionActionListener extends EventListener<UIQuestionsInfo> {
    public void execute(Event<UIQuestionsInfo> event) throws Exception {
      UIQuestionsInfo questionsInfo = event.getSource() ;
      String quesId = event.getRequestContext().getRequestParameter(OBJECTID) ;
      
      UIQuestionManagerForm questionManagerForm = questionsInfo.getAncestorOfType(UIQuestionManagerForm.class) ;
      questionManagerForm.isViewEditQuestion = false ;
      questionManagerForm.isViewResponseQuestion = true ;
      questionManagerForm.isResponseQuestion = true ;
      UIResponseForm responseForm = questionManagerForm.getChildById(questionManagerForm.UI_RESPONSE_FORM) ;
      responseForm.setIsChildren(true) ;
      responseForm.setQuestionId(quesId) ;
    }
  }
  
  static public class DeleteQuestionActionListener extends EventListener<UIQuestionsInfo> {
    public void execute(Event<UIQuestionsInfo> event) throws Exception {
      UIQuestionsInfo questionManagerForm = event.getSource() ;
      String questionId = event.getRequestContext().getRequestParameter(OBJECTID) ;
      UIPopupContainer popupContainer = questionManagerForm.getAncestorOfType(UIPopupContainer.class);
      UIPopupAction popupAction = popupContainer.getChild(UIPopupAction.class).setRendered(true) ;
      UIDeleteQuestion deleteQuestion = popupAction.activate(UIDeleteQuestion.class, 500) ;
      deleteQuestion.setIsManagement(true) ;
      deleteQuestion.setQuestionId(questionId) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(popupAction) ;
    }
  }
  
  static public class CloseActionListener extends EventListener<UIQuestionsInfo> {
    public void execute(Event<UIQuestionsInfo> event) throws Exception {
      UIQuestionsInfo questionManagerForm = event.getSource() ;
      UIFAQPortlet portlet = questionManagerForm.getAncestorOfType(UIFAQPortlet.class) ;
      UIPopupAction popupAction = portlet.getChild(UIPopupAction.class) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(popupAction) ;
    }
  }
  
  static public class ChangeTabActionListener extends EventListener<UIQuestionsInfo> {
    public void execute(Event<UIQuestionsInfo> event) throws Exception {
      UIQuestionsInfo questionsInfo = event.getSource() ;
      String idTab = event.getRequestContext().getRequestParameter(OBJECTID) ;
      if(idTab.equals("0")) {
        questionsInfo.isEditTab_ = true ;
        questionsInfo.isResponseTab_ = false ;
        
        UIQuestionManagerForm questionManagerForm = questionsInfo.getAncestorOfType(UIQuestionManagerForm.class) ;
        questionManagerForm.isViewEditQuestion = true ;
        questionManagerForm.isViewResponseQuestion = false ;
      } else {
        questionsInfo.isEditTab_ = false;
        questionsInfo.isResponseTab_ = true ;
        
        UIQuestionManagerForm questionManagerForm = questionsInfo.getAncestorOfType(UIQuestionManagerForm.class) ;
        questionManagerForm.isViewEditQuestion = false ;
        questionManagerForm.isViewResponseQuestion = true ;
      }
    }
  }
}
