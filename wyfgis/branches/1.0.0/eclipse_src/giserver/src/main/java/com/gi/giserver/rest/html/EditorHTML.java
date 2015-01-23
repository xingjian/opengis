package com.gi.giserver.rest.html;

public class EditorHTML {

	static public String getLoginForm() {
		StringBuilder sb = new StringBuilder();
		sb.append("<form action='${POST_URI}' method='post'><table id='authLoginTable'><tbody>");
		sb.append("<tr><td colspan='2'><b>Login</b></td></tr>");
		sb.append("<tr><td>User Name</td><td><input type='text' name='username' /></td></tr>");
		sb.append("<tr><td>Password</td><td><input type='password' name='password' /></td></tr>");
		sb.append("<tr><td colspan='2'><input type='submit' value='Login' /></td></tr>");
		sb.append("</tbody></table></form>");

		return sb.toString();
	}

}
