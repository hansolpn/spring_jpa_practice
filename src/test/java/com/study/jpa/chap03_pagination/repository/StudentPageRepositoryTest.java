package com.study.jpa.chap03_pagination.repository;

import com.study.jpa.chap02_querymethod.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
// JPA는 INSERT, UPDATE, DELETE시에 트랜잭션을 기준으로 동작하는 경우가 많음
// 기능을 보장받기 위해서는 웬만하면 트랜잭션 기능을 함께 사용해야 합니다.
// 나중에 MVC 구조에서 Service 클래스에 아노테이션을 첨부하면 됩니다.
@Transactional
@Rollback(false)
class StudentPageRepositoryTest {

    @Autowired
    IStudentPageRepository pageRepository;

    @BeforeEach
    void buckInsert() {
        // 학생을 147명 저장
        for (int i = 1; i <= 147; i++) {
            Student s = Student.builder()
                    .name("김테스트" + i)
                    .city("도시시" + i)
                    .major("전공공" + i)
                    .build();

            //pageRepository.save(s);
        }
    }
    
    @Test
    @DisplayName("기본 페이징 테스트")
    void testBasicPagination() {
        //given
        int pageNo = 1;
        int amount = 10;

        // 페이지 정보 생성
        // 페이지 번호가 zero-based -> 0이 1페이지
        Pageable pageInfo = PageRequest.of(pageNo - 1, amount);

        //when
        Page<Student> students = pageRepository.findAll(pageInfo);

        // 페이징이 완료된 총 학생 데이터 묶음
        List<Student> studentList = students.getContent();

        //then
        System.out.println("\n\n\n");
        studentList.forEach(System.out::println);
        System.out.println("\n\n\n");
    }
}