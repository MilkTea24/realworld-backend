package com.milktea.main.user.entity;

import com.milktea.main.user.dto.request.UserUpdateRequest;
import com.milktea.main.util.TimestampEntity;
import com.milktea.main.util.exceptions.ExceptionUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Entity
@Getter
@Table(name = "user_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends TimestampEntity {
    private static final String DEFAULT_BIO = "Default Bio";
    private static final String DEFAULT_IMAGE_PATH = null;

    @Id
    @GeneratedValue
    private Long id;

    private String email;

    private String username;

    private String bio;

    private String image;

    private String password;

    @OneToMany(mappedBy = "user")
    private final List<Authority> authorities = new ArrayList<>();

    @Builder
    public User(String email, String username, String bio, String image) {
        String inputBio = DEFAULT_BIO;
        if (!Objects.isNull(bio)) inputBio = bio;

        String inputImage = DEFAULT_IMAGE_PATH;
        if (!Objects.isNull(bio)) inputImage = image;

        this.email = email;
        this.username = username;
        this.bio = inputBio;
        this.image = inputImage;
    }

    public void setPassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public void addAuthority(Authority authority) {
        authority.setUser(this);
        authorities.add(authority);
    }

    public void updateUser(UserUpdateRequest.UserUpdateDTO userRequest) {
        //Record Component를 얻고 각각의 Component에서 getAccessor를 호출하여 getter 얻기
        List<Method> getters = Arrays.stream(userRequest.getClass().getRecordComponents())
                .map(RecordComponent::getAccessor)
                .toList();

        //Entity와 DTO getter를 매핑하기 위해 Entity Field를 reflection으로 가져옴
        Field[] fields = this.getClass().getDeclaredFields();

        try {
            for (Method getter : getters) {
                String updateValue = (String) getter.invoke(userRequest);
                //클라이언트에서 보낸 DTO 필드 값이 null이면 수정하는 필드가 아니므로 패스
                if (Objects.isNull(updateValue)) continue;

                Field field = searchUpdateField(fields, getter);
                log.debug("업데이트 전 필드 값 - {}", field.get(this));
                //Record의 getter로 얻은 값을 entity의 field 값으로 변경
                field.set(this, updateValue);
                log.debug("업데이트 후 필드 값 - {}", field.get(this));
            }
        } catch (Exception e) {
            log.error("DTO 메서드를 가져오는 중 문제가 생겼습니다.");
            if (log.isDebugEnabled()) log.debug(ExceptionUtils.getStackTrace(e));
        }
    }

    private Field searchUpdateField(Field[] fields, Method getter) {
        for (Field f : fields) {
            //조건 1 : 필드 이름과 getter 이름 같아야 함
            if (!f.getName().equals(getter.getName())) continue;

            //조건 2 : 필드의 타입은 getter와 타입이 같거나 부모타입이어야 한다.
            if (!getter.getReturnType().isAssignableFrom(f.getType())) continue;

            return f;
        }

        log.error("클라이언트가 전송한 Update할 필드를 엔티티에서 찾을 수 없음!");
        if (log.isDebugEnabled()) log.debug("Update할 수 없는 DTO 필드 - {}", getter.getName());
        throw new RuntimeException("User에서 update할 필드를 찾지 못했습니다.");
    }
}
