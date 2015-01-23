package com.gi.server.rest.admin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.gi.engine.util.common.PathUtil;
import com.gi.server.core.auth.Administrator;
import com.gi.server.core.config.ConfigManager;
import com.gi.server.core.service.ServiceManager;
import com.gi.server.core.service.mapservice.MapService;
import com.gi.server.core.service.mapservice.MapServicePool;
import com.gi.server.rest.html.AdministratorHTML;

/**
 * @author wuyf
 * 
 */
@Path("")
@Produces( { MediaType.TEXT_HTML })
public class Resource {

	@Context
	ServletContext context;

	@GET
	public Response getResult(@QueryParam("f") @DefaultValue("html") String f,
			@CookieParam("username") String username,
			@CookieParam("password") String password) {
		return result(f, username, password);
	}

	@POST
	public Response postResult(@FormParam("f") @DefaultValue("html") String f,
			@FormParam("username") String username,
			@FormParam("password") String password) {
		return result(f, username, password);
	}

	private Response result(String f, String username, String password) {
		Response result = null;

		try {
			Administrator admin = new Administrator(username, password);

			ResponseBuilder rb = null;
			if ("html".equals(f)) {
				rb = Response.ok(generateHTMLResult(admin));

				if (admin.isAuthorized()) {
					int maxAge = 60 * ConfigManager.getServerConfig()
							.getAdminCookieMaxageMinutes();
					NewCookie cookieUsername = new NewCookie("username", admin
							.getUsername(), null, null, null, maxAge, false);
					NewCookie cookiePassword = new NewCookie("password", admin
							.getPassword(), null, null, null, maxAge, false);
					rb.cookie(cookieUsername, cookiePassword);
				}
			}

			result = rb.build();
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

				HashMap<String, MapService> mapServices = ServiceManager
						.getMapServices();
				restBody += "<b>Manage Map Services:</b><br/>";
				restBody += "["
						+ PathUtil.realPathToFake(ServiceManager
								.getMapServicesDir().getAbsolutePath()) + "]";
				restBody += "<ul>";
				for (Iterator<Entry<String, MapService>> itr = mapServices
						.entrySet().iterator(); itr.hasNext();) {
					Entry<String, MapService> entry = itr.next();
					String name = entry.getKey();
					MapService mapService = entry.getValue();
					restBody += "<li>";
					restBody += "&nbsp;&nbsp;<b>" + name + "</b>";
					if (mapService.isStarted()) {
						MapServicePool pool = mapService.getMapServicePool();
						restBody += "&nbsp;&nbsp;Running("
								+ pool.getWorkingInstanceCount() + ")";
						restBody += "&nbsp;&nbsp;Totle("
								+ (pool.getWorkingInstanceCount() + pool
										.getIdleInstanceCount()) + ")";
						restBody += "&nbsp;&nbsp;Max(" + pool.getMaxInstances()
								+ ")";
						restBody += "&nbsp;&nbsp;[<a href='" + thisURI
								+ "stop?service=map>" + name + "'>Stop</a>]";
						restBody += "&nbsp;&nbsp;[<a href='" + thisURI
								+ "reload?service=map>" + name
								+ "'>Reload</a>]";
						long startupSeconds = pool.getStartupTime() / 1000;
						long s = startupSeconds;
						long m = 0;
						long h = 0;
						long d = 0;
						if (s >= 60) {
							m = s / 60;
							s = s % 60;
						}
						if (m >= 60) {
							h = m / 60;
							m = m % 60;
						}
						if (h >= 24) {
							d = h / 24;
							h = h % 24;
						}
						restBody += "&nbsp;&nbsp;";
						if (d > 0) {
							restBody += "&nbsp;" + d + " day";
						}
						if (h > 0) {
							restBody += "&nbsp;" + h + " hrs";
						}
						if (m > 0) {
							restBody += "&nbsp;" + m + " min";
						}
						if (s > 0) {
							restBody += "&nbsp;" + s + " sec";
						}
					} else {
						restBody += "&nbsp;&nbsp;[<a href='" + thisURI
								+ "start?service=map>" + name + "'>Start</a>]";
					}
					restBody += "</li>";
				}
				restBody += "</ul>";
				restBody += "<br/><br/>";

				restBody += "<br/><br/><b>Server Operations:</b>";
				restBody += "&nbsp;&nbsp;<a href='" + thisURI
						+ "clear'>Clear</a>";
				restBody += "&nbsp;&nbsp;<a href='" + thisURI
						+ "reconfig'>Reload Configurations</a>";
			}

			AdministratorHTML html = new AdministratorHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("Administrator Home");
			html.setHome(thisURI);
			html.setCatalog("");
			html.setHeader("Home");
			html.setRestBody(restBody);
			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
