package com.gi.giserver.rest.admin;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import com.gi.giserver.core.auth.Administrator;
import com.gi.giserver.core.service.ServiceManager;
import com.gi.giserver.core.service.mapservice.MapService;
import com.gi.giserver.core.service.mapservice.MapServicePool;
import com.gi.giserver.rest.html.AdministratorHTML;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("")
public class Resource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("username") String username,
			@QueryParam("password") String password) {
		return result(f, username, password);
	}

	@POST
	public String postResult(@FormParam("f") @DefaultValue("html") String f,
			@FormParam("username") String username,
			@FormParam("password") String password) {
		return result(f, username, password);
	}

	private String result(String f, String username, String password) {
		String result = null;

		try {
			Administrator admin = new Administrator(username, password);

			if ("html".equals(f)) {
				result = this.generateHTMLResult(admin);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(Administrator admin) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();
			String rootURI = contextRoot + "/rest/admin/";
			String thisURI = rootURI;

			String restBody = "";
			if (!admin.isAuthorized()) {
				restBody = AdministratorHTML.getLoginForm();
				restBody = restBody.replace("${POST_URI}", thisURI);
			} else {
				restBody += AdministratorHTML.getServerInformation(context);

				MapService[] mapServices = ServiceManager.getMapServices();
				restBody += "<b>Manage Map Services:</b>";
				restBody += "<ul>";
				int count = mapServices.length;
				for (int i = 0; i < count; i++) {
					MapService mapService = mapServices[i];
					String mapName = mapService.getMapDesc().getMapName();
					restBody += "<li>";
					restBody += "&nbsp;&nbsp;<b>" + mapName + "</b>";
					if (mapService.isStarted()) {
						MapServicePool pool = mapService.getMapServicePool();
						restBody += "&nbsp;&nbsp;Running("
								+ pool.getRunningInstanceCount() + ")";
						restBody += "&nbsp;&nbsp;Totle("
								+ pool.getTotleInstanceCount() + ")";
						restBody += "&nbsp;&nbsp;Max("
								+ pool.getMaxInstanceCount() + ")";
						restBody += "&nbsp;&nbsp;[<a href='" + thisURI
								+ "stop?service=map:" + mapName + "&username="
								+ admin.getUsername() + "&password="
								+ admin.getPassword() + "'>Stop</a>]";
						restBody += "&nbsp;&nbsp;[<a href='" + thisURI
								+ "reload?service=map:" + mapName
								+ "&username=" + admin.getUsername()
								+ "&password=" + admin.getPassword()
								+ "'>Reload</a>]";
					} else {
						restBody += "&nbsp;&nbsp;[<a href='" + thisURI
								+ "start?service=map:" + mapName + "&username="
								+ admin.getUsername() + "&password="
								+ admin.getPassword() + "'>Start</a>]";
					}
					restBody += "</li>";
				}
				restBody += "</ul>";
				restBody += "<br/><br/>";

				restBody += "<br/><br/><b>Server Operations:</b>";
				restBody += "&nbsp;&nbsp;<a href='" + thisURI
						+ "clear?username=" + admin.getUsername()
						+ "&password=" + admin.getPassword() + "'>Clear</a>";
				restBody += "&nbsp;&nbsp;<a href='" + thisURI
						+ "reconfig?username=" + admin.getUsername()
						+ "&password=" + admin.getPassword()
						+ "'>Reload Configurations</a>";
			}

			result += AdministratorHTML.getHeader(contextRoot).replace(
					"${TITLE}", "Administrator Home");
			result += AdministratorHTML.getNav(contextRoot).replace(
					"${CATALOG}", "").replace(
					"${HOME}",
					thisURI + "?username=" + admin.getUsername() + "&password="
							+ admin.getPassword());
			result += AdministratorHTML.getH2().replace("${TITLE}", "Home");
			result += AdministratorHTML.getRestBody().replace("${RESTBODY}",
					restBody);
			result += AdministratorHTML.getFooter();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
