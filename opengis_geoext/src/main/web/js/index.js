Ext.BLANK_IMAGE_URL = 'js/ext/resources/images/default/s.gif';
//刷新验证码函数
function reloadcode(){
    var verify = document.getElementById('safecode');
    verify.setAttribute('src', 'page/validate_code.jsp?'+Math.random());
}

Ext.onReady(function(){
	Ext.QuickTips.init();
	//设置页面布局
	var viewPort = new Ext.Viewport({
			layout:'border',
			items:[{
				id:'centerViewPort',
				region:'center',
				layout:'fit',
				border:false,
				contentEl:'centerDiv'
 				},{
				region:'north',
				contentEl:'head',
				height:70
			},{
				region:'south',
				contentEl:'foot'
			}]
	});
	
	var loginForm = new Ext.form.FormPanel({
		defaultType:'textfield',
		labelAlign:'right',
		frame:true,
		height:140,
		layout:'form',
		bodyStyle:'padding:15px 30px 5px 30px;text-align: center;',
		items:[{name:'user.name',fieldLabel:'用户名',style:'width:180;',allowBlank: false,blankText: '用户名不能为空!'},
			{name:'user.pwd',fieldLabel:'密    码',style:'width:180;',inputType:'password',allowBlank: false,blankText: '密码不能为空!'},
		{cls:'key',name:'randCode', id:'randCode', fieldLabel:'验证码',maxLength: 4,width: 100,allowBlank: false,blankText: '验证码不能为空!'
		,maxLengthText: '验证码不能超过4个字符！'}],
		buttonAlign:'center',
		buttons:[{text:'登录',handler:function(){
			if(loginForm.getForm().isValid()){ 
				
				 loginForm.form.doAction('submit',{
				 	url:'login.action',
				 	method:'post',
				 	waitTitle:"请稍候",   
                    waitMsg:"正在提交登录信息数据，请稍候...",   
				 	success:function(form,action){ 
				 		win.close();
				 		wrc = Ext.getCmp('centerViewPort');
						wrc.removeAll();
						mainPn =  Ext.getCmp('mainPanel');
						wrc.add(mainPn);
						wrc.doLayout();
	                },   
                    failure:function(form,action){  
                      Ext.Msg.alert('系统提示','登录失败，请检查登录信息！');
                    } 
				 	
				 	
				 });
			}
		}},{text:'注册'},
		{text:'重置',handler: function(){    
               loginForm.getForm().reset();    
           }}]
	});
	
	var win = new Ext.Window({
		title:'登录窗口',
		layout:'table',
		width:450,
		height:250,
		closeAction:'hide',
		defaults:{
		},
		layoutConfig:{
			columns:1
		},
		items:[
			{
			height:80,
			width:437,
			contentEl:'login_top'
			},loginForm
		]

	});
	win.show();
	var bd = Ext.getDom('randCode');     
    var bd2 = Ext.get(bd.parentNode);
    bd2.createChild([{
	      tag: 'span',
	      html: '&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<a href="javascript:reloadcode();">'
	      }, {
	      tag: 'img',
	      id: 'safecode',
	      src: 'page/validate_code.jsp',
	      align: 'absbottom'
	      }]);
});