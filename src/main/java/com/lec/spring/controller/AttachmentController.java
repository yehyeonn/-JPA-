package com.lec.spring.controller;
// 다운로드를 위한 controller

import com.lec.spring.domain.Attachment;
import com.lec.spring.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController // 데이터 response 하는 컨트롤러
                    // @Controller + @ResponseBody
public class AttachmentController {

    @Value("${app.upload.path}")
    private String uploadDir;

    private AttachmentService attachmentService;

    @Autowired
    public void setAttachmentService(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    // 파일 다운로드
    // id: 첨부파일의 id
    // ResponseEntity<T> 를 사용하여
    // '직접' Response data 를 구성  // JSON, DATA 는 spring 에서 기본값으로 세팅해줬던 거
    @RequestMapping("/board/download")
    public ResponseEntity<?> download(Long id){
        if(id == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);   // 게시물 id 가 없으면 400 에러, white Error 안 뜸. 그건 spring 이 보여주려고 만든 거

        Attachment file = attachmentService.findById(id);
        if(file == null) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);   // DB에 사진이 없으면 404 에러

        String sourceName = file.getSourcename();   // 원본 이름
        String fileName = file.getFilename();       // 저장된 파일명

        String path = new File(uploadDir, fileName).getAbsolutePath();  // 저장 파일의 절대 경로

        // 파일 유형(MIME type) 추출
        try {
            String mimeType = Files.probeContentType(Paths.get(path));        // ex) "image/png"          어떤 타입인지 전송해줘야만 한다. 간혹 파일 유형이 지정되지 않은 경우도 있다

            // 파일 유형이 지정되지 않은 경우 null 리턴됨
            if(mimeType == null) {
                mimeType = "application/octet-stream";  // 일련의 byte 스트림 타입. 유형이 알려지지 않은 경우 지정
            }

            // response body 준비
            Path filePath = Paths.get(path);    // 파일 경로
            // 저장된 파일 => inputStream 뽑아내고 Spring에서 사용 가능한 resource 만듦(파일이라고 생각하세요 이걸 response 에 담아 보낼 예정)
            Resource resource = new InputStreamResource(Files.newInputStream(filePath));     // filePath로 부터 InputStream 뽑아냅니다 그걸 spring에서 사용하는 리소스로 뽑아냄

            // response header 세팅
            HttpHeaders headers = new HttpHeaders();
            // ↓ 원본 파일 이름(sourceName) 으로 다운로드 하게 하기 위한 세팅(이게 헤더에 저장되어있음) ★★★★★★★★
            // 반.드.시. URL 인코딩 해야함(한국어로 되어있는 거는 깨져서 들어감)
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename(URLEncoder.encode(sourceName, "utf-8")).build());   // 다운 받으면 원본 이름으로 다운 받아지게 하는 것
            headers.setCacheControl("no-cache");    // 캐시 사용 여부 설정
            headers.setContentType(MediaType.parseMediaType(mimeType)); // 파일 유형 지정, 지정 안 되어있으면 어떠한 동작도 안 하고, 에러 남. 이게 어떤 건지 보여주는 거임(img인지, html인지... 표현해줄 수 있는 건 해주고 못해주는 건 다운로드 받게 해줄 수 있게 세팅함)

            // ResponseEntity<> 리턴(body, header, status)
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);  // 200
        } catch (IOException e) {
            return new ResponseEntity<>(null, null, HttpStatus.CONFLICT);    // 409
        }
    }
}
