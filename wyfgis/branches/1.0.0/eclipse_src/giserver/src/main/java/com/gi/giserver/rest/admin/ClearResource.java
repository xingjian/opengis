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
@Path("/clear")
public class ClearResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("f") @DefaultValue("html") String f, @QueryParam("username") String username,
			@QueryParam("password") String password) {
		return result(f, username, password);
	}

	@POST
	public String postResult(@FormParam("f") @DefaultValue("html") String f, @FormParam("username") String username,
			@FormParam("password") String password) {
		return result(f, username, password);
	}

	private String result(String f, String username, String password) {
		String result = null;

		try {
			Administrator admin = new Administrator(username, password);
			admin.clearServer();

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

		String contextRoot = context.getContextPath();
		String rootURI = contextRoot + "/rest/admin/";
		String thisURI = rootURI + "clear";

		String restBody = "";
		if (!admin.isAuthorized()) {
			restBody = AdministratorHTML.getLoginForm();
			restBody = restBody.replace("${POST_URI}", thisURI);
		} else {
			restBody += "<b>Succeed clear not used resource !</b>";
		}

		result += AdministratorHTML.getHeader(contextRoot).replace("${TITLE}", "Clear");
		result += AdministratorHTML.getNav(contextRoot).replace("${CATALOG}", " &gt; Clear").replace("${HOME}",
				rootURI + "?username=" + admin.getUsername() + "&password=" + admin.getPassword());
		result += AdministratorHTML.getH2().replace("${TITLE}", "Clear");
		result += AdministratorHTML.getRestBody().replace("${RESTBODY}", restBody);
		result += AdministratorHTML.getFooter();

		return result;
	}

}
