package com.lec.spring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lec.spring.domain.User;
import com.lec.spring.domain.oauth.KakaoOAuthToken;
import com.lec.spring.domain.oauth.KakaoProfile;
import com.lec.spring.service.UserService;
import com.lec.spring.util.U;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {
    // kakao 로그인, yml에 입력해둔 값 가져오기
    @Value("${app.oauth2.kakao.client-id}")
    private String kakaoClientId;
    @Value("${app.oauth2.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    @Value("${app.oauth2.kakao.token-uri}")
    private String kakaoTokenUri;
    @Value("${app.oauth2.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    @Value("${app.oauth2.password}")
    private String oauth2Password;

    @Autowired
    private UserService userService;    // 화원가입 시켜야 하기 때문에 주입

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/kakao/callback")
    public String kakaoCallback(String code) {    // 카카오가 보내준 code 값 받아오기
        //------------------------------------------------------------------
        // ■ code 값 확인
        //   code 값을 받았다는 것은 인증 완료 되었다는 뜻..
        System.out.println("\n<<카카오 인증 완료>>\ncode: " + code);

        //----------------------------------------------------------------------
        // ■ Access token 받아오기 <= code 값 사용
        // 이 Access token 을 사용하여  Kakao resource server 에 있는 사용자 정보를 받아오기 위함.
        KakaoOAuthToken token = kakaoAccessToken(code); // 메소드 호출

        //------------------------------------------------------------------
        // ■ 사용자 정보 요청 <= Access Token 사용
        KakaoProfile profile = kakaoUserInfo(token.getAccess_token());

        //---------------------------------------------------
        // ■ 회원가입 시키기  <= KakaoProfile (사용자 정보) 사용
        User kakaoUser = registerKakaoUser(profile);

        //---------------------------------------------------
        // ■ 로그인 처리
        loginKakaoUser(kakaoUser);

        //-------------------------------------------------
        return "redirect:/";    // 루트로 리다이렉트
    }

    //------------------------------------------------------------------
    // kakao Access Token 받아오기
    public KakaoOAuthToken kakaoAccessToken(String code){
        // 카카오서버 쪽으로 POST 방식 요청, key-value 쌍
        RestTemplate rt = new RestTemplate();

        // header 준비
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"); // 사이트에서 지정해준 header

        // body 준비
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(); // key와 value 쌍
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);   // 인증 직후 받은 code 값 사용!

        // header 와 body 를 담은 HttpEntity 생성
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        // 요청
        ResponseEntity<String> response = rt.exchange(
                kakaoTokenUri,  // Access Token 발급 URI
                HttpMethod.POST,    // request method
                kakaoTokenRequest,  // HttpEntity (body, header)
                String.class        // 응답받을 타입
        );

        System.out.println("카카오 AccessToken 요청 응답: " + response);   // header, body toString
        // body 만 확인해보면
        System.out.println("카카오 AccessToken 요청 응답 body: " + response.getBody());

        //------------------------------------------------------------------
        // 직접 json 을 java Object 로 바꾸기 (domain.oauth.KakaoOauthToken) 리턴타입 바꿈!
        ObjectMapper mapper = new ObjectMapper();
        KakaoOAuthToken token = null;

        try {
            token = mapper.readValue(response.getBody(), KakaoOAuthToken.class);    // 처리할 json, java Object 로 만들 class
            // AccessToken 확인
            System.out.println("카카오 Access Token: " + token.getAccess_token()); // 확인용, 혹시 token 없으면 exception 발생해서 try-catch
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return token;
    }

    //-------------------------------------------------
    // kakao 사용자 정보 요청(kakaoUserInfo 메소드 생성), token 생성 해서 권한 받기
    public KakaoProfile kakaoUserInfo(String access_token){
        RestTemplate rt = new RestTemplate();

        // header 준비
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + access_token);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // body는 생략(필수는 아니었음)

        //HttpEntity 생성
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        // 요청
        ResponseEntity<String> response = rt.exchange(
                kakaoUserInfoUri,
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        System.out.println("카카오 사용자 profile 요청 응답: " + response);
        System.out.println("카카오 사용자 profile 요청 응답 body: " + response.getBody());

        //-------------------------------------------------
        // 받은 권한으로 사용자 정보 가져오기
        ObjectMapper mapper = new ObjectMapper();
        KakaoProfile profile = null;
        try {
            profile = mapper.readValue(response.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 확인
        System.out.println("""
                [카카오 회원정보]
                id: %s
                nickname: %s
                """.formatted(profile.getId(), profile.getKakaoAccount().getProfile().getNickname()));

        return profile;
    }

    //-----------------------------------------------------------------------------
    // 프로필 정보로 회원가입 시키기  (username, password, email, name 필요)
    // Kakao 로그인 한 회원을 User 에 등록하기
    public User registerKakaoUser(KakaoProfile profile) {
        // 새로이 가입시킬 username 을 생성 (unique 해야 한다!)
        String provider = "KAKAO";
        String providerId = "" + profile.getId();
        String username = provider + "_" + providerId;
        String name = profile.getKakaoAccount().getProfile().getNickname();
        String password = oauth2Password;

        System.out.println("""
                [카카오 인증 회원 정보]
                  username: %s
                  name: %s
                  password: %s   
                  provider: %s
                  providerId: %s             
                """.formatted(username, name, password, provider, providerId));

            // 회원가입 진행하기 전에
            // 이미 가입한 회원인지, 혹은 비가입자인지 체크하여야 한다.
            User user = userService.findByUsername(username);   // 있다면 user 리턴, 없으면 null 리턴
            if(user == null){  // 미가입자인 경우, 회원가입 진행
                User newUser = User.builder()
                        .username(username)
                        .name(name)
                        .password(password)
                        .provider(provider)
                        .providerId(providerId)
                        .build();
                int cnt = userService.register(newUser);  // 회원가입 INSERT!!
                if(cnt > 0){    // 정상적으로 INSERT 되면 1이 리턴 됨
                    System.out.println("[Kakao 인증 회원 가입 성공]");
                    user = userService.findByUsername(username);   // id 값은 이미 있음(newUser) 그러나 regdate 를 읽어오기 위해 다시 불러옴!
                } else {
                    System.out.println("[Kakao 인증 회원 가입 실패]");
                }
            } else {        // 이미 가입한 경우
                    System.out.println("[Kakao 인증. 이미 가입된 회원입니다]");
            }

            return user;
        }

        //-------------------------------------------
        // 회원가입 진행한 후 로그인 시키기
        public void loginKakaoUser(User kakaoUser){
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    kakaoUser.getUsername(),  // username
                    oauth2Password            // password
            );

            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            // 세션에 붙는 시큐리티 폴더, Authentication랑 UserDetails 만드는 겁니다
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(authentication);

            U.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
            System.out.println("Kakao 인증 로그인 처리 완료");
        }

}   // end Controller
