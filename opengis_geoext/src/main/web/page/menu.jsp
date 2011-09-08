<%@ page language="java" pageEncoding="UTF-8" %>
<html>
	<head>
	<script type="text/javascript">
		Ext.onReady(function() {
			var root = new Ext.tree.AsyncTreeNode({
				id : "source",
				text : "系统菜单",
				expanded : true,
				iconCls:'icon-docs',
				leaf : false
			});
			var load = new Ext.tree.TreeLoader({
				url : "menu.action",
				requestMethod : "post"
			});
			var tree = new Ext.tree.TreePanel({
				renderTo : "menuDiv",
				animate : true,
				rootVisible : false,
				autoScroll : true,
				autoHeight : true,
				border:false,
  				autoWidth:true,
				lines : true, 
				root : root,
				loader : load,
				listeners: {
				        "click": function(node, event) {
					            if(node.isLeaf()) {    
					                event.stopEvent();
					                alert(node.text);
					            } else {
					                event.stopEvent();
					            }
			  			}
				}
			});
	});
	</script>
	</head>
	<body>
		<div id="menuDiv"></div>
	</body>
</html>
