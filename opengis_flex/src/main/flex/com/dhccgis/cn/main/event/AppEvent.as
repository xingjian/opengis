package com.dhccgis.cn.main.event
{
	import flash.events.Event;
	
	/**   
	 * 类名: AppEvent.as 
	 * 包名: com.dhccgis.cn.main.event 
	 * 功能: 应用事件
	 * 作者: promisePB xingjian@yeah.net   
	 * 日期: 2011-1-4 下午01:37:47 
	 * 版本: V1.0   
	 */
	public class AppEvent extends Event
	{
		//事件携带数据		
		private var _data:Object;
		
		//构造函数
		public function AppEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false, data:Object=null)
		{
			if (data != null) _data = data;
			super(type, bubbles, cancelable);
		}
		
		public function get data():Object
		{
			return _data;
		} 
		
		public function set data(data:Object):void
		{
			_data = data;
		}
	}
}