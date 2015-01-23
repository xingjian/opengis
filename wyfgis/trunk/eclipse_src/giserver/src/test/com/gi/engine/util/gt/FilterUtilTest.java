package com.gi.engine.util.gt;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.opengis.filter.Filter;

public class FilterUtilTest {

	@Test
	public void testWhereclauseToFilter() {
		String where = "NAME like '%北%' and ( OBJECTID>0 or NAME='たは' ) and NAME not like '上海' or NAME<>'사랑해'";
		Filter filter = FilterUtil.whereclauseToFilter(where);
		assertNotNull(filter);
	}

}
