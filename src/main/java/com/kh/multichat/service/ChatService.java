package com.kh.multichat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatService {

    public void saveTest(String data) {
      log.info("save data ... {}", data);
    }
}
