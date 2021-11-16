<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<%@ include file="../layout/header.jsp" %>

<div class="container">
	<form onsubmit="update(event, ${boardEntity.id})">
		<div class="form-group">
			<input type="text" id="title" value="${boardEntity.title}" class="form-control" placeholder="Enter title" required="required">
		</div>
		<div class="form-group">
			<textarea class="form-control" id="content" rows="5" >${boardEntity.content }</textarea>
		</div>
		<button type="submit" class="btn btn-primary">글수정</button>
	</form>
</div>
 
<script>
	async function update(event, id) {
		
		event.preventDefault();
		
		// /board/udpateForm/1
		// update board set title =? and content = ? where id = 1;
		
		let boardUpdateDto = {
				title: document.querySelector("#title").value,
				content: document.querySelector("#content").value
		};
			
		console.log(boardUpdateDto);
		
		// JSON.stringify(자바스크립트 오브젝트) -> 리턴 JSON
		// JSON.parse(제이슨 문자열) -> 리턴 자바스크립트 오브젝트
		
		let response = await fetch("/api/board/"+id, {
				method: "put",
				body: JSON.stringify(boardUpdateDto),
				headers: {
					"Content-Type": "application/json; charset=utf-8"
				}
		});
		
		let parseResponse = await response.json(); // 나중에 스프링 함수에서 리턴될 때 뭐가 리턴되는지 확인
		// json으로 파싱하면 . code를 파싱할 수 있다
		
		if(parseResponse.code == 1) {
			alert("업데이트 성공");
			location.href="/board/"+id ;
		} else {
			alert("업데이트 실패: " + parseResponse.msg);
		}
	}

     $('#content').summernote({
  		  height: 350
    });

</script>

<%@ include file="../layout/footer.jsp" %>