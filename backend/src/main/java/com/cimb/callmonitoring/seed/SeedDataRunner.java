package com.cimb.callmonitoring.seed;

import com.cimb.callmonitoring.entity.CallRecord;
import com.cimb.callmonitoring.entity.User;
import com.cimb.callmonitoring.repository.CallRecordRepository;
import com.cimb.callmonitoring.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class SeedDataRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedDataRunner.class);

    private static final int RECORD_COUNT = 100;
    private static final int SPAN_MONTHS = 6;

    private static final List<String> CS_NAMES = List.of(
            "Siti Aminah", "Budi Santoso", "Dewi Lestari", "Agus Wijaya", "Rina Kartika",
            "Joko Susilo", "Maya Anggraini", "Andi Prasetyo", "Sri Wahyuni", "Rudi Hartono",
            "Fitri Handayani", "Hendra Gunawan", "Nina Marlina", "Tono Suharto", "Yuni Astuti",
            "Eko Nugroho", "Ratna Dewi", "Bambang Hermawan", "Indah Permata", "Rizky Ramadhan"
    );

    private static final List<String> CUSTOMER_NAMES = List.of(
            "Ahmad Fauzi", "Maria Susanti", "Lukman Hakim", "Putri Amelia", "Yusuf Maulana",
            "Sari Dewi", "Fajar Sidik", "Lina Marliana", "Dedi Kurniawan", "Nurul Aini",
            "Bima Sakti", "Citra Ayu", "Dian Puspita", "Eka Yulia", "Gilang Pratama",
            "Hana Safitri", "Irwan Saputra", "Julia Rahma", "Kevin Hartanto", "Laila Fitria",
            "Muhammad Rizki", "Nadia Rahayu", "Oscar Wijaya", "Poppy Lestari", "Qori Ananda",
            "Reni Oktaviani", "Slamet Riyadi", "Tari Kusuma", "Umar Bakri", "Vina Melinda"
    );

    private final CallRecordRepository callRecordRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    public SeedDataRunner(CallRecordRepository callRecordRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.callRecordRepository = callRecordRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedAdminUser();
        seedCallRecords();
    }

    private void seedAdminUser() {
        if (userRepository.existsByUsername("admin")) {
            return;
        }
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        userRepository.save(admin);
        log.info("Seeded admin user");
    }

    private void seedCallRecords() {
        if (callRecordRepository.count() > 0) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minus(SPAN_MONTHS, ChronoUnit.MONTHS);
        long totalMillis = ChronoUnit.MILLIS.between(start, now);

        for (int i = 0; i < RECORD_COUNT; i++) {
            CallRecord record = new CallRecord();
            record.setCallId(UUID.randomUUID());
            record.setCallTimestamp(start.plusMillis((long) (random.nextDouble() * totalMillis)));
            record.setCsName(CS_NAMES.get(random.nextInt(CS_NAMES.size())));
            record.setCustomerName(CUSTOMER_NAMES.get(random.nextInt(CUSTOMER_NAMES.size())));
            record.setSentimentScore(sentimentScoreWithWeighting());
            callRecordRepository.save(record);
        }
        log.info("Seeded {} call records", RECORD_COUNT);
    }

    private Short sentimentScoreWithWeighting() {
        // ~60% below 70, ~40% at or above 70
        if (random.nextDouble() < 0.6) {
            return (short) (random.nextInt(70) + 1); // 1..69
        }
        return (short) (random.nextInt(31) + 70); // 70..100
    }
}