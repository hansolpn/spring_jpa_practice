package com.study.jpa.chap05_practice.api;

import com.study.jpa.chap05_practice.dto.*;
import com.study.jpa.chap05_practice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@Tag(name = "post API", description = "게시물 조회, 등록, 수정, 삭제 api 입니다.")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostApiController {

    // 리소스: 게시물 (Post)
    /*
        게시물 목록 조회: /posts            - GET, param: (page, size)
        게시물 개별 조회: /posts/{id}       - GET
        게시물 등록:     /posts            - POST, payload: (title, writer, content, hashTags)
        게시물 수정:     /posts            - PUT, PATCH, payload: (title, content, postNo)
        게시물 삭제:     /posts/{id}       - DELETE
     */
    private final PostService postService;

    @GetMapping
    public ResponseEntity<?> list(PageDTO dto) {
        log.info("/api/v1/posts?page={}&size={}", dto.getPage(), dto.getSize());

        PostListResponseDTO resDTO = postService.getPosts(dto);

        return ResponseEntity.ok().body(resDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable long id) {
        log.info("/api/v1/posts/{} GET", id);

        try {
            PostDetailResponseDTO resDTO = postService.getDetail(id);
            return ResponseEntity.ok().body(resDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @Operation(summary = "게시물 작성", description = "게시물 작성을 담당하는 메서드 입니다.")
    @Parameters({
            @Parameter(name = "writer", description = "게시물의 작성자 이름을 쓰세요!", example = "김뽀삐", required = true),
            @Parameter(name = "title", description = "게시물의 제목을 쓰세요!", example = "제목제목", required = true),
            @Parameter(name = "content", description = "게시물의 내용을 쓰세요!", example = "내용내용"),
            @Parameter(name = "hashTags", description = "게시물의 해시태그를 작성하세요!", example = "['하하', '호호']")
    })
    @PostMapping
    public ResponseEntity<?> create(
            @Validated @RequestBody PostCreateDTO dto,
            BindingResult result // 검증 에러 정보를 가진 객체
            
    ) {
        log.info("/api/v1/posts POST - payload: {}", dto);

        if (dto == null) {
            return ResponseEntity.badRequest().body("등록 게시물 정보를 전달해 주세요!");
        }

        ResponseEntity<List<FieldError>> fieldErrors = getValidatedResult(result);
        if (fieldErrors != null) return fieldErrors;

        try {
            // 위에 존재하는 if문을 모두 건너뜀 -> dto가 null도 아니고, 입력값 검증도 모두 통과함. -> service에게 명령
            PostDetailResponseDTO resDTO = postService.insert(dto);
            return ResponseEntity.ok().body(resDTO);
        } catch (Exception e) {
            log.error("", e);
            return ResponseEntity.internalServerError().body("미안 서버 터짐 원인: " + e.toString());
        }

    }

    // 게시물 수정
    @RequestMapping(method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<?> update(@Validated @RequestBody PostModifyDTO dto,
                                    BindingResult result,
                                    HttpServletRequest request
    ) {
        log.info("/api/v1/posts {} - payload: {}", request.getMethod(), dto);

        if (dto == null) {
            return ResponseEntity.badRequest().body("수정 게시물 정보를 전달해 주세요!");
        }
        
        ResponseEntity<List<FieldError>> fieldErrors = getValidatedResult(result);
        if (fieldErrors != null) return fieldErrors;

        PostDetailResponseDTO resDTO = postService.modify(dto);

        return ResponseEntity.ok().body(resDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.info("/api/v1/posts/{} DELETE", id);

        try {
            postService.delete(id);
            return ResponseEntity.ok("DEL SUCCESS");
        }/* catch (SQLIntegrityConstraintViolationException e) {
            ResponseEntity.internalServerError()
                    .body("해시태그가 달린 게시물은 삭제할 수 있습니다.");
        }*/
        catch (Exception e) {
            log.error(e.toString(), e);
            return ResponseEntity.internalServerError().body(e.toString());
        }

    }

    //입력값 검증(validation)의 결과를 처리해 주는 전역 메서드
    private static ResponseEntity<List<FieldError>> getValidatedResult(BindingResult result) {
        if (result.hasErrors()) { // 입력값 검증 단계에서 문제가 있었다면 true
            List<FieldError> fieldErrors = result.getFieldErrors();
            fieldErrors.forEach(err -> {
                log.warn("invalid client data - {}", err.toString());
            });

            return ResponseEntity.badRequest().body(fieldErrors);
        }
        return null;
    }

}
