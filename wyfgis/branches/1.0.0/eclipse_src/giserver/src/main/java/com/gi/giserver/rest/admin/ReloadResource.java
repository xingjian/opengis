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
import com.gi.giserver.rest.html.AdministratorHTML;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/reload")
public class ReloadResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("f") @DefaultValue("html") String f, @QueryParam("service") String service,
			@QueryParam("username") String username, @QueryParam("password") String password) {
		return result(f, service, username, password);
	}

	@POST
	public String postResult(@FormParam("f") @DefaultValue("html") String f, @FormParam("service") String service,
			@FormParam("username") String username, @FormParam("password") String password) {
		return result(f, service, username, password);
	}

	private String result(String f, String service, String username, String password) {
		String result = null;

		try {
			Administrator admin = new Administrator(username, password);
			boolean isSucceed = false;

			try {
				String[] str = service.split(":");
				if ("map".equals(str[0].toLowerCase())) {
					isSucceed = admin.reloadMapService(str[1]);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if ("html".equals(f)) {
				result = this.generateHTMLResult(admin, service, isSucceed);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(Administrator admin, String service, boolean isSucceed) {
		String result = "";

		String contextRoot = context.getContextPath();
		String rootURI = contextRoot + "/rest/admin/";
		String thisURI = rootURI + "reload";

		String restBody = "";
		if (!admin.isAuthorized()) {
			restBody = AdministratorHTML.getLoginForm();
			restBody = restBody.replace("${POST_URI}", thisURI);
		} else {
			String[] str = service.split(":");
			if (isSucceed) {
				restBody += "<b>Succeed reload " + str[0] + " service '" + str[1] + "' !</b>";
			} else {
				restBody += "<b>Reload " + str[0] + " service '" + str[1] + "' failed !</b>";
			}
		}

		result += AdministratorHTML.getHeader(contextRoot).replace("${TITLE}", "Reload");
		result += AdministratorHTML.getNav(contextRoot).replace("${CATALOG}", " &gt; Reload").replace("${HOME}",
				rootURI + "?username=" + admin.getUsername() + "&password=" + admin.getPassword());
		result += AdministratorHTML.getH2().replace("${TITLE}", "Reload");
		result += AdministratorHTML.getRestBody().replace("${RESTBODY}", restBody);
		result += AdministratorHTML.getFooter();

		return result;
	}

}
