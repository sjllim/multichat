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

    @RequestMapping("/chat")
    public String chat() {
        return "chat";
    }

    @RequestMapping("/")
    public String room() {
        return "room";
    }

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
