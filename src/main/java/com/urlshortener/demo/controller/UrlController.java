package com.urlshortener.demo.controller;
import com.urlshortener.demo.dto.UrlResponse;
import com.urlshortener.demo.model.Url;
import com.urlshortener.demo.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/api/shorten")
    public Map<String, String> shortenUrl(@RequestBody Map<String, String> request) {

        String longUrl = request.get("longUrl");
        String customCode = request.get("customCode");

        // Validate longUrl check this working or not
        if (longUrl == null || longUrl.isEmpty()) {
            throw new RuntimeException("Invalid URL");
        }

        String shortUrl = urlService.createShortUrl(longUrl, customCode);

        return Map.of("shortUrl", shortUrl);
    }
    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        String ip = request.getRemoteAddr();

        try {
            if (urlService.isRateLimited(ip)) {
                response.sendError(429, "Too many requests");
                return;
            }
        } catch (Exception e) {
            //ignore
        }

        try {
            String originalUrl = urlService.getOriginalUrl(shortCode);
            response.setStatus(HttpServletResponse.SC_FOUND); // 302
            response.setHeader("Location", originalUrl);
        } catch (RuntimeException e) {
            response.sendError(404, e.getMessage());
        }
    }
    @GetMapping("/api/analytics/{shortCode}")
    public Object getAnalytics(@PathVariable String shortCode) {
        try {
            return urlService.getAnalytics(shortCode);
        } catch (Exception e) {
            return Map.of("error", "URL not found");
        }
    }

    @GetMapping("/api/top")
    public List<UrlResponse> getTopUrls() {
        return urlService.getTopUrls();
    }

}