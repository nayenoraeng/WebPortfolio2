package com.project.parkrental.translation;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.springframework.stereotype.Service;

@Service
public class TranslateService {
    private Translate translate;

    public TranslateService() {
        // API 키를 사용하여 번역 클라이언트 생성
        translate = TranslateOptions.newBuilder().setApiKey("AIzaSyB-eX64QKdEmHebhBHaTyTkOPBFDWPYtbw").build().getService();
    }

    // 한글을 영어로 번역
    public String translateToEnglish(String text) {
        Translation translation = translate.translate(text, Translate.TranslateOption.sourceLanguage("ko"),
                Translate.TranslateOption.targetLanguage("en"));
        return translation.getTranslatedText();
    }

    // 영어를 일본어로 번역
    public String translateToJapanese(String text) {
        Translation translation = translate.translate(text,
                Translate.TranslateOption.sourceLanguage("en"),
                Translate.TranslateOption.targetLanguage("ja")); // 일본어로 변경
        return translation.getTranslatedText();
    }
}

