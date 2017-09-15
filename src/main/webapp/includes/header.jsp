<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav class="navbar navbar-default navbar-fixed-top">
	<div class="container-fluid">
		<div class="navbar-header">
			<button type="button" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar" class="navbar-toggle collapsed">
				<span class="sr-only">Toggle navigation</span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>
			<a href="/" class="navbar-brand">BNADE</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<c:if test="${!sessionScope.user.isVip}">
				<ul class="nav navbar-nav navbar-right">
					<li><a href="/itemQuery.jsp">物价查询</a></li>
					<li><a href="/auctionQuantity.jsp">服务器排行</a></li>
					<li><a href="/itemTopSearch.jsp">物品搜索排行</a></li>
					<li><a href="/itemMarket.jsp">市场价排行</a></li>
					<li><a href="/topOwner.jsp">卖家排行</a></li>
					<li><a href="/download.jsp">下载</a></li>
					<li><a href="/ownerQuery.jsp">玩家物品</a></li>
					<li><a href="/page/auction/s810101">101圣物</a></li>
					<li><a href="/setting.jsp">设置</a></li>
					<c:if test="${empty sessionScope.user}">
						<li><a href="/page/user/login">登录</a></li>
					</c:if>
					<c:if test="${!empty sessionScope.user}">
						<li class="dropdown">
							<a class="dropdown-toggle" href="#" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">${sessionScope.user.nickname}<span class="caret"></span></a>
							<ul class="dropdown-menu">
								<li class="divider"></li>
								<li><a href="/page/user/signOut">退出</a></li>
							</ul>
						</li>
					</c:if>
				</ul>
			</c:if>
			<c:if test="${sessionScope.user.isVip}">
				<ul class="nav navbar-nav navbar-right">
					<li><a href="/itemQuery.jsp">物价查询</a></li>
					<li><a href="/auctionQuantity.jsp">服务器排行</a></li>
					<li><a href="/itemTopSearch.jsp">物品搜索排行</a></li>
					<li><a href="/itemMarket.jsp">市场价排行</a></li>
					<li><a href="/topOwner.jsp">卖家排行</a></li>
					<li><a href="/download.jsp">下载</a></li>
					<li><a href="/ownerQuery.jsp">玩家物品</a></li>
					<li><a href="/page/auction/s810101">101圣物</a></li>
					<li><a href="/setting.jsp">设置</a></li>
					<li class="dropdown">
						<a class="dropdown-toggle" href="#" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">${sessionScope.user.nickname}<span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="/page/user/info">我的信息</a></li>
							<li><a href="/page/user/mail">修改邮箱</a></li>
							<li><a href="/page/user/realm">我的服务器</a></li>
							<li><a href="/page/user/character">我的角色</a></li>
							<li><a href="/page/user/itemNotification">我的物品提醒</a></li>
							<li class="divider"></li>
							<li><a href="/page/user/signOut">退出</a></li>
						</ul>
					</li>
				</ul>
			</c:if>
		</div>
	</div>
</nav>