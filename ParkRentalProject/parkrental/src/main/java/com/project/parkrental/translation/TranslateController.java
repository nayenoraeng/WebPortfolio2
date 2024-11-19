package com.project.parkrental.translation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranslateController {

    private final TranslateService translateService;

    public TranslateController(TranslateService translateService) {
        this.translateService = translateService;
    }

    @GetMapping("/translate")
    public String translate(@RequestParam String text, @RequestParam String targetLang) {
        String translatedText;

        // Add a console log or debugging line to ensure targetLang is received correctly
        System.out.println("Target language received: " + targetLang);

        switch (targetLang) {
            case "en":
                translatedText = translateService.translateToEnglish(text);
                break;
            case "ja": // 일본어로 변경
                translatedText = translateService.translateToJapanese(text); // 일본어 메서드 호출
                break;
            case "ko":
                translatedText = text; // 한국어는 번역 필요 없음
                break;
            default:
                translatedText = "지원하지 않는 언어입니다."; // 지원하지 않는 언어에 대한 기본 케이스
                break;
        }

        return translatedText;
    }
}

