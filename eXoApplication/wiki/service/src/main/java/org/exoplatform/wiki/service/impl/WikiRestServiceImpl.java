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
package org.exoplatform.wiki.service.impl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.wiki.mow.api.Page;
import org.exoplatform.wiki.rendering.RenderingService;
import org.exoplatform.wiki.service.WikiRestService;
import org.exoplatform.wiki.service.WikiService;
import org.xwiki.rendering.syntax.Syntax;

/**
 * Created by The eXo Platform SAS
 * Author : viet nguyen
 *          viet.nguyen@exoplatform.com
 * Jun 20, 2010  
 */
@Path("/wiki")
public class WikiRestServiceImpl implements WikiRestService, ResourceContainer {

  private final WikiService      wikiService;

  private final RenderingService renderingService;

  private static Log             log = ExoLogger.getLogger(WikiRestService.class);

  private final CacheControl     cc;

  public WikiRestServiceImpl(WikiService wikiService, RenderingService renderingService) {
    this.wikiService = wikiService;
    this.renderingService = renderingService;
    cc = new CacheControl();
    cc.setNoCache(true);
    cc.setNoStore(true);
  }

  /**
   * {@inheritDoc}
   */
  @GET
  @Path("/{wikiType}/{wikiOwner:.+}/{pageId}/content/")
  @Produces(MediaType.TEXT_HTML)
  public Response getWikiPageContent(@PathParam("wikiType") String wikiType,
                                     @PathParam("wikiOwner") String wikiOwner,
                                     @PathParam("pageId") String pageId,
                                     @QueryParam("markup") boolean isMarkup) {
    String pageContent = "";
    String syntaxId = "";
    try {
      Page page = wikiService.getPageById(wikiType, wikiOwner, pageId);
      if (page != null) {
        pageContent = page.getContent().getText();
        syntaxId = page.getContent().getSyntax();
        syntaxId = (syntaxId != null) ? syntaxId : Syntax.XWIKI_2_0.toIdString();
      }
      if (!isMarkup) {
        pageContent = renderingService.render(pageContent, syntaxId);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Response.serverError().entity(e.getMessage()).cacheControl(cc).build();
    }
    return Response.ok(pageContent, MediaType.TEXT_HTML).cacheControl(cc).build();
  }

}
