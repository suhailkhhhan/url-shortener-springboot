package com.urlshortener.demo.service;

import com.urlshortener.demo.dto.UrlResponse;
import com.urlshortener.demo.model.Url;
import com.urlshortener.demo.repository.UrlRepository;
import com.urlshortener.demo.util.Base62Util;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;

    public String createShortUrl(String longUrl, String customCode) {

        // If custom code provided
        if (customCode != null && !customCode.isEmpty()) {

            // Check if already or Not hai
            boolean exists = urlRepository.findByShortCode(customCode).isPresent();

            if (exists) {
                throw new RuntimeException("Custom code already in use");
            }

            Url url = Url.builder()
                    .longUrl(longUrl)
                    .shortCode(customCode)
                    .createdAt(LocalDateTime.now())
                    .clickCount(0)
                    .build();

            urlRepository.save(url);

            return "http://localhost:8080/" + customCode;
        }

        // Default flow
        Url url = Url.builder()
                .longUrl(longUrl)
                .createdAt(LocalDateTime.now())
                .clickCount(0)
                .build();

        Url saved = urlRepository.save(url);

        String shortCode = Base62Util.encode(saved.getId());

        saved.setShortCode(shortCode);
        urlRepository.save(saved);

        return "http://localhost:8080/" + shortCode;
    }
    private final StringRedisTemplate redisTemplate;
    public String getOriginalUrl(String shortCode) {
        System.out.println("Method HIT");
        String longUrl = null;

        try {
            longUrl = redisTemplate.opsForValue().get(shortCode);
        } catch (Exception e) {
            //ignore
        }

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        // Expiry check
        if (url.getExpiryDate() != null &&
                url.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Link expired");
        }

        // Cache check is present or not
        if (longUrl == null) {
            longUrl = url.getLongUrl();

            try {
                redisTemplate.opsForValue().set(shortCode, longUrl);
            } catch (Exception e) {}
        }

        // increment how many person click on link
        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);

        return longUrl;
    }
    public boolean isRateLimited(String ip) {

        try {
            String key = "rate_limit:" + ip;

            Long count = redisTemplate.opsForValue().increment(key);

            if (count == 1) {
                redisTemplate.expire(key, 60, TimeUnit.SECONDS);
            }

            return count > 10;

        } catch (Exception e) {
            return false;
        }
    }
    public Url getAnalytics(String shortCode) {
        return urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL not found"));
    }
    public List<UrlResponse> getTopUrls() {

        return urlRepository.findTop5ByOrderByClickCountDesc()
                .stream()
                .map(url -> new UrlResponse(
                        url.getShortCode(),
                        url.getLongUrl(),
                        url.getClickCount()
                ))
                .toList();
    }

}
