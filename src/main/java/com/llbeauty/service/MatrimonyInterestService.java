package com.llbeauty.service;

import com.llbeauty.entity.MatrimonyInterest;
import com.llbeauty.entity.MatrimonyMatch;
import com.llbeauty.entity.User;
import com.llbeauty.enums.InterestStatus;
import com.llbeauty.exception.ResourceNotFoundException;
import com.llbeauty.repository.MatrimonyInterestRepository;
import com.llbeauty.repository.MatrimonyMatchRepository;
import com.llbeauty.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class MatrimonyInterestService {

    private final MatrimonyInterestRepository matrimonyInterestRepository;
    private final MatrimonyMatchRepository matrimonyMatchRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public MatrimonyInterestService(MatrimonyInterestRepository matrimonyInterestRepository,
                                    MatrimonyMatchRepository matrimonyMatchRepository,
                                    UserRepository userRepository,
                                    NotificationService notificationService) {
        this.matrimonyInterestRepository = matrimonyInterestRepository;
        this.matrimonyMatchRepository = matrimonyMatchRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public MatrimonyInterest sendInterest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Sender and receiver cannot be the same user");
        }

        // Check duplicate pending interest (either direction)
        boolean alreadyPending = matrimonyInterestRepository.existsBySenderIdAndReceiverIdAndStatus(senderId, receiverId, InterestStatus.PENDING) ||
                                 matrimonyInterestRepository.existsBySenderIdAndReceiverIdAndStatus(receiverId, senderId, InterestStatus.PENDING);
        if (alreadyPending) {
            throw new IllegalArgumentException("A pending interest request already exists between you and this user");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found with id: " + senderId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found with id: " + receiverId));

        MatrimonyInterest interest = new MatrimonyInterest(sender, receiver, InterestStatus.PENDING);
        MatrimonyInterest savedInterest = matrimonyInterestRepository.save(interest);

        // Notify receiver of new interest
        notificationService.createNotification(receiver, "Interest Received", "You have received a new matrimony interest request from " + sender.getName() + ".");
        notificationService.notifyInterestReceived(receiverId, senderId);  

        return savedInterest;
    }

    public MatrimonyInterest acceptInterest(Long interestId) {
        MatrimonyInterest interest = matrimonyInterestRepository.findById(interestId)
                .orElseThrow(() -> new ResourceNotFoundException("Interest request not found with id: " + interestId));

        if (interest.getStatus() == InterestStatus.ACCEPTED) {
            return interest; // Already accepted
        }

        interest.setStatus(InterestStatus.ACCEPTED);
        MatrimonyInterest savedInterest = matrimonyInterestRepository.save(interest);

        // Create MatrimonyMatch automatically
        User sender = interest.getSender();
        User receiver = interest.getReceiver();

        // Check if match already exists in either userOne/userTwo direction
        boolean matchExists = matrimonyMatchRepository.existsByUserOneIdAndUserTwoId(sender.getId(), receiver.getId()) ||
                              matrimonyMatchRepository.existsByUserOneIdAndUserTwoId(receiver.getId(), sender.getId());

        if (!matchExists) {
            MatrimonyMatch match = new MatrimonyMatch(sender, receiver);
            matrimonyMatchRepository.save(match);

            // Notify both users about the new match via NotificationService (in‑app & email)
            notificationService.notifyMatchCreated(sender.getId(), receiver.getId());
        }

        // Notify sender that their interest was accepted
        notificationService.createNotification(sender, "Interest Accepted", receiver.getName() + " accepted your interest.");
        notificationService.notifyInterestAccepted(sender.getId(), receiver.getId());

        return savedInterest;
    }

    public MatrimonyInterest rejectInterest(Long interestId) {
        MatrimonyInterest interest = matrimonyInterestRepository.findById(interestId)
                .orElseThrow(() -> new ResourceNotFoundException("Interest request not found with id: " + interestId));

        interest.setStatus(InterestStatus.REJECTED);
        return matrimonyInterestRepository.save(interest);
    }

    @Transactional(readOnly = true)
    public List<MatrimonyInterest> getReceivedInterests(Long userId) {
        return matrimonyInterestRepository.findByReceiverId(userId);
    }

    @Transactional(readOnly = true)
    public List<MatrimonyInterest> getSentInterests(Long userId) {
        return matrimonyInterestRepository.findBySenderId(userId);
    }
}

