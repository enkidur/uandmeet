package com.project.uandmeet.notification;

import com.project.uandmeet.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Notification findByIdAndReceiver(Long notificationId, Member member);

    List<Notification> findAllByReceiver(Member member);
    boolean existsByReceiverAndIsRead(Member member, Boolean isRead);
}
