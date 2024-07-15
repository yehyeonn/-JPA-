package com.lec.spring.service;

import com.lec.spring.domain.Attachment;
import com.lec.spring.domain.Post;
import com.lec.spring.util.U;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

@Service
public class BoardServiceImpl implements BoardService {

    // file 저장되는 경로 받아오는 것
    @Value("${app.upload.path}")
    private String uploadDir;

    @Value("${app.pagination.write_pages}") // 페이지의 개수 가져오는 것
    private int WRITE_PAGES;

    @Value("${app.pagination.page_rows}")   // 한 페이지에 몇 개의 글을 가져올지
    private int PAGE_ROWS;

    // 특정 글(id) 에 첨부파일(들) 추가
    private void addFiles(Map<String, MultipartFile> files, Long id) {
        if(files == null) return;   // 사진 첨부 안 했으면 할 거 없음

        for(Map.Entry<String, MultipartFile> e : files.entrySet()){

            // name="upfile##" 인 경우만 첨부파일 등록. (이유, 다른 웹에디터와 섞이지 않도록..ex: summernote)
            if(!e.getKey().startsWith("upfile")) continue;  // upfile 로 시작하지 않은 사진은 지나간다(패스)

            // 첨부파일 정보 출력(특정 파일이나 사이즈만 받아야 하는 경우에 사용할 수 있다.)
            System.out.println("\n첨부파일 정보: " + e.getKey()); // name 값
            U.printfileInfo(e.getValue());  // MultipartFile 정보 출력
            System.out.println();

            // 물리적인 파일 저장
            Attachment file = upload(e.getValue()); // MultipartFile 이 넘어갑니다

            // 성공하면 DB에 저장
            if(file != null){
                file.setPost_id(id);    // FK 설정
//                attachmentRepository.save(file);    // INSERT
                // TODO
            }
        }

    }

    // 물리적으로 서버에 파일을 저장. 중복된 파일 이름에 대해 rename 처리 필요
    private Attachment upload(MultipartFile multipartFile) {
        Attachment attachment = null;

        // 첨부된 파일 없으면 pass
        String originalFilename = multipartFile.getOriginalFilename();
        if(originalFilename == null || originalFilename.isEmpty()) return null;

        // 원본파일명
        String sourceName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        // 저장될 파일명
        String fileName = sourceName;

        // 파일이 중복되는지 확인
        File file = new File(uploadDir, fileName);
        if(file.exists()){  // 이미 존재하는 파일명, 중복된다면 다른 이름으로 변경하여 저장
            // a.txt => a_2378142783946.txt  : time stamp 값을 활용할거다!(ms 단위의 고유한 값)
            // "a" => "a_2378142783946"  : 확장자 없는 경우

            int pos = fileName.lastIndexOf(".");  // 확장자 있는지
            if(pos > -1) {  // 확장자가 있는 경우
                String name = fileName.substring(0, pos);   // 파일이름
                String ext = fileName.substring(pos + 1);       // 파일 '확장자'

                // 중복 방지 회피를 위해 새로운 이름(타임스탬프, 현재시간 ms) 를 파일명에 추가
                fileName = name + "_" + System.currentTimeMillis() + "." + ext;
            } else {        // 확장자 없는 경우
                fileName += "_" + System.currentTimeMillis();   // 확장자 없으면 굳이 이름과 확장자 나눌 필요없음 그냥 타임스탬프 추가하면 됨
            }
        }
        System.out.println("fileName: " + fileName);

        // java.io.* 이후
        // java.nio.* 등장, 쓰임새는 다르지만 편리함 path, paths, files
        Path copyOfLocation = Paths.get(new File(uploadDir, fileName).getAbsolutePath());    // 경로를 표현하는 객체, 절대 경로로
        System.out.println(copyOfLocation);

        try {
            // inputStream을 가져와서
            // copyOfLocation (저장위치)로 파일을 쓴다.
            // copy의 옵션은 기존에 존재하면 REPLACE(대체한다), 오버라이딩 한다

            // java.nio.file.Files
            Files.copy( // 저장하는 메소드
                    multipartFile.getInputStream(),
                    copyOfLocation,
                    StandardCopyOption.REPLACE_EXISTING // 기존에 존재하면 덮어쓰기
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        attachment = Attachment.builder()
                .filename(fileName) // 저장된 이름
                .sourcename(sourceName) // 원본이름
                .build();

        return attachment;  // DB에 저장하기 위해 만듦!(Attachment file = upload(e.getValue()) 를 통해)
    }


    // [이미지 파일인지 여부 세팅]
    private void setImage(List<Attachment> fileList) {
        // upload 실제 물리적인 경로 가져오기
        String realPath = new File(uploadDir).getAbsolutePath();

        for(Attachment attachment : fileList){
            BufferedImage imgData = null;
            File f = new File(realPath, attachment.getFilename());  // 저장된 첨부파일에 대한 File 객체

            try {
                imgData = ImageIO.read(f);
                // ※ ↑ 파일이 존재 하지 않으면 IOExcepion 발생한다
                //   ↑ 이미지가 아닌 경우는 null 리턴
                if(imgData != null) attachment.setImage(true);      // 이미지 인 경우 세팅

            } catch (IOException e) {   // f 가 없으면 Exception 발생, f 는 있으나 이미지가 아니면 null
                System.out.println("파일 존재 안 함: " + f.getAbsolutePath() + "[" + e.getMessage() + "]");
            }
        }
    }


    // 특정 첨부파일을 물리적으로 삭제
    private void delFile(Attachment file) {
        String saveDirectory = new File(uploadDir).getAbsolutePath();   // 경로 가져오기

        File f = new File(saveDirectory, file.getFilename());   // 물리적으로 저장된 파일 객체
        System.out.println("삭제 시도 ---->" + f.getAbsolutePath());

        if(f.exists()){
            if(f.delete()){
                System.out.println("삭제 성공");
            } else {
                System.out.println("삭제 실패");
            }
        } else {
            System.out.println("파일이 존재하지 않습니다.");
        }
    }


    @Override
    public int write(Post post, Map<String, MultipartFile> files) {
        return 0;
    }

    @Override
    public Post detail(Long id) {
        return null;
    }

    @Override
    public List<Post> list() {
        return null;
    }

    @Override
    public List<Post> list(Integer page, Model model) {
        return null;
    }

    @Override
    public Post selectById(Long id) {
        return null;
    }

    @Override
    public int update(Post post, Map<String, MultipartFile> files, Long[] delfile) {
        return 0;
    }

    @Override
    public int deleteByID(Long id) {
        return 0;
    }
}
