/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.wiki.webui;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SAS
 * Author : viet nguyen
 *          viet.nguyen@exoplatform.com
 * Jul 13, 2010  
 */
@ComponentConfig(
  lifecycle = UIApplicationLifecycle.class,
  template = "app:/templates/wiki/webui/UIWikiHistorySpaceArea.gtmpl",
  events = {
    @EventConfig(listeners = UIWikiHistorySpaceArea.ReturnViewModeActionListener.class)
  }
)
public class UIWikiHistorySpaceArea extends UIContainer {

  public static final String RETURN_VIEW_MODE = "ReturnViewMode";

  public UIWikiHistorySpaceArea() throws Exception {
    addChild(UIWikiPageVersionsList.class, null, null).setRendered(true);
    addChild(UIWikiPageVersionsCompare.class, null, null).setRendered(false);
  }

  static public class ReturnViewModeActionListener extends EventListener<UIWikiHistorySpaceArea> {
    @Override
    public void execute(Event<UIWikiHistorySpaceArea> event) throws Exception {
      UIWikiPortlet wikiPortlet = event.getSource().getAncestorOfType(UIWikiPortlet.class);
      wikiPortlet.changeMode(WikiMode.VIEW);
    }
  }

}
