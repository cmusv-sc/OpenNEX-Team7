package controllers;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by shbekti on 4/28/15.
 */
public class DummyServices extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result tokenize() {
        JsonNode json = request().body().asJson();
        String content = json.findPath("content").textValue();

        if (content == null) {
            return badRequest("Missing parameter [content]");
        }

        byte[] decodedBytes = Base64.decodeBase64(content.getBytes());
        String decodedString = new String(decodedBytes);

        String[] lines = decodedString.split("\\s");

        String joinedString = StringUtils.join(lines, "\n");

        byte[] encodedBytes = Base64.encodeBase64(joinedString.getBytes());
        String encodedString = new String(encodedBytes);

        String jsonResponse = "{\"content\":\"" + encodedString + "\"" + "}";

        return ok(jsonResponse);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result map() {
        JsonNode json = request().body().asJson();
        String content = json.findPath("content").textValue();

        if (content == null) {
            return badRequest("Missing parameter [content]");
        }

        byte[] decodedBytes = Base64.decodeBase64(content.getBytes());
        String decodedString = new String(decodedBytes);

        String[] lines = decodedString.split("\n");

        for (int i = 0; i < lines.length; ++i) {
            lines[i] += "\t1";
        }

        String joinedString = StringUtils.join(lines, "\n");

        byte[] encodedBytes = Base64.encodeBase64(joinedString.getBytes());
        String encodedString = new String(encodedBytes);

        String jsonResponse = "{\"content\":\"" + encodedString + "\"" + "}";

        return ok(jsonResponse);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result sort() {
        JsonNode json = request().body().asJson();
        String content = json.findPath("content").textValue();

        if (content == null) {
            return badRequest("Missing parameter [content]");
        }

        byte[] decodedBytes = Base64.decodeBase64(content.getBytes());
        String decodedString = new String(decodedBytes);

        String[] lines = decodedString.split("\n");
        Arrays.sort(lines);

        String joinedString = StringUtils.join(lines, "\n");

        byte[] encodedBytes = Base64.encodeBase64(joinedString.getBytes());
        String encodedString = new String(encodedBytes);

        String jsonResponse = "{\"content\":\"" + encodedString + "\"" + "}";

        return ok(jsonResponse);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result reduce() {
        JsonNode json = request().body().asJson();
        String content = json.findPath("content").textValue();

        if (content == null) {
            return badRequest("Missing parameter [content]");
        }

        byte[] decodedBytes = Base64.decodeBase64(content.getBytes());
        String decodedString = new String(decodedBytes);

        String[] lines = decodedString.split("\n");

        ArrayList<String> results = new ArrayList<>();
        String previousKey = null;
        int sum = 0;
        String key = null;
        int value;

        for (String line : lines) {
            String pair[] = line.split("\\t");
            key = pair[0];
            value = Integer.parseInt(pair[1]);

            if (key.equals(previousKey)) {
                sum += value;
            } else {
                if (previousKey == null) {
                    previousKey = key;
                    sum += value;
                } else {
                    results.add(previousKey + "\t" + sum);
                    previousKey = key;
                    sum = value;
                }
            }
        }

        if (lines.length > 0) {
            results.add(key + "\t" + sum);
        }

        StringBuilder sb = new StringBuilder();

        for (String result : results) {
            sb.append(result);
            sb.append("\n");
        }

        String joinedString = StringUtils.join(sb.toString(), "\n");

        byte[] encodedBytes = Base64.encodeBase64(joinedString.getBytes());
        String encodedString = new String(encodedBytes);

        String jsonResponse = "{\"content\":\"" + encodedString + "\"" + "}";

        return ok(jsonResponse);
    }

    public static String shift(String input) {
        char[] forRet = input.toCharArray();
        int len = forRet.length;
        for(int i = 0; i<len; i++) {
            char ch = forRet[i];
            if(ch >= 97) {
                forRet[i] = (char)((int)(ch)  - 32);
                System.out.println("before:" + ch +", After:" + forRet[i]);
            }
            else {
                forRet[i] = (char)((int)(ch)  + 32);
                System.out.println("before:" + ch +", After:" + forRet[i]);
            }
        }
        return new String(forRet);
    }

}
