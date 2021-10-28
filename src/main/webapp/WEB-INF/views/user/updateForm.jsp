<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="../layout/header.jsp" %>



<div class="container">
	<form>
		<div class="form-group">
			<input type="text" value="${sessionScope.principal.username }" id="username" class="form-control" placeholder="Enter username" maxlength="20" required="required" readonly>
		</div>
		<div class="form-group">
			<input type="password" value="${sessionScope.principal.password }" id="password" class="form-control"	placeholder="Enter password" maxlength="20" >
		</div>
		<div class="form-group">
			<input type="email" value="${sessionScope.principal.email }" id="email" class="form-control"	placeholder="Enter email" required="required">
		</div>
		<button type="submit" class="btn btn-primary">회원수정</button>
	</form>
</div>

<script>
	

</script>

<%@ include file="../layout/footer.jsp" %>



