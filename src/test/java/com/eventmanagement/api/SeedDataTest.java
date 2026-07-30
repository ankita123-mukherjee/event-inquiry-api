package com.eventmanagement.api;

import com.eventmanagement.api.entity.EventInquiry;
import com.eventmanagement.api.entity.EventType;
import com.eventmanagement.api.entity.InquiryStatus;
import com.eventmanagement.api.entity.Role;
import com.eventmanagement.api.entity.User;
import com.eventmanagement.api.repository.EventInquiryRepository;
import com.eventmanagement.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
class SeedDataTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventInquiryRepository inquiryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Transactional
    @Rollback(false)
    void seedThousandSampleInquiries() {
        long currentInquiries = inquiryRepository.count();
        System.out.println("CURRENT_INQUIRY_COUNT_BEFORE_SEED = " + currentInquiries);

        // Fetch or create a default user for seeding
        User defaultUser = userRepository.findByEmail("demo@example.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .fullName("Demo User")
                        .email("demo@example.com")
                        .password(passwordEncoder.encode("Password123!"))
                        .role(Role.ROLE_USER)
                        .build()));

        User adminUser = userRepository.findByEmail("admin@example.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .fullName("Admin User")
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("AdminPass123!"))
                        .role(Role.ROLE_ADMIN)
                        .build()));

        List<User> users = List.of(defaultUser, adminUser);
        Random random = new Random();

        String[] locations = {
                "Grand Ballroom Plaza, New York", "Sunset Beach Resort, Miami",
                "Royal Heritage Hall, London", "Metropolitan Convention Center, Chicago",
                "Bayview Pavilion, San Francisco", "Starlight Banquet Garden, Austin",
                "Crystal Lake Manor, Seattle", "Skyline Terrace Rooftop, Los Angeles"
        };

        String[] customerNames = {
                "Sophia Martinez", "Liam Johnson", "Emma Williams", "Noah Brown",
                "Olivia Jones", "Ethan Garcia", "Ava Miller", "Jackson Davis",
                "Isabella Rodriguez", "Aiden Martinez", "Mia Hernandez", "Lucas Lopez"
        };

        String[] requests = {
                "Vegetarian & Vegan catering required", "Need AV setup with 4K projectors",
                "Live acoustic band & DJ stage setup", "Valet parking for 150 vehicles",
                "Floral decor in pastel themes", "Wheelchair accessible venue requirement",
                "Custom cocktail bar & mocktail station", "Photography & videography team included"
        };

        EventType[] eventTypes = EventType.values();
        InquiryStatus[] statuses = InquiryStatus.values();

        List<EventInquiry> inquiriesToSave = new ArrayList<>();
        int targetRecords = 1000;

        for (int i = 1; i <= targetRecords; i++) {
            User owner = users.get(random.nextInt(users.size()));
            String name = customerNames[random.nextInt(customerNames.length)];
            EventType type = eventTypes[random.nextInt(eventTypes.length)];
            InquiryStatus status = statuses[random.nextInt(statuses.length)];
            String loc = locations[random.nextInt(locations.length)];
            String req = requests[random.nextInt(requests.length)];

            BigDecimal budget = BigDecimal.valueOf(5000 + random.nextInt(145000));
            int guests = 20 + random.nextInt(980);
            LocalDate eventDate = LocalDate.now().plusDays(random.nextInt(365));

            EventInquiry inquiry = EventInquiry.builder()
                    .user(owner)
                    .customerName(name + " #" + i)
                    .customerEmail("customer" + i + "@sample-events.com")
                    .customerPhone("+1555" + String.format("%07d", i % 10000000))
                    .eventType(type)
                    .eventDate(eventDate)
                    .location(loc)
                    .estimatedBudget(budget)
                    .guestCount(guests)
                    .status(status)
                    .specialRequests(req)
                    .createdAt(LocalDateTime.now().minusDays(random.nextInt(60)))
                    .updatedAt(LocalDateTime.now())
                    .build();

            inquiriesToSave.add(inquiry);
        }

        inquiryRepository.saveAll(inquiriesToSave);

        long finalCount = inquiryRepository.count();
        System.out.println("FINAL_INQUIRY_COUNT_AFTER_SEED = " + finalCount);
    }
}
