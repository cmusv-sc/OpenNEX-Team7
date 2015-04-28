package controllers;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Arrays;

/**
 * Created by shbekti on 4/28/15.
 */
public class DummyServices extends Controller {

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

        int length = lines.length;
        for(int i=0; i<length; i++) {
            lines[i] = shift(lines[i]);
        }

        Arrays.sort(lines);

        String joinedString = StringUtils.join(lines, "\n");

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
