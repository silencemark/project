<%@ page language="java" errorPage="/pages/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>


<!DOCTYPE HTML>
<!--[if lt IE 7 ]><html class="ie6 ieOld"><![endif]-->
<!--[if IE 7 ]><html class="ie7 ieOld"><![endif]-->
<!--[if IE 8 ]><html class="ie8 ieOld"><![endif]-->
<!--[if (gte IE 9)|!(IE)]><!--><html><!--<![endif]-->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>首页</title>
<meta name="Keywords" content="">
<meta name="Description" content="">
<link href="<%=request.getContextPath() %>/js/weixin/style/reset.css" rel="stylesheet" type="text/css" />
<link href="<%=request.getContextPath() %>/js/weixin/style/base.css" rel="stylesheet" type="text/css" />
<link href="<%=request.getContextPath() %>/js/weixin/style/page.css" rel="stylesheet" type="text/css" />
<jsp:include page="../../include/header.jsp" flush="true"></jsp:include>
</head>

<body class="p-audit p-resource">
	<div class="wrapper">
		<jsp:include page="../../include/top.jsp" flush="true"></jsp:include>
		<jsp:include page="../../include/left.jsp" flush="true"></jsp:include>
	
	<div class="content">
		<div class="crumb">
            <h5>系统设置 &#187; 添加多图文</h5>
        </div>
        

	<!--container-->
	<div class="webContainer clearFix containerWcMulitArticleAsset" id="webContainer">

		
		<form action="<%=request.getContextPath() %>/m/wxset!saveDTImgTextMultiForm.action"  
			method="post"	id="jsonform" style="display: block;">
			<input type="hidden" id="multiGraphic" name="multiGraphic"/>
		</form>
		<!--main-->
		<div class="webMain">
			<div class="mainHeader">
				<h1>添加多图文消息</h1>
			</div>
			<div class="mainBody" id="wechatMulitAsset">

				<div class="item itemMulit" id="itemMulit"></div>

				<div class="form">
					<form>
						<input type="hidden" id="imgtextid">
						<input type="hidden" id="imgtextlistid">
						<fieldset>
							<div class="l">
								<h6>标题</h6>
								<div class="i"><input type="text" class="input" id="title" name="paramCondition.title" /></div>
							</div>
							<div class="l">
								<h6>作者<span>（选填）</span></h6>
								<div class="i"><input type="text" class="input" id="author" name="paramCondition.author" /></div>
							</div>
							<div class="l cover">
								<h6>封面<span class="up1">（大图片建议尺寸：600像素 * 500像素）</span><span class="up2" style="display:none">（小图片建议尺寸：200像素 * 200像素）</span></h6>
								<div class="i">
									<div class="up"><input type="text" class="js_imgupload" name="paramCondition.imgurl" id="imgurl" maxWidth="600" onchange="$.__mulitArticleAssetImgChange__" /></div>
									<div class="cb clearFix"><input type="checkbox" name="paramCondition.ifviewcontent"  data-val="封面图片显示在正文中"  id="ifviewcontent" /></div>
								</div>
							</div>
							<%--
							<div class="l">
								<h6>摘要</h6>
								<div class="i"><textarea class="input" id="summary" name="paramCondition.summary" ></textarea></div>
							</div>
							 --%>
							<div class="l">
								<h6>正文</h6>
								<div class="i" style="width:501px;"><script id="content" type="text/plain" name="paramCondition.content"></script></div>
								<span id="mycontent" style="display:none;"></span>
							</div>
							<div class="l">
								<h6>原文链接<span>（选填）</span></h6>
								<div class="i"><input type="text" class="input" id="linkurl" name="paramCondition.linkurl"/></div>
							</div>
						</fieldset>
					</form>
					<div class="point"></div>
				</div>

				<div class="btnCon btnCon2">
					<a href="#" class="btnD">取消</a>
					<input type="button" id="submit" class="btnA" value="提交" />
				</div>
				
			</div>
		</div>
		<!--main end-->

	</div>
	<!--container end-->

</div>	
	</div>
	
	<script>
	var __mulit = {
	<s:if test="#request.dataList == null or #request.dataList == null.size<=0">
		<%-- 新增 --%>
		cover: {
			imgtextid:"",
			imgtextlistid:"",
			title: "",
			author: "",
			imgurl: "",
			ifviewcontent: "1",
			content: "",
			linkurl: ""
		},
		sub: [{
			imgtextid:"",
			imgtextlistid:"",
			title: "",
			author: "",
			imgurl: "",
			ifviewcontent: "1",
			content: "",
			linkurl: ""
		}]
	</s:if>
	<s:else>
		<%-- 修改 --%>
		<s:iterator value="#request.dataList" status="st" id="item">
			<s:if test="#st.index==0">
				<%-- 第一项 --%>
				cover: {
					imgtextid:"<s:property value='#item.imgtextid'/>",
					imgtextlistid:"<s:property value='#item.imgtextlistid'/>",
					title: "<s:property value='#item.title' escape='false'/>",
					author: "<s:property value='#item.author' escape='false'/>",
					imgurl: "<s:property value='#item.imgurl'/>",
					ifviewcontent: "<s:property value='#item.ifviewcontent'/>",
					content: "<s:property value='#item.content' escape='false'/>",
					linkurl: "<s:property value='#item.linkurl' escape='false'/>"
				},
			</s:if>
			<s:else>
				<s:if test="#st.index==1">
					sub: [
				</s:if>
				 {
					imgtextid:"<s:property value='#item.imgtextid'/>",
					imgtextlistid:"<s:property value='#item.imgtextlistid'/>",
					title: "<s:property value='#item.title' escape='false'/>",
					author: "<s:property value='#item.author' escape='false'/>",
					imgurl: "<s:property value='#item.imgurl'/>",
					ifviewcontent: "<s:property value='#item.ifviewcontent'/>",
					content: "<s:property value='#item.content'  escape='false'/>",
					linkurl: "<s:property value='#item.linkurl' escape='false'/>"
				},
				<s:if test="#st.Last">
					]
				</s:if>
			</s:else>
		</s:iterator>
	</s:else>
	}
	//提交多图文
	function saveDTImgTextMulti(){
		$('#multiGraphic').val(JSON.stringify(__mulit));
		$('#jsonform').submit();
	}
	</script>

	<script src="<%=request.getContextPath()%>/js/weixin/js/gtyutil.js"></script>
	<script src="<%=request.getContextPath()%>/js/weixin/js/hhutil.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath() %>/js/weixin/js/jquery-1.8.0.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath() %>/js/weixin/js/swfobject.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath() %>/js/weixin/js/common.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath() %>/js/weixin/js/wechat/mulitArticleAsset.js"></script>

	<!--富文本编辑器-->
	<script type="text/javascript" charset="utf-8" src="<%=request.getContextPath() %>/ueditor/ueditor.config.js"></script>
    <script type="text/javascript" charset="utf-8" src="<%=request.getContextPath() %>/ueditor/ueditor.all.js"> </script>
    <script type="text/javascript" charset="utf-8" src="<%=request.getContextPath() %>/js/ueditor/lang/zh-cn/zh-cn.js"></script>
    <!--富文本编辑器 end-->
</body>
</html>