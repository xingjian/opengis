package com.gi.giserver.core.util.error;

public class ServiceErrorUtil {

	/**
	 * @param error
	 * @return Error code Sample: 91003 
	 * 9 - GIServer 
	 * 1 - Service type 
	 * 003 - Error detail
	 */
	public static String getErrorCode(ServiceError error) {
		String result = "90000";

		// Unknown error
		if (ServiceError.UNKNOWN.equals(error)) {
		}
		
		// Authorization error
		// 91xxx
		else if (ServiceError.NO_EDITOR_AUTHORIZATION.equals(error)) {
			result = "91001";
		}

		// Geometry service error
		// 92xxx
		else if (ServiceError.GEOMETRY_NO_RESULT_ERROR.equals(error)) {
			result = "92001";
		} 
		
		// Map service error
		// 93xxx
		else if (ServiceError.MAP_EXPORT_ERROR.equals(error)) {
			result = "93001";
		}else if (ServiceError.MAP_FIND_ERROR.equals(error)) {
			result = "93002";
		}else if (ServiceError.MAP_IDENTIFY_ERROR.equals(error)) {
			result = "93003";
		}else if (ServiceError.MAP_QUERY_LAYER_ERROR.equals(error)) {
			result = "93004";
		}

		// Map service edit error
		// 94xxx
		else if (ServiceError.MAP_INSERT_FEATURES_ERROR.equals(error)) {
			result = "94001";
		} else if (ServiceError.MAP_DELETE_FEATURES_ERROR.equals(error)) {
			result = "94002";
		} else if (ServiceError.MAP_MODIFY_FEATURES_ERROR.equals(error)) {
			result = "94003";
		}

		return result;
	}

	public static String getErrorMessage(ServiceError error) {
		String result = "Unknown error";

		// Unknown error
		if (ServiceError.UNKNOWN.equals(error)) {
		}
		
		// Authorization error
		else if (ServiceError.NO_EDITOR_AUTHORIZATION.equals(error)) {
			result = "No editor authorization";
		} 
		
		// Geometry service error
		else if (ServiceError.GEOMETRY_NO_RESULT_ERROR.equals(error)) {
			result = "No result of geometry operation";
		}

		// Map service error
		else if (ServiceError.MAP_EXPORT_ERROR.equals(error)) {
			result = "Export map error occured";
		}else if (ServiceError.MAP_FIND_ERROR.equals(error)) {
			result = "Find error occured";
		}else if (ServiceError.MAP_IDENTIFY_ERROR.equals(error)) {
			result = "Identify error occured";
		}else if (ServiceError.MAP_QUERY_LAYER_ERROR.equals(error)) {
			result = "Query layer error occured";
		}
		
		// Map service edit error		
		else if (ServiceError.MAP_INSERT_FEATURES_ERROR.equals(error)) {
			result = "Insert features error occured";
		} else if (ServiceError.MAP_DELETE_FEATURES_ERROR.equals(error)) {
			result = "Delete features error occured";
		} else if (ServiceError.MAP_MODIFY_FEATURES_ERROR.equals(error)) {
			result = "Modify features error occured";
		} 

		return result;
	}

}
