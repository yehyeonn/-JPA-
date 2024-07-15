$(function () {
    // [추가] q버튼 누르면 첨부 파일 추가 가능
    let i = 0;
    $("#btnAdd").click(function () {
        $("#files").append(`
        <div class="input-group mb-2">
           <input class="form-control col-xs-3" type="file" name="upfile${i}"/>    <!-- 파일 이름을 upfile1, upfile2 ... 이렇게 담겨서 서버로 보내야함(중복되면 안 됨!)-->
           <button type="button" class="btn btn-outline-danger" onclick="$(this).parent().remove()">삭제</button>
        </div>
        `);
        i++;
    });

    // Summernote 추가 (내용 작성 부분에!), 초기 옵션은 매개변수 안에 object 로 넣을 수 있음
    $('#content').summernote({
        height: 300,
    });
});