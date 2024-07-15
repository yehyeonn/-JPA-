$(function () {
    // 페이징 헤더
    $("[name='pageRows']").change(function () {
        // alert($(this).val());    // 확인용

        $("[name='frmPageRows']").attr({
            "method": "POST",
            "action": "/board/pageRows",
        }).submit();    // 이러면 pageRows에 선택한 pagerow의 value 값이 입력됨. BoardController에 poageRows 만들어요
    });
});