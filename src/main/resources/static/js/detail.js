$(function () {
    // 글 [삭제] 버튼
    $("#btnDel").click(function (){
        let answer = confirm("삭제하시겠습니까?");
        answer && $("form[name='frmDelete']").submit();
    });

    // 현재 글의 id 값
    const id = $("input[name='id']").val().trim();

    // 현재 글의 댓글들을 불러온다
    loadComment(id);

    // 근데 댓글 작성은 회원만 가능
    // 댓글 작성 버튼 누르면 댓글 등록 하기.
    // 1. 어느글에 대한 댓글인지? --> 위에 id 변수에 담겨있다
    // 2. 어느 사용자가 작성한 댓글인지? --> logged_id 값
    // 3. 댓글 내용은 무엇인지?  --> 아래 content
    $("#btn_comment").click(function (){
        const content = $("#input_comment").val().trim();

        // 비어있는지 검증
        if(!content){
            alert("댓글을 입력하세요");
            $("#input_comment").focus();
            return;
        }

        // Post 방식으로 전달해야함 => 전달할 parameter 준비
        const data = {
            "post_id": id,
            "user_id": logged_id,
            "content": content,
        };

        $.ajax({
            url: "/comment/write",
            type: "POST",
            data: data,
            cache: false,
            success: function(data, status){
                if(status == "success"){
                    if(data.status !== "OK"){
                        alert(data.status);
                        return;
                    }   // 성공하면
                    loadComment(id);    // 댓글 목록 다시 업데이트(내가 작성한 것도 보여야함)
                    $("#input_comment").val('');
                }
            },
        })
    });
});

// 특정 글(post_id) 의 댓글 목록 읽어오기
function loadComment(post_id){
    $.ajax({
        url: "/comment/list/" + post_id,
        type: "GET",
        cache: false,
        success: function(data, status){
            if(status == "success"){
                // 서버 쪽에 에러 메세지 있는 경우
                if(data.status !== "OK"){
                    alert(data.status);
                    return;
                }

                buildComment(data); // 댓글 화면 렌더링(response 받은 data 를 가지고)

                // ★댓글목록을 불러오고 난뒤에 삭제에 대한 이벤트 리스너를 등록해야 한다
                addDelete();

            }
        },
    });

}

function buildComment(result){
    $("#cmt_cnt").text(result.count); // 댓글 총 개수

    const out = [];
    result.data.forEach(comment => {
        let id = comment.id;
        let content = comment.content.trim();
        let regdate = comment.regdate;

        let user_id = parseInt(comment.user.id);
        let username = comment.user.name;
        let name = comment.user.name;

        // 삭제버튼 여부
        const delBtn = (logged_id !== user_id) ? '' : `
            <i class="btn fa-solid fa-delete-left text-danger" data-bs-toggle="tooltip" data-cmtdel-id="${id}" title="삭제"></i>   
            <!--/* data-cmtdel-id="${id}" : 삭제 버튼을 눌렀을 때 몇 번 댓글을 삭제할 건지 알기 위해 */-->
        `;

        const row = `
            <tr>
                <td><span><strong>${username}</strong><br><small class="text-secondary">(${name})</small></span></td>
                <td>
                    <span>${content}</span>${delBtn}
                </td>
                <td><span><small class="text-secondary">${regdate}</small></span></td>
            </tr>
        `;
        out.push(row);
    });

    $("#cmt_list").html(out.join('\n'));
}

function addDelete(){

    // 현재 글의 id
    const id = $("input[name='id']").val().trim();

    $("[data-cmtdel-id]").click(function (){
        if(!confirm("댓글을 삭제하시겠습니까?")) return;

        // 삭제할 댓글의 id
        const comment_id = $(this).attr("data-cmtdel-id");

        $.ajax({
            url: "/comment/delete",
            type: "POST",
            cache: false,
            data: {"id": comment_id},
            success: function (data, status){
                if(status == "success") {
                    if(data.status !== "OK"){
                        alert(data.status);
                        return;
                    }

                    // 삭제 후에도 댓글 목록 불어와야 한다
                    loadComment(id);
                }
            }
        })

    });



}
