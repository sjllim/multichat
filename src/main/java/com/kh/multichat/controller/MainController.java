package com.kh.multichat.controller;

import com.google.gson.Gson;
import com.kh.multichat.vo.ChatRoom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class MainController {
    List<ChatRoom> roomList = new ArrayList<>();
    static int roomNum = 0;

    /**
     * 채팅 페이지 응답
     */
    @RequestMapping("/chat")
    public String chat() {
        return "chat";
    }

    /**
     * 채팅방 목록 페이지 응답 (메인 페이지)
     */
    @RequestMapping("/")
    public String room() {
        return "room";
    }

    /**
     * 채팅방 목록 데이터 조회
     * @param roomName 채팅방이름
     * @return roomList 채팅방목록
     */
    @ResponseBody
    @RequestMapping(value="/room/create", produces = "application/json;charset=UTF-8")
    public String createRoom(@RequestParam(value="roomName", defaultValue = "")String roomName) {

        if (!roomName.isEmpty()) {
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setRoomNum(++roomNum);
            chatRoom.setRoomName(roomName);
            roomList.add(chatRoom);
        }

        return new Gson().toJson(roomList);
    }

    /**
     * 해당 채팅방 접속 요청
     * @param roomName 채팅방 이름
     * @param roomNumber 채팅방 번호
     */
    @RequestMapping("/move/chatting")
    public String chatting(String roomName, int roomNumber, Model model) {

        List<ChatRoom> newList = roomList.stream().filter(o->o.getRoomNum() == roomNumber).collect(Collectors.toList());
        if (!newList.isEmpty()) {
            model.addAttribute("roomName", roomName);
            model.addAttribute("roomNumber", roomNumber);
            return "chat";
        } else {
            return "room";
        }
    }
}
