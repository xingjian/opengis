var accordion = new Ext.Panel({
    layout:'accordion',
    defaults: {
        bodyStyle: 'padding:0px'
    },
    layoutConfig: {
        titleCollapse: false,
        animate: true,
        activeOnTop: false
    },
    items: [{
        title: '开源GIS功能树',
        layout:'fit',
        autoLoad:{url:'page/menu.jsp',scripts:true}
    },{
        title: '系统管理',
        html: '<p>Panel content!</p>'
    },{
        title: '业务维护',
        html: '<p>Panel content!</p>'
    }]
});


var mainPanel = new Ext.Container({
	id:'mainPanel',
	layout:'border',
	border:false,
	items:[{
			region:'center',
			layout:'fit',
			autoLoad:{url:'page/centermap.jsp',scripts:true}
			},{
			collapsible:true,
			title:'开源GIS',
			layout:'fit',	
			region:'east',
			width:170
		},{
			region:'west',
			width:170,
			title:'菜单树',
			layout:'fit',
			collapsible:true,
			items:[accordion]
		}]
});