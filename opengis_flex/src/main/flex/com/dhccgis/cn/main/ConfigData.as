package com.dhccgis.cn.main
{
	/**
	 * 类名：ConfigData.as
	 * 功能：该类保存初始化加载配置地图的数据，由ConfigManager来初始化，非专业开发人员，严禁自定义该对象
	 * 包名：com.dhcc.cn.main
	 * 作者：邢健  xingjian@dhcc.com.cn
	 * 日期：2011年1月4日
	 * 版本：V1.0
	 **/
	public class ConfigData{
		
		public var configBasemaps:Array;
		
		public function ConfigData()
		{
			configBasemaps = [];
		}
	}
}	