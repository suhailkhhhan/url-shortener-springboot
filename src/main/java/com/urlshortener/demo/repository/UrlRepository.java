package com.urlshortener.demo.repository;

import com.urlshortener.demo.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
public interface UrlRepository extends  JpaRepository<Url, Long> {
    List<Url> findTop5ByOrderByClickCountDesc();
    Optional<Url> findByShortCode(String shortCode);

}