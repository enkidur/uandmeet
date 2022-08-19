package com.project.uandmeet.chat.repository;


import com.project.uandmeet.chat.model.ResignChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResignChatRoomJpaRepository extends JpaRepository <ResignChatRoom, Long> {
    ResignChatRoom findByRoomId(String roomId);
}
