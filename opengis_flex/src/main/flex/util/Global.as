package util
{
	import flash.net.*;
	import flash.utils.Dictionary;
	
	import mx.controls.*;
	import mx.core.Application;
	import mx.messaging.ChannelSet;
	import mx.messaging.channels.AMFChannel;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;
	public class Global
	{
		public static const appBlazeUri:String = "/dhccgis/messagebroker/amf";

        //返回远程对象
        public static function getRemoteObject(destination:String,resultHandler:Function=null,rfaultHandler:Function=null):RemoteObject{
                var service:ChannelSet = new ChannelSet();
                service.addChannel(new AMFChannel("amf",appBlazeUri));
                var ro:RemoteObject = new RemoteObject();
                ro.destination = destination;
                ro.channelSet = service;
                if(resultHandler != null){
                        ro.addEventListener(ResultEvent.RESULT,resultHandler);
                }
                if(rfaultHandler != null){
                        ro.addEventListener(FaultEvent.FAULT,rfaultHandler);
                }else{
                        ro.addEventListener(FaultEvent.FAULT,faultHandler);
                }
                return ro;
        }

        public static function faultHandler(event:FaultEvent):void{
                Alert.show("服务发生错误，请稍后再试!\n错误原因:"+event.fault.faultString,"错误",1);
        }
    }
}
