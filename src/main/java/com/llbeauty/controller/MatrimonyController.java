package com.llbeauty.controller;

import com.llbeauty.dto.MatrimonyProfileDTO;
import com.llbeauty.entity.MatrimonyChatMessage;
import com.llbeauty.entity.MatrimonyInterest;
import com.llbeauty.entity.MatrimonyMatch;
import com.llbeauty.entity.MatrimonyProfile;
import com.llbeauty.entity.User;
import com.llbeauty.enums.ProfileStatus;
import com.llbeauty.enums.InterestStatus;
import com.llbeauty.exception.ResourceNotFoundException;
import com.llbeauty.repository.UserRepository;
import com.llbeauty.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/matrimony")
public class MatrimonyController {

    private static final Logger log = LoggerFactory.getLogger(MatrimonyController.class);

    private final MatrimonyProfileService matrimonyProfileService;
    private final MatrimonyInterestService matrimonyInterestService;
    private final MatrimonyMatchService matrimonyMatchService;
    private final MatrimonySearchService matrimonySearchService;
    private final MatrimonyChatService matrimonyChatService;
    private final UserRepository userRepository;

    public MatrimonyController(MatrimonyProfileService matrimonyProfileService,
                               MatrimonyInterestService matrimonyInterestService,
                               MatrimonyMatchService matrimonyMatchService,
                               MatrimonySearchService matrimonySearchService,
                               MatrimonyChatService matrimonyChatService,
                               UserRepository userRepository) {
        this.matrimonyProfileService = matrimonyProfileService;
        this.matrimonyInterestService = matrimonyInterestService;
        this.matrimonyMatchService = matrimonyMatchService;
        this.matrimonySearchService = matrimonySearchService;
        this.matrimonyChatService = matrimonyChatService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return userRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        }
        throw new ResourceNotFoundException("User not authenticated");
    }

    // ==========================================
    //  LANDING / DASHBOARD PAGE
    // ==========================================
    @GetMapping
    public String landingPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
        	return "redirect:/auth/login?redirect=/matrimony";
        }
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        Optional<MatrimonyProfile> profileOpt = matrimonyProfileService.getProfileByUserId(user.getId());
        if (profileOpt.isPresent()) {
            MatrimonyProfile profile = profileOpt.get();
            model.addAttribute("profile", profile);
            model.addAttribute("completionPercentage", matrimonyProfileService.calculateProfileCompletion(profile));
        } else {
            model.addAttribute("profile", null);
        }
        return "eva_matrimony";
    }

    // ==========================================
    //  PROFILE CREATION WIZARD (GET mappings)
    // ==========================================
    @GetMapping("/create-profile")
    public String createProfile(RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        Optional<MatrimonyProfile> profileOpt = matrimonyProfileService.getProfileByUserId(user.getId());
        if (profileOpt.isPresent()) {
            return "redirect:/matrimony/profile/step1";
        }
        // Initialize an empty profile for the user
        MatrimonyProfile profile = new MatrimonyProfile();
        profile.setUser(user);
        profile.setStatus(ProfileStatus.PENDING); // Will save as pending or temp status
        try {
            matrimonyProfileService.createProfile(profile);
        } catch (Exception e) {
            // Already exists or other issue
            log.info("Profile initialization: {}", e.getMessage());
        }
        return "redirect:/matrimony/profile/step1";
    }

    @GetMapping("/profile/step1")
    public String getStep1(Model model) {
        User user = getAuthenticatedUser();
        MatrimonyProfile profile = matrimonyProfileService.getProfileByUserId(user.getId())
                .orElseGet(() -> {
                    MatrimonyProfile p = new MatrimonyProfile();
                    p.setUser(user);
                    return matrimonyProfileService.createProfile(p);
                });
        model.addAttribute("profile", profile);
        return "matrimony_create_step1";
    }

    @GetMapping("/profile/step2")
    public String getStep2(Model model) {
        User user = getAuthenticatedUser();
        MatrimonyProfile profile = matrimonyProfileService.getProfileByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found. Please complete Step 1 first."));
        model.addAttribute("profile", profile);
        return "matrimony_create_step2";
    }

    @GetMapping("/profile/step3")
    public String getStep3(Model model) {
        User user = getAuthenticatedUser();
        MatrimonyProfile profile = matrimonyProfileService.getProfileByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found. Please complete Step 1 first."));
        model.addAttribute("profile", profile);
        return "matrimony_create_step3";
    }

    @GetMapping("/profile/step4")
    public String getStep4(Model model) {
        User user = getAuthenticatedUser();
        MatrimonyProfile profile = matrimonyProfileService.getProfileByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found. Please complete Step 1 first."));
        model.addAttribute("profile", profile);
        return "matrimony_create_step4";
    }

    @GetMapping("/profile/step5")
    public String getStep5(Model model) {
        User user = getAuthenticatedUser();
        MatrimonyProfile profile = matrimonyProfileService.getProfileByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found. Please complete Step 1 first."));
        model.addAttribute("profile", profile);
        return "matrimony_create_step5";
    }

    @GetMapping("/profile/step6")
    public String getStep6(Model model) {
        User user = getAuthenticatedUser();
        MatrimonyProfile profile = matrimonyProfileService.getProfileByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found. Please complete Step 1 first."));
        model.addAttribute("profile", profile);
        return "matrimony_create_step6";
    }

    // ==========================================
    //  PROFILE CREATION WIZARD (POST mappings)
    // ==========================================
    @PostMapping("/profile/step1")
    public String saveStep1(@ModelAttribute MatrimonyProfileDTO dto, RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        MatrimonyProfile profile = matrimonyProfileService.getProfileByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        profile.setFullName(dto.getFullName());
        profile.setDateOfBirth(dto.getDateOfBirth());
        profile.setGender(dto.getGender());
        profile.setHeight(dto.getHeight());
        profile.setMotherTongue(dto.getMotherTongue());
        profile.setCity(dto.getCity());
        profile.setState(dto.getState());
        profile.setCountry(dto.getCountry());
        matrimonyProfileService.updateProfile(profile.getId(), profile);
        redirectAttributes.addFlashAttribute("successMessage", "Step 1 saved successfully!");
        return "redirect:/matrimony/profile/step2";
    }

    @PostMapping("/profile/step2")
    public String saveStep2(@ModelAttribute MatrimonyProfileDTO dto, RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        MatrimonyProfile profile = matrimonyProfileService.getProfileByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        profile.setReligion(dto.getReligion());
        profile.setCaste(dto.getCaste());
        profile.setSubCaste(dto.getSubCaste());
        matrimonyProfileService.updateProfile(profile.getId(), profile);
        redirectAttributes.addFlashAttribute("successMessage", "Step 2 saved successfully!");
        return "redirect:/matrimony/profile/step3";
    }

    @PostMapping("/profile/step3")
    public String saveStep3(@ModelAttribute MatrimonyProfileDTO dto, RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        MatrimonyProfile profile = matrimonyProfileService.getProfileByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        profile.setEducation(dto.getEducation());
        profile.setOccupation(dto.getOccupation());
        profile.setCompanyName(dto.getCompanyName());
        profile.setAnnualIncome(dto.getAnnualIncome());
        matrimonyProfileService.updateProfile(profile.getId(), profile);
        redirectAttributes.addFlashAttribute("successMessage", "Step 3 saved successfully!");
        return "redirect:/matrimony/profile/step4";
    }

    @PostMapping("/profile/step4")
    public String saveStep4(@ModelAttribute MatrimonyProfileDTO dto, RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        MatrimonyProfile profile = matrimonyProfileService.getProfileByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        profile.setFatherOccupation(dto.getFatherOccupation());
        profile.setMotherOccupation(dto.getMotherOccupation());
        profile.setBrotherCount(dto.getBrotherCount());
        profile.setSisterCount(dto.getSisterCount());
        profile.setFamilyType(dto.getFamilyType());
        matrimonyProfileService.updateProfile(profile.getId(), profile);
        redirectAttributes.addFlashAttribute("successMessage", "Step 4 saved successfully!");
        return "redirect:/matrimony/profile/step5";
    }

    @PostMapping("/profile/step5")
    public String saveStep5(@ModelAttribute MatrimonyProfileDTO dto, RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        MatrimonyProfile profile = matrimonyProfileService.getProfileByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        profile.setAboutMe(dto.getAboutMe());
        matrimonyProfileService.updateProfile(profile.getId(), profile);
        redirectAttributes.addFlashAttribute("successMessage", "Step 5 saved successfully!");
        return "redirect:/matrimony/profile/step6";
    }

    @PostMapping("/profile/step6")
    public String saveStep6(@ModelAttribute MatrimonyProfileDTO dto, RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        MatrimonyProfile profile = matrimonyProfileService.getProfileByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        
        if (dto.getProfilePhoto() == null || dto.getProfilePhoto().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "At least one profile photo is required.");
            return "redirect:/matrimony/profile/step6";
        }
        
        profile.setProfilePhoto(dto.getProfilePhoto());
        matrimonyProfileService.updateProfile(profile.getId(), profile);
        redirectAttributes.addFlashAttribute("successMessage", "Step 6 saved successfully! Review and submit your profile.");
        return "redirect:/matrimony/my-profile";
    }

    @PostMapping("/profile/submit")
    public String submitProfile(RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        MatrimonyProfile profile = matrimonyProfileService.getProfileByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        
        if (profile.getProfilePhoto() == null || profile.getProfilePhoto().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please complete all steps (including photo upload) before submitting.");
            return "redirect:/matrimony/profile/step6";
        }

        profile.setStatus(ProfileStatus.PENDING);
        matrimonyProfileService.updateProfile(profile.getId(), profile);
        redirectAttributes.addFlashAttribute("successMessage", "Your EVA Matrimony profile has been submitted for verification.");
        return "redirect:/matrimony";
    }

    // ==========================================
    //  VIEW / EDIT OWN PROFILE
    // ==========================================
    @GetMapping("/my-profile")
    public String viewMyProfile(Model model) {
        User user = getAuthenticatedUser();
        MatrimonyProfile profile = matrimonyProfileService.getProfileByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Matrimony profile not found"));
        model.addAttribute("profile", profile);
        model.addAttribute("completionPercentage", matrimonyProfileService.calculateProfileCompletion(profile));
        return "matrimony_profile";
    }

    @GetMapping("/edit-profile")
    public String editProfile() {
        return "redirect:/matrimony/profile/step1";
    }

    // ==========================================
    //  SEARCH / MATRIMONY DISCOVERY
    // ==========================================
    @GetMapping("/search")
    public String searchProfiles(
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "minAge", required = false) Integer minAge,
            @RequestParam(value = "maxAge", required = false) Integer maxAge,
            @RequestParam(value = "religion", required = false) String religion,
            @RequestParam(value = "education", required = false) String education,
            @RequestParam(value = "occupation", required = false) String occupation,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model
    ) {
        User user = getAuthenticatedUser();
        // Check if current user has an approved profile first
        Optional<MatrimonyProfile> ownProfileOpt = matrimonyProfileService.getProfileByUserId(user.getId());
        if (ownProfileOpt.isEmpty() || ownProfileOpt.get().getStatus() != ProfileStatus.APPROVED) {
            model.addAttribute("notApproved", true);
            return "matrimony_search";
        }

        Page<MatrimonyProfile> profiles = matrimonySearchService.searchProfiles(
                city, gender, minAge, maxAge, religion, education, occupation, PageRequest.of(page, 12)
        );
        
        // Exclude current user from search result
        List<MatrimonyProfile> filteredList = profiles.getContent().stream()
                .filter(p -> !p.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());

        // Attach interest map to show if user already sent/received interest
        List<MatrimonyInterest> sentInterests = matrimonyInterestService.getSentInterests(user.getId());
        Map<Long, String> interestMap = new HashMap<>();
        for (MatrimonyInterest mi : sentInterests) {
            interestMap.put(mi.getReceiver().getId(), mi.getStatus().name());
        }

        model.addAttribute("profiles", filteredList);
        model.addAttribute("interestMap", interestMap);
        model.addAttribute("city", city);
        model.addAttribute("gender", gender);
        model.addAttribute("minAge", minAge);
        model.addAttribute("maxAge", maxAge);
        model.addAttribute("religion", religion);
        model.addAttribute("education", education);
        model.addAttribute("occupation", occupation);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", profiles.getTotalPages());
        return "matrimony_search";
    }

    @GetMapping("/view-profile/{id}")
    public String viewProfile(@PathVariable("id") Long id, Model model) {
        User currentUser = getAuthenticatedUser();
        MatrimonyProfile profile = matrimonyProfileService.getProfileByUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matrimony profile not found"));

        if (profile.getStatus() != ProfileStatus.APPROVED && !profile.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("This profile is not active or approved");
        }

        // Check if interest sent
        List<MatrimonyInterest> sent = matrimonyInterestService.getSentInterests(currentUser.getId());
        Optional<MatrimonyInterest> sentInterest = sent.stream()
                .filter(i -> i.getReceiver().getId().equals(id))
                .findFirst();

        // Check match status
        List<MatrimonyMatch> matches = matrimonyMatchService.getUserMatches(currentUser.getId());
        boolean isMatched = matches.stream().anyMatch(m ->
                m.getUserOne().getId().equals(profile.getUser().getId()) ||
                m.getUserTwo().getId().equals(profile.getUser().getId())
        );

        model.addAttribute("profile", profile);
        model.addAttribute("sentInterest", sentInterest.orElse(null));
        model.addAttribute("isMatched", isMatched);
        return "matrimony_view_profile";
    }

    // ==========================================
    //  INTERESTS MANAGEMENT
    // ==========================================
    @GetMapping("/interests")
    public String getInterests(Model model) {
        User user = getAuthenticatedUser();
        List<MatrimonyInterest> received = matrimonyInterestService.getReceivedInterests(user.getId());
        List<MatrimonyInterest> sent = matrimonyInterestService.getSentInterests(user.getId());

        model.addAttribute("receivedInterests", received);
        model.addAttribute("sentInterests", sent);
        return "matrimony_interests";
    }

    @PostMapping("/interests/send/{receiverId}")
    public String sendInterest(@PathVariable("receiverId") Long receiverId, RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        try {
            matrimonyInterestService.sendInterest(user.getId(), receiverId);
            redirectAttributes.addFlashAttribute("successMessage", "Interest request sent successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/matrimony/view-profile/" + receiverId;
    }

    @PostMapping("/interests/accept/{id}")
    public String acceptInterest(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            matrimonyInterestService.acceptInterest(id);
            redirectAttributes.addFlashAttribute("successMessage", "Interest accepted! You are now matched.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/matrimony/interests";
    }

    @PostMapping("/interests/reject/{id}")
    public String rejectInterest(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            matrimonyInterestService.rejectInterest(id);
            redirectAttributes.addFlashAttribute("successMessage", "Interest request declined.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/matrimony/interests";
    }

    // ==========================================
    //  MATCHES & CHAT
    // ==========================================
    @GetMapping("/matches")
    public String getMatches(Model model) {
        User user = getAuthenticatedUser();
        List<MatrimonyMatch> matches = matrimonyMatchService.getUserMatches(user.getId());
        model.addAttribute("matches", matches);
        return "matrimony_matches";
    }

    @GetMapping("/chat")
    public String openChat(@RequestParam("matchId") Long matchId, Model model) {
        User user = getAuthenticatedUser();
        // Validate match exists and user is part of it
        List<MatrimonyMatch> matches = matrimonyMatchService.getUserMatches(user.getId());
        MatrimonyMatch currentMatch = matches.stream()
                .filter(m -> m.getId().equals(matchId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid match or unauthorized chat access"));

        User partner = currentMatch.getUserOne().getId().equals(user.getId()) ? currentMatch.getUserTwo() : currentMatch.getUserOne();
        MatrimonyProfile partnerProfile = matrimonyProfileService.getProfileByUserId(partner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Partner profile not found"));

        List<MatrimonyChatMessage> chatHistory = matrimonyChatService.getChatHistory(matchId);

        model.addAttribute("match", currentMatch);
        model.addAttribute("partner", partner);
        model.addAttribute("partnerProfile", partnerProfile);
        model.addAttribute("chatHistory", chatHistory);
        return "matrimony_chat";
    }

    @PostMapping("/chat/send")
    public String sendMessage(
            @RequestParam("matchId") Long matchId,
            @RequestParam("receiverId") Long receiverId,
            @RequestParam("message") String messageText,
            RedirectAttributes redirectAttributes
    ) {
        User user = getAuthenticatedUser();
        try {
            MatrimonyMatch match = new MatrimonyMatch();
            match.setId(matchId);

            User receiver = new User();
            receiver.setId(receiverId);

            MatrimonyChatMessage msg = new MatrimonyChatMessage();
            msg.setMatch(match);
            msg.setSender(user);
            msg.setReceiver(receiver);
            msg.setMessage(messageText);

            matrimonyChatService.saveMessage(msg);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to send message: " + e.getMessage());
        }
        return "redirect:/matrimony/chat?matchId=" + matchId;
    }

    @GetMapping("/chat/history/{matchId}")
    @ResponseBody
    public ResponseEntity<?> getChatHistoryJson(@PathVariable("matchId") Long matchId) {
        User user = getAuthenticatedUser();
        List<MatrimonyMatch> matches = matrimonyMatchService.getUserMatches(user.getId());
        boolean authorized = matches.stream().anyMatch(m -> m.getId().equals(matchId));
        if (!authorized) {
            return ResponseEntity.status(403).body("Unauthorized");
        }

        List<MatrimonyChatMessage> chatHistory = matrimonyChatService.getChatHistory(matchId);
        List<Map<String, Object>> response = chatHistory.stream().map(msg -> {
            Map<String, Object> map = new HashMap<>();
            map.put("senderId", msg.getSender().getId());
            map.put("senderName", msg.getSender().getName());
            map.put("message", msg.getMessage());
            map.put("timestamp", msg.getTimestamp().toString());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
