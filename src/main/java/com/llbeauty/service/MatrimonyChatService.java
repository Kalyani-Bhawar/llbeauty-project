package com.llbeauty.service;

import com.llbeauty.entity.MatrimonyChatMessage;
import com.llbeauty.repository.MatrimonyChatRepository;
import com.llbeauty.entity.MatrimonyMatch;
import com.llbeauty.repository.MatrimonyMatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class MatrimonyChatService {

    private final MatrimonyChatRepository matrimonyChatRepository;
    private final MatrimonyMatchRepository matrimonyMatchRepository;

    public MatrimonyChatService(MatrimonyChatRepository matrimonyChatRepository,
                                MatrimonyMatchRepository matrimonyMatchRepository) {
        this.matrimonyChatRepository = matrimonyChatRepository;
        this.matrimonyMatchRepository = matrimonyMatchRepository;
    }

    @Transactional(readOnly = true)
    public boolean canChat(Long userOneId, Long userTwoId) {
        if (userOneId == null || userTwoId == null) {
            return false;
        }
        return matrimonyMatchRepository.existsByUserOneIdAndUserTwoId(userOneId, userTwoId) ||
               matrimonyMatchRepository.existsByUserOneIdAndUserTwoId(userTwoId, userOneId);
    }

    public MatrimonyChatMessage saveMessage(MatrimonyChatMessage message) {
        if (message == null || message.getMatch() == null || message.getSender() == null || message.getReceiver() == null) {
            throw new IllegalArgumentException("Message, match, sender, and receiver details must not be null");
        }

        Long matchId = message.getMatch().getId();
        Long senderId = message.getSender().getId();
        Long receiverId = message.getReceiver().getId();

        // Check match exists
        MatrimonyMatch match = matrimonyMatchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match does not exist with id: " + matchId));

        // Validate sender and receiver are matched in this specific Match
        boolean senderIsUserOne = match.getUserOne().getId().equals(senderId);
        boolean senderIsUserTwo = match.getUserTwo().getId().equals(senderId);
        boolean receiverIsUserOne = match.getUserOne().getId().equals(receiverId);
        boolean receiverIsUserTwo = match.getUserTwo().getId().equals(receiverId);

        if (!((senderIsUserOne && receiverIsUserTwo) || (senderIsUserTwo && receiverIsUserOne))) {
            throw new IllegalArgumentException("Users in message do not match the users associated with this Match");
        }

        // Validate sender and receiver are generally matched
        if (!canChat(senderId, receiverId)) {
            throw new IllegalArgumentException("Users are not matched and cannot exchange messages");
        }

        return matrimonyChatRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<MatrimonyChatMessage> getChatHistory(Long matchId) {
        return matrimonyChatRepository.findByMatchIdOrderByTimestampAsc(matchId);
    }
}
