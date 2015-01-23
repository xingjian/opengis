package com.gi.giserver.core.util.error;

public enum ServiceError {

	// Authorization error
	NO_EDITOR_AUTHORIZATION,
	
	// Geometry Service error
	GEOMETRY_NO_RESULT_ERROR,
	
	// Map Service error
	MAP_EXPORT_ERROR,
	MAP_FIND_ERROR,
	MAP_IDENTIFY_ERROR,
	MAP_QUERY_LAYER_ERROR,
	
	// Map Service edit error
	MAP_INSERT_FEATURES_ERROR,
	MAP_DELETE_FEATURES_ERROR,
	MAP_MODIFY_FEATURES_ERROR	,	
	
	UNKNOWN
}
