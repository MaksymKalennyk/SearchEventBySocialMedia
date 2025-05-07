package com.example.searchingevents.bot;

import com.example.searchingevents.bot.dto.SentimentResult;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Service
public class TranslateCommentAnalyzer implements CommentSentimentAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(TranslateCommentAnalyzer.class);

    private final StanfordCoreNLP pipeline;

    public TranslateCommentAnalyzer() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,parse,sentiment");
        this.pipeline = new StanfordCoreNLP(props);
        logger.info("Ініціалізовано StanfordCoreNLP для сентимент-аналізу.");
    }

    @Override
    public SentimentResult analyze(String text) {
        logger.info("Початок аналізу коментаря: {}", text);

        try {
            String translatedText = translateToEnglish(text);
            logger.info("Перекладено з української на англійську: {}", translatedText);

            String sentiment = analyzeSentiment(translatedText);
            logger.info("Результат сентимент-аналізу: {}", sentiment);

            return new SentimentResult(
                    text,
                    translatedText,
                    mapToSentiment(sentiment)
            );
        } catch (Exception e) {
            logger.error("Помилка при аналізі коментаря: {}", e.getMessage(), e);
            return new SentimentResult(text, null, SentimentResult.Sentiment.NEUTRAL);
        }
    }

    private String translateToEnglish(String textUa) throws Exception {
        String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=uk&tl=en&dt=t&q=" +
                URLEncoder.encode(textUa, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        logger.debug("Відповідь від Google: {}", response.body());

        JSONArray arr = new JSONArray(response.body());
        JSONArray first = arr.getJSONArray(0);
        JSONArray translation = first.getJSONArray(0);
        return translation.getString(0);
    }

    private String analyzeSentiment(String englishText) {
        Annotation annotation = new Annotation(englishText);
        pipeline.annotate(annotation);

        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            return sentence.get(SentimentCoreAnnotations.SentimentClass.class); // "Positive", etc.
        }

        return "Neutral";
    }

    private SentimentResult.Sentiment mapToSentiment(String stanfordSentiment) {
        return switch (stanfordSentiment.toLowerCase()) {
            case "very positive", "positive" -> SentimentResult.Sentiment.POSITIVE;
            case "very negative", "negative" -> SentimentResult.Sentiment.NEGATIVE;
            default -> SentimentResult.Sentiment.NEUTRAL;
        };
    }
}
