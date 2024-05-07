package com.kh.multichat.handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SocketHandler extends TextWebSocketHandler {

    // 웹소켓 세션 관리
    // Map<String, WebSocketSession> sessionMap = new HashMap<>();
    // 웹소켓 세션을 담아둘 리스트
    List<Map<String, Object>> rls = new ArrayList<>();

    // 메시지 발송
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String msg = message.getPayload();
        JsonObject obj = jsonToObjectParser(msg);

        String rN = obj.get("roomNumber").getAsString();
        Map<String, Object> temp = new HashMap<>();
        if (!rls.isEmpty()) {
            for (Map<String, Object> rl : rls) {
                String roomNumber = rl.get("roomNumber").toString();

                if (roomNumber.equals(rN)) {
                    temp = rl;
                    break;
                }
            }

            for (String k : temp.keySet()) {
                if (k.equals("roomNumber")) {
                    continue;
                }

                WebSocketSession wss = (WebSocketSession) temp.get(k);
                if (wss != null) {
                    wss.sendMessage(new TextMessage(obj.toString()));
                }
            }
        }
    }

    // 소켓 연결
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        boolean flag = false;

        String url = session.getUri().toString();

        String roomNumber = url.split("/chatting/")[1];

        int idx = rls.size();
        if (!rls.isEmpty()) {

            for (int i=0; i<rls.size(); i++) {
                String rN = rls.get(i).get("roomNumber").toString();
                if (rN.equals(roomNumber)) {
                    flag = true;
                    idx = i;
                    break;
                }
            }
        }

        if (flag) {
            Map<String, Object> map = rls.get(idx);
            map.put(session.getId(), session);
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("roomNumber", roomNumber);
            map.put(session.getId(), session);
            rls.add(map);
        }

        JsonObject obj = new JsonObject();
        obj.addProperty("type", "getId");
        obj.addProperty("sessionId", session.getId());

        session.sendMessage(new TextMessage(obj.toString()));
    }

    // 소켓 종료
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if (!rls.isEmpty()) {
            for (Map<String, Object> rl : rls) {
                rl.remove(session.getId());
            }
        }
        super.afterConnectionClosed(session, status);
    }

    // Json parsing
    private static JsonObject jsonToObjectParser(String jsonStr) {
        JsonParser parser = new JsonParser();
        JsonObject obj = null;

        obj = (JsonObject) parser.parse(jsonStr);
        return obj;
    }
}
