package com.lec.spring.controller;

import com.lec.spring.domain.Post;
import com.lec.spring.domain.PostValidator;
import com.lec.spring.service.BoardService;
import com.lec.spring.util.U;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;  // 서비스 단 위임을 위해 만든 BoardService 생성, 주입

    public BoardController(){
        System.out.println("BoardController() 생성");
    }

    @GetMapping("/write")   // 눈에 보여지는 화면 4개(PPT 초록색)
    public void write(){}

    @PostMapping("/write")  // 글 작성 후 보이는 화면
    public String writeOk(
            @RequestParam Map<String, MultipartFile> files  // 첨부파일들 <name, file>
            , @Valid Post post
            , BindingResult result
            , Model model       // 매개변수 선언시 BindingResult 보다 Model ㅇ르 뒤에 두어야 한다.
            , RedirectAttributes redirectAttributes // redirect 시 넘겨줄 값들을 담는 객체

    ){  // 2. post = html name 넘어온 것들, 그걸 boardService에 넘겨줌

        // 만약 에러가 있다면 원래 있던 페이지로 redirect 한다(밑에 서비스단 위임 진행X)
        if(result.hasErrors()){
            // addAttribute
            //    request parameters로 값을 전달.  redirect URL에 query string 으로 값이 담김
            //    request.getParameter에서 해당 값에 액세스 가능
            // addFlashAttribute
            //    일회성. 한번 사용하면 Redirect후 값이 소멸
            //    request parameters로 값을 전달하지않음
            //    '객체'로 값을 그대로 전달

            // redirect 시, 기존에 입력했던 값들은 보이게 하기
            redirectAttributes.addFlashAttribute("user", post.getUser());
            redirectAttributes.addFlashAttribute("subject", post.getSubject());
            redirectAttributes.addFlashAttribute("content", post.getContent());

            // 어떤 에러가 발생했는지 알려주기 위해 에러 내용을 같이 보내주기위함
            List<FieldError> errList = result.getFieldErrors();
            for (FieldError err : errList) {
                redirectAttributes.addFlashAttribute("error_" + err.getField(), err.getCode());
            }


            return "redirect:/board/write"; // 근데 이러면 다른 입력한 값들도 함께 날아감
        }
        // 서비스단 위임
        model.addAttribute("result", boardService.write(post, files)); // 작성했던 값을 가져와 새로운 model 에 추가
        return "board/writeOk";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model){

        Post post = boardService.detail(id);
        model.addAttribute("post", post);

        return "board/detail";
    }

    @GetMapping("/list")
//    public void list(Model model){
    public void list(Integer page, Model model){    // page라는 파라메타가 없으면 null, list overriding
        boardService.list(page, model); // service 단에서 model 을 받았기 때문에
//        model.addAttribute("list", boardService.list());    // boardService.list() 결과를 list 에 담음
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable Long id, Model model){
        model.addAttribute("post", boardService.selectById(id));
        return "board/update";
    }

    @PostMapping("/update") // 제출하고 만들 post 방식
    public String updateOk(
            @RequestParam Map<String, MultipartFile> files  // 새로 추가될 파일들
            , @Valid Post post
            , BindingResult result
            , Long[] delfile    // 삭제할 파일들(여러개 일 수도 있어서 배열로 받음0
            , Model model
            , RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("subject", post.getSubject());
            redirectAttributes.addFlashAttribute("content", post.getContent());

            List<FieldError> errList = result.getFieldErrors();
            for (FieldError err : errList) {
                redirectAttributes.addFlashAttribute("error_" + err.getField(), err.getCode());
            }
            return "redirect:/board/update/" + post.getId(); // Id는 수정사항이 아니다
        }

        model.addAttribute("result", boardService.update(post, files, delfile));
        return "board/updateOk";
        // 성공하면 detail 실패하면 update로 돌아가

    }

    // 삭제 처리
    @PostMapping("/delete")
    public String deleteOk(Long id, Model model){
        model.addAttribute("result", boardService.deleteByID(id));
        return "board/deleteOk";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder){
        System.out.println("BoardController.inBinder() 호출");
        binder.setValidator(new PostValidator());
    }

    // 페이징
    // pageRows 변경시 동작
    @PostMapping("/pageRows")   // page: 현재 페이지, pageRows: 내가 몇 개씩 볼지 선택한 값
    public String pageRows(Integer page, Integer pageRows){
        // pageRows 를 session에 담아 저장(다음 페이지도 동일한 개수만큼 보이게 하려고)
        U.getSession().setAttribute("pageRows", pageRows);
        return "redirect:/board/list?page=" + page; //변경된 시점에서 pageRows를 받아뒀기 때문에 내가 선택한 개수만큼의 페이지가 보임.
    }




}   // end Controller
