package com.project.uandmeet.chat.service;

import com.project.uandmeet.chat.dto.NotificationDto;
import com.project.uandmeet.chat.model.ChatMessage;
import com.project.uandmeet.chat.model.InvitedUsers;
import com.project.uandmeet.chat.repository.ChatMessageJpaRepository;
import com.project.uandmeet.chat.repository.InvitedUsersRepository;
import com.project.uandmeet.model.Board;
import com.project.uandmeet.repository.BoardRepository;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.util.NotificationComparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final InvitedUsersRepository invitedUsersRepository;
    private final ChatMessageJpaRepository chatMessageJpaRepository;

    private final BoardRepository postRepository;

    @Transactional
    public List<NotificationDto> getNotification(UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMember().getId();
        Boolean readCheck = false;

        List<NotificationDto> notificationDtoList = new ArrayList<>();
        List<InvitedUsers> invitedUsers = invitedUsersRepository.findAllByMemberIdAndReadCheck(memberId, readCheck);

        for (InvitedUsers invitedUser : invitedUsers) {
            List<ChatMessage> findChatMessageDtoList = chatMessageJpaRepository.findAllByRoomId(String.valueOf(invitedUser.getBoardId()));
            for (ChatMessage findChatMessageDto : findChatMessageDtoList) {
                if (Objects.equals(String.valueOf(invitedUser.getBoardId()), findChatMessageDto.getRoomId())) {
                    if (invitedUser.getReadCheckTime().isBefore(findChatMessageDto.getCreatedAt())) {
                        Board board = postRepository.findById(Long.valueOf(findChatMessageDto.getRoomId())).orElseThrow(
                                () -> new IllegalArgumentException("존재하지 않는 게시물 입니다.")
                        );
                        NotificationDto notificationDto = new NotificationDto();
                        if(findChatMessageDto.getMessage().isEmpty()){
                            notificationDto.setMessage("왔나요?");
                        }else {
                            notificationDto.setMessage(findChatMessageDto.getMessage());
                        }
                        notificationDto.setNickname(findChatMessageDto.getSender());
                        notificationDto.setCreatedAt(findChatMessageDto.getCreatedAt());
                        notificationDto.setRoomId(findChatMessageDto.getRoomId());
                        notificationDto.setTitle(board.getTitle());
                        notificationDtoList.add(notificationDto);
                    }
                }
            }
        }
        notificationDtoList.sort(new NotificationComparator());
        return notificationDtoList;
    }
}

