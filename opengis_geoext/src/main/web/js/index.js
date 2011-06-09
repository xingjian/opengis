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
				region:'center',
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
				 	success:function(form,action){
				 		window.location.href="testpage/geoext_test.jsp";
				 	}
				 });
				 //直接跳转
				/**window.location.href="testpage/geoext_test.jsp";*/
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