package com.lec.spring.domain;

import com.lec.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component  // 중복된 username 이 있는지 확인하기 위해 service를 주입 받아야 하는데 그걸 위해 적음
public class UserValidator implements Validator {

    @Autowired
    UserService userService;    // DB 접근해서 username 가져와서 중복된 username이 있는지 가져오기 위함

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        // username 필수, 중복된 username 여부
        String username = user.getUsername();
        if(username == null || username.trim().isEmpty()){
            errors.rejectValue("username", "username 은 필수 입니다.");
        } else if(userService.isExist(username)){
            errors.rejectValue("username", "이미 존재하는 아이디(username) 입니다.");
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "name 은 필수입니다");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password 는 필수입니다");

        // email
        // 입력이 되어 있으면 정규표현식 패턴 체크
        // TODO


        // 입력 password, re_password 가 동일한지 비교
        if(!user.getPassword().equals(user.getRe_password())){
            errors.rejectValue("re_password", "비밀번호와 비밀번호 확인 입력값은 같아야 합니다.");
        }
        // 이제 이걸 UserController 에 연결!



    }
}
