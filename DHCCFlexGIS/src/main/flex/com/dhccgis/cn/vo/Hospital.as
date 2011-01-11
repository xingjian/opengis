package com.dhccgis.cn.vo
{
	/**   
	 * 类名: Hospital.as 
	 * 包名: com.dhccgis.cn.vo 
	 * 功能: todo(用一句话描述该文件做什么) 
	 * 作者: promisePB xingjian@yeah.net   
	 * 日期: Jan 6, 2011 4:16:15 PM 
	 * 版本: V1.0   
	 */
	[Bindable]
	[RemoteClass(alias="com.dhccgis.cn.app.vo.Hospital")];
	public class Hospital
	{
		[Bindable]
		public var id:String;
		[Bindable]
		public var name:String;
		[Bindalbe]
		public var x:String;
		[Bindable]
		public var y:String;
		
		public function Hospital()
		{
		}
	}
}