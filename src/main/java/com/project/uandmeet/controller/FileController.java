package com.project.uandmeet.controller;

import com.project.uandmeet.image.AwsS3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class FileController {
    private final AwsS3Uploader awsS3Uploader;

    //테스트입니다
    @PostMapping("/images")
    public String upload(@RequestParam("images") MultipartFile multipartFile) throws IOException {
        awsS3Uploader.upload(multipartFile, "static");
        return "test";
    }
    //파일 업로드 할때 api 통신을 통해 받아올 객체의 타입은 MultipartFile 입니다.
    //upload 메소드의 두번째 파라미터(static)의 이름에 따라 S3 bucket 내부에 이미지를 담을 해당 이름의 폴더가 생성됩니다
    // 이를 이용해 프로필 이미지는 "profileImg" , 게시물의 이미지는 "boardImg" 라고 명명해도 될것같습니다.
    //보드 컨트롤러에 내용 포함하면 됩니당 멤버 컨트롤러도!
}
