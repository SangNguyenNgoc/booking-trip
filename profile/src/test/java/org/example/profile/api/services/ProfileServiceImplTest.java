package org.example.profile.api.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.example.profile.ProfileApplication;
import org.example.profile.api.dtos.RegisterRequest;
import org.example.profile.api.dtos.UpdateProfileRequest;
import org.example.profile.api.entities.Profile;
import org.example.profile.api.interfaces.ProfileMapper;
import org.example.profile.api.interfaces.ProfileRepository;
import org.example.profile.utils.exception.AppException;
import org.example.profile.utils.exception.InputInvalidException;
import org.example.profile.utils.services.ObjectsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;


import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {
    @Mock
    private ProfileRepository profileRepository;

    @Spy
    private ProfileMapper profileMapper = Mappers.getMapper(ProfileMapper.class);

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Setter
    private List<Profile> profiles;

    @Mock
    private ObjectsValidator<UpdateProfileRequest> updateProfileRequestObjectsValidator;

    @Mock
    private ObjectsValidator<RegisterRequest> registerRequestObjectsValidator;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private RegisterRequest validRegisterRequest;
    private RegisterRequest invalidPasswordConfirmRequest;
    private RegisterRequest emailTakenRegisterRequest;
    private Profile successProfile;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Profile>> typeReference = new TypeReference<List<Profile>>() {};
        InputStream inputStream = ProfileApplication.class.getResourceAsStream("/data/profiles.json");
        try {
            List<Profile> profiles = mapper.readValue(inputStream, typeReference);
            setProfiles(profiles);
            System.out.println("Đã import " + profiles.size() + " thông tin người dùng");
        } catch (Exception e) {
            System.out.println("Không thể import dữ liệu: " + e.getMessage());
        }
    }

//    @BeforeEach
//    void init() {
//        // Tạo một TestingAuthenticationToken với các chi tiết xác thực
//        TestingAuthenticationToken authenticationToken =
//                new TestingAuthenticationToken("user", "password", "ROLE_USER");
//
//        // Thiết lập SecurityContext cho quá trình test
//        SecurityContext context = SecurityContextHolder.createEmptyContext();
//        context.setAuthentication(authenticationToken);
//        SecurityContextHolder.setContext(context);
//    }

    @Test
    void getAllProfile(){
        when(profileRepository.findAll()).thenReturn(profiles);

        var result = profileService.getAllProfile();

        assertNotNull(result);
        assertEquals(profiles.size(), result.getSize());
    }

    @Test
    void getProfile(){
        // Mock SecurityContext và Authentication
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        Jwt jwt = Mockito.mock(Jwt.class);

        // Thiết lập claim 'sub' trong Jwt
        when(jwt.getClaim("sub")).thenReturn("60eaaa6f1173535842c35663");

        // Thiết lập jwt làm principal của Authentication
        when(authentication.getPrincipal()).thenReturn(jwt);

        // Đặt Authentication vào SecurityContext
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(profileRepository.findById("60eaaa6f1173535842c35663"))
                .thenReturn(profiles.stream()
                        .filter(profile -> "60eaaa6f1173535842c35663".equals(profile.getId()))
                        .findFirst());
        var result = profileService.getProfile();

        assertNotNull(result);
        assertEquals("Nguyễn Ngọc Sang", result.getFullname());
    }

    @Test
    void updateProfile(){
        // Mock SecurityContext và Authentication
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        Jwt jwt = Mockito.mock(Jwt.class);

        // Thiết lập claim 'sub' trong Jwt
        when(jwt.getClaim("sub")).thenReturn("60eaaa6f1173535842c35663");

        // Thiết lập jwt làm principal của Authentication
        when(authentication.getPrincipal()).thenReturn(jwt);

        // Đặt Authentication vào SecurityContext
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(profileRepository.findById("60eaaa6f1173535842c35663"))
                .thenReturn(profiles.stream()
                        .filter(profile -> "60eaaa6f1173535842c35663".equals(profile.getId()))
                        .findFirst());

        // Giả sử đây là request để cập nhật profile
        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
        updateProfileRequest.setFullname("Updated Name");

        var result = profileService.updateInfo(updateProfileRequest);

        assertNotNull(result);
        assertEquals("Updated Name", result.getFullname());
    }

    @BeforeEach
    void setUpBeforeRegister() {
        // Set up các đối tượng mẫu cho các test case
        validRegisterRequest = RegisterRequest.builder()
                .email("newuser@example.com")
                .fullname("New User")
                .password("password123")
                .confirmPassword("password123")
                .username("newuser")
                .build();

        invalidPasswordConfirmRequest = RegisterRequest.builder()
                .email("newuser@example.com")
                .fullname("New User")
                .password("password123")
                .confirmPassword("password321") // Mật khẩu xác nhận không khớp
                .username("newuser")
                .build();

        emailTakenRegisterRequest = RegisterRequest.builder()
                .email("existing@example.com")
                .fullname("Existing User")
                .password("password123")
                .confirmPassword("password123")
                .username("existinguser")
                .build();
        successProfile = Profile.builder()
                .email("newuser@example.com")
                .fullname("New User")
                .id("1")
                .build();
    }

    @Test
    void register_Success() {
        // Giả lập profileRepository không tồn tại email đã cho
        when(profileRepository.existsByEmail(validRegisterRequest.getEmail())).thenReturn(false);
        when(profileRepository.save(Mockito.any(Profile.class))).thenReturn(successProfile);
        // Khi đăng ký thành công, cần trả về chuỗi "success"
        String result = profileService.register(validRegisterRequest);

        assertEquals("success", result);
    }

    @Test
    void register_EmailTaken() {
        // Giả lập email đã tồn tại
        when(profileRepository.existsByEmail(emailTakenRegisterRequest.getEmail())).thenReturn(true);

        // Kiểm tra xem có ném ngoại lệ AppException không
        AppException exception = assertThrows(AppException.class, () -> {
            profileService.register(emailTakenRegisterRequest);
        });

        // Kiểm tra thông điệp ngoại lệ
        assertEquals("Email taken!", exception.getError());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertTrue(exception.getMessages().contains("Email already exists"));
    }

    @Test
    void register_PasswordNotConfirmed() {
        // Giả lập profileRepository không tồn tại email đã cho
        when(profileRepository.existsByEmail(invalidPasswordConfirmRequest.getEmail())).thenReturn(false);

        // Kiểm tra xem có ném ngoại lệ InputInvalidException không
        InputInvalidException exception = assertThrows(InputInvalidException.class, () -> {
            profileService.register(invalidPasswordConfirmRequest);
        });

        // Kiểm tra thông điệp ngoại lệ
        assertTrue(exception.getMessages().contains("Invalid confirming password"));
    }

}