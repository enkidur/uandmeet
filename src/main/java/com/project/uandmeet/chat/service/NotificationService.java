package com.project.uandmeet.chat.service;


import com.project.uandmeet.auth.UserDetailsImpl;
import com.project.uandmeet.chat.dto.NotificationDto;
import com.project.uandmeet.chat.model.ChatMessage;
import com.project.uandmeet.chat.model.InvitedMembers;
import com.project.uandmeet.chat.repository.ChatMessageJpaRepository;
import com.project.uandmeet.chat.repository.InvitedMembersRepository;
import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.model.Board;
import com.project.uandmeet.repository.BoardRepository;
import com.project.uandmeet.util.NotificationComparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.project.uandmeet.exception.ErrorCode.BOARD_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final InvitedMembersRepository invitedMembersRepository;
    private final ChatMessageJpaRepository chatMessageJpaRepository;

    private final BoardRepository boardRepository;

    @Transactional
    public List<NotificationDto> getNotification(UserDetailsImpl userDetails) {
        Long userId = userDetails.getMember().getId();
        Boolean readCheck = false;

        List<NotificationDto> notificationDtoList = new ArrayList<>();
        List<InvitedMembers> invitedmembers = invitedMembersRepository.findAllByMemberIdAndReadCheck(userId, readCheck);

        for (InvitedMembers invitedMember : invitedmembers) {
            List<ChatMessage> findChatMessageDtoList = chatMessageJpaRepository.findAllByRoomId(String.valueOf(invitedMember.getPostId()));
            for (ChatMessage findChatMessageDto : findChatMessageDtoList) {
                if (Objects.equals(String.valueOf(invitedMember.getPostId()), findChatMessageDto.getRoomId())) {
                    if (invitedMember.getReadCheckTime().isBefore(findChatMessageDto.getCreatedAt())) {
                        Board board = boardRepository.findById(Long.valueOf(findChatMessageDto.getRoomId())).orElseThrow(
                                () -> new CustomException(BOARD_NOT_FOUND)
                        );
                        NotificationDto notificationDto = new NotificationDto();
                        if(findChatMessageDto.getMessage().isEmpty()){
                            notificationDto.setMessage("파일이 왔어요😲");
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

