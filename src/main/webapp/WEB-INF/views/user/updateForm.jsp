<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="../layout/header.jsp" %>



<div class="container">
	<form onsubmit="update(event, ${sessionScope.principal.id})">
		<div class="form-group">
			<input type="text" value="${sessionScope.principal.username }"  class="form-control" placeholder="Enter username" maxlength="20" required="required" readonly>
		</div>
		<div class="form-group">
			<input type="email" value="${sessionScope.principal.email }" id="email" class="form-control"	placeholder="Enter email" required="required">
		</div>
		<button type="submit" class="btn btn-primary">회원수정</button>
	</form>
</div>

<script>
async function update(event, id) {
	
	event.preventDefault();
	
	// /board/udpateForm/1
	// update board set title =? and content = ? where id = 1;
	
	let userUpdateDto = {
			email: document.querySelector("#email").value
	};
		
	// JSON.stringify(자바스크립트 오브젝트) -> 리턴 JSON
	// JSON.parse(제이슨 문자열) -> 리턴 자바스크립트 오브젝트
	
	let response = await fetch("/user/"+id, {
			method: "put",
			body: JSON.stringify(userUpdateDto),
			headers: {
				"Content-Type": "application/json; charset=utf-8"
			}
	});
	
	let parseResponse = await response.json(); // 나중에 스프링 함수에서 리턴될 때 뭐가 리턴되는지 확인
	// json으로 파싱하면 . code를 파싱할 수 있다
	
	if(parseResponse.code == 1) {
		alert("업데이트 성공");
		location.href="/";
	} else {
		alert("업데이트 실패: " + parseResponse.msg);
	}
}

</script>

<%@ include file="../layout/footer.jsp" %>



