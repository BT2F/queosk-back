package com.bttf.queosk.repository;

import com.bttf.queosk.entity.baseentity.JpaAuditingConfiguration;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.model.usermodel.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JpaAuditingConfiguration.class)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입이 잘 된다.")
    public void setUserData_test() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .email("a@x.com")
                .password("test")
                .phone("0100000000")
                .status(UserStatus.NOT_VERIFIED)
                .build();

        userRepository.save(user);
        // when

        User savedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // then

        assertThat(savedUser.getId()).isEqualTo(user.getId());
    }

}