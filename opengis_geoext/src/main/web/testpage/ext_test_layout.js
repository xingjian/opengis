Ext.onReady(function(){
	
	//设置页面布局
	var viewPort = new Ext.Viewport({
			layout:'border',
			items:[{
				region:'center'
 				},{
				region:'north',
				height:40
			},{
				region:'south'
			}]
	});
	
	var form = new Ext.form.FormPanel({
		defaultType:'textfield',
		labelAlign:'right',
		frame:true,
		labelWidth:50,
		bodyStyle:'background-color: #DFE8F6; padding-top: 25px;border: 0px solid;',
		items:[{fieldLabel:'用户名'},{fieldLabel:'密    码',inputType:'password'}],
		buttons:[{text:'登录'},{text:'注册'}]
	});
	
	var win = new Ext.Window({
		title:'登录窗口',
		width:500,
		height:300,
		closeAction:'hide',
		layout:'fit',
		items:[
			{
				title:'table layout',
				layout:'table',
				defaults:{
					bodyStyle:'padding:20px'
				},
				layoutConfig:{
					columns:1
				},
				items:[form,
					{
						html:'form'
					},{
						html:'<p>cell a content</p>'
					}
				]
			}
		]
	});
	win.show();
});