package com.kh.coupang.controller;

import com.kh.coupang.domain.*;
import com.kh.coupang.service.ReviewService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/*")
public class ReviewController {

    @Autowired
    private ReviewService review;

    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    @PostMapping("/review")
    public ResponseEntity<Review> create(ReviewDTO dto) throws IOException {

        // review부터 추가하여 revi_code가 담긴 review!
        Review vo = new Review();
        vo.setId(dto.getId());
        vo.setProdCode(dto.getProdCode());
        vo.setReviTitle(dto.getReviTitle());
        vo.setReviDesc(dto.getReviDesc());
        vo.setRating(dto.getRating());

        Review result = review.create(vo);

        // review_image에는 revi_code가 필요!
        for (MultipartFile file : dto.getFiles()) {
            ReviewImage imgVo = new ReviewImage();
            String fileName = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String saveName = uploadPath + File.separator + "review" + File.separator + uuid + "_" + fileName;
            Path savePath = Paths.get(saveName);
            file.transferTo(savePath);

            imgVo.setReviUrl(saveName);
            imgVo.setReview(result);
            review.createImg(imgVo);

        }
        return result!=null ?
            ResponseEntity.status(HttpStatus.CREATED).body(result) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/review")
    public ResponseEntity<List<Review>> viewAll
            (@RequestParam(name = "prodCode", required = false) Integer prodCode,
            @RequestParam(name = "page", defaultValue = "1") int page ) {
        Sort sort = Sort.by("reviCode").descending();
        Pageable pageable = PageRequest.of(page - 1, 10, sort);
        QReview qReview = QReview.review;

        BooleanBuilder builder = new BooleanBuilder();
        if (prodCode != null) {
            BooleanExpression expression = qReview.prodCode.eq(prodCode);

            builder.and(expression);
        }

        Page<Review> list = review.viewAll(pageable, builder);
        return ResponseEntity.status(HttpStatus.OK).body(list.getContent());
    }
}
