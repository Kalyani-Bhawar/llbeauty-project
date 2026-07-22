package com.llbeauty.service;

import com.llbeauty.entity.MatrimonyMatch;
import com.llbeauty.entity.User;
import com.llbeauty.repository.MatrimonyMatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class MatrimonyMatchService {

    private final MatrimonyMatchRepository matrimonyMatchRepository;

    public MatrimonyMatchService(MatrimonyMatchRepository matrimonyMatchRepository) {
        this.matrimonyMatchRepository = matrimonyMatchRepository;
    }

    public MatrimonyMatch createMatch(User userOne, User userTwo) {
        if (userOne == null || userTwo == null) {
            throw new IllegalArgumentException("Users must not be null");
        }

        // Check if match already exists in either direction
        boolean matchExists = matrimonyMatchRepository.existsByUserOneIdAndUserTwoId(userOne.getId(), userTwo.getId()) ||
                              matrimonyMatchRepository.existsByUserOneIdAndUserTwoId(userTwo.getId(), userOne.getId());

        if (matchExists) {
            List<MatrimonyMatch> existingMatches = matrimonyMatchRepository.findByUserOneIdOrUserTwoId(userOne.getId(), userOne.getId());
            for (MatrimonyMatch match : existingMatches) {
                if ((match.getUserOne().getId().equals(userOne.getId()) && match.getUserTwo().getId().equals(userTwo.getId())) ||
                    (match.getUserOne().getId().equals(userTwo.getId()) && match.getUserTwo().getId().equals(userOne.getId()))) {
                    return match;
                }
            }
        }

        MatrimonyMatch match = new MatrimonyMatch(userOne, userTwo);
        return matrimonyMatchRepository.save(match);
    }

    @Transactional(readOnly = true)
    public boolean isMatchExists(Long userOneId, Long userTwoId) {
        return matrimonyMatchRepository.existsByUserOneIdAndUserTwoId(userOneId, userTwoId) ||
               matrimonyMatchRepository.existsByUserOneIdAndUserTwoId(userTwoId, userOneId);
    }

    @Transactional(readOnly = true)
    public List<MatrimonyMatch> getUserMatches(Long userId) {
        return matrimonyMatchRepository.findByUserOneIdOrUserTwoId(userId, userId);
    }
}
