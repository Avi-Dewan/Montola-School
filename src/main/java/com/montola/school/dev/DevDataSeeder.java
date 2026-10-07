package com.montola.school.dev;

import com.montola.school.auth.enums.Role;
import com.montola.school.auth.model.User;
import com.montola.school.auth.repository.UserRepository;
import com.montola.school.auth.security.CustomUserDetails;
import com.montola.school.course.dto.ChapterRequestDto;
import com.montola.school.course.dto.ChapterResponseDto;
import com.montola.school.course.dto.LectureRequestDto;
import com.montola.school.course.dto.QuizOptionRequestDto;
import com.montola.school.course.dto.QuizQuestionRequestDto;
import com.montola.school.course.dto.QuizRequestDto;
import com.montola.school.course.dto.SubjectRequestDto;
import com.montola.school.course.dto.SubjectResponseDto;
import com.montola.school.course.dto.TopicRequestDto;
import com.montola.school.course.dto.TopicResponseDto;
import com.montola.school.course.enums.ChapterStatus;
import com.montola.school.course.enums.QuestionType;
import com.montola.school.course.enums.QuizType;
import com.montola.school.course.model.ClassEntity;
import com.montola.school.course.model.Level;
import com.montola.school.course.repository.ClassRepository;
import com.montola.school.course.repository.LevelRepository;
import com.montola.school.course.service.ChapterService;
import com.montola.school.course.service.LectureService;
import com.montola.school.course.service.QuizService;
import com.montola.school.course.service.SubjectService;
import com.montola.school.course.service.TopicService;
import com.montola.school.notice.dto.NoticeRequestDto;
import com.montola.school.notice.enums.NoticeType;
import com.montola.school.notice.service.NoticeService;
import com.montola.school.shop.dto.ShopProductRequestDto;
import com.montola.school.shop.enums.ShopItemStatus;
import com.montola.school.shop.enums.ShopProductFormat;
import com.montola.school.shop.enums.ShopProductType;
import com.montola.school.shop.service.ShopAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Populates a local development database with a small, usable set of demo data.
 * <p>
 * A freshly migrated database is schema-only, and there is no way to create the
 * first user through the API — registration always assigns STUDENT, and admin
 * registration itself requires an existing ADMIN. This exists to break that
 * deadlock and to give every screen something to show.
 * </p>
 * <p>
 * It is gated three independent ways: the dev Spring profile (a deployed instance
 * runs the prod profile — see the Dockerfile), an explicit property, and an
 * idempotency check so a populated database is never written to twice.
 * </p>
 *
 * @author avidewan
 */
@Component
@Profile("dev")
@ConditionalOnProperty(name = "app.dev.seed", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class DevDataSeeder implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "admin@montola.local";
    private static final String TEACHER_EMAIL = "teacher@montola.local";
    private static final String STUDENT_EMAIL = "student@montola.local";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LevelRepository levelRepository;
    private final ClassRepository classRepository;
    private final SubjectService subjectService;
    private final ChapterService chapterService;
    private final TopicService topicService;
    private final LectureService lectureService;
    private final QuizService quizService;
    private final ShopAdminService shopAdminService;
    private final NoticeService noticeService;

    @Value("${app.dev.seed-password}")
    private String seedPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // The marker for "already seeded". Everything below depends on this
        // short-circuit, so a second startup never duplicates data.
        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            log.info("Dev seed: already seeded ({} exists), skipping.", ADMIN_EMAIL);
            return;
        }

        log.warn("Dev seed: populating a development database. Not for production.");

        seedUsers();

        // Creating a chapter records its author from the security context
        // (ChapterServiceImpl calls userService.getCurrentUser()), which does not
        // exist outside a request. Run the curriculum seeding as the admin we just
        // created, and clear the context afterwards.
        try {
            authenticateAs(userRepository.findByEmail(ADMIN_EMAIL).orElseThrow());
            seedCurriculum();
        } finally {
            SecurityContextHolder.clearContext();
        }

        log.warn("Dev seed: complete. Seeded accounts: {}, {}, {}.",
                ADMIN_EMAIL, TEACHER_EMAIL, STUDENT_EMAIL);
    }

    private void seedCurriculum() {
        // The three levels are inserted by migration V13, not here. Creating them
        // again would violate the unique constraint on levels.name.
        Level jsc = existingLevel("JSC");

        ClassEntity classSix = createClass("Class 6", jsc);
        createClass("Class 7", jsc);
        createClass("Class 8", jsc);

        SubjectResponseDto mathematics = createSubject(classSix, "Mathematics", 0);
        createSubject(classSix, "English", 1);
        createSubject(classSix, "Science", 2);

        seedChapterWithContent(mathematics);
        seedShopProduct(jsc, classSix, mathematics);
        seedNotice();
    }

    /**
     * Puts the given user into the security context for the current thread, so
     * services that read the current user work outside a request.
     */
    private void authenticateAs(User user) {
        Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());

        var authentication = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(user.getId(), user.getEmail(), user.getPasswordHash(), authorities),
                null,
                authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void seedUsers() {
        createUser(ADMIN_EMAIL, "Dev Admin", Role.ADMIN);
        createUser(TEACHER_EMAIL, "Dev Teacher", Role.TEACHER);
        createUser(STUDENT_EMAIL, "Dev Student", Role.STUDENT);
    }

    private void createUser(String email, String fullName, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPasswordHash(passwordEncoder.encode(seedPassword));
        // Login rejects an unactivated account, so this is not optional.
        user.setIsActivated(true);
        user.setRoles(new HashSet<>(Set.of(role)));

        userRepository.save(user);

        log.info("Dev seed: created {} as {}", email, role);
    }

    /**
     * Levels are seeded by migration V13, so the seeder only looks them up.
     */
    private Level existingLevel(String name) {
        return levelRepository.findAllByOrderByOrderIndexAsc().stream()
                .filter(level -> name.equals(level.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Level '" + name + "' not found — migration V13 should have inserted it."));
    }

    /**
     * Classes are saved through the repository rather than {@code ClassService}
     * because the class request DTO has no level field, so the service cannot
     * attach a class to a level.
     */
    private ClassEntity createClass(String name, Level level) {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setName(name);
        classEntity.setDescription(name + " course material");
        classEntity.setLevel(level);

        return classRepository.save(classEntity);
    }

    private SubjectResponseDto createSubject(ClassEntity classEntity, String name, int orderIndex) {
        SubjectRequestDto subject = new SubjectRequestDto();
        subject.setClassId(classEntity.getId());
        subject.setName(name);
        subject.setDescription(name + " for " + classEntity.getName());
        subject.setOrderIndex(orderIndex);

        return subjectService.create(subject);
    }

    /**
     * A free, published chapter with a lecture and a quiz, so the student content
     * player has something to open without a purchase.
     */
    private void seedChapterWithContent(SubjectResponseDto subject) {
        ChapterRequestDto chapter = new ChapterRequestDto();
        chapter.setSubjectId(subject.getId());
        chapter.setTitle("Algebra Basics");
        chapter.setDescription("Variables, expressions and simple equations.");
        chapter.setStatus(ChapterStatus.PUBLISHED);
        chapter.setOrderIndex(0);
        chapter.setFree(true);

        ChapterResponseDto savedChapter = chapterService.create(chapter);

        TopicRequestDto topic = new TopicRequestDto();
        topic.setChapterId(savedChapter.getId());
        topic.setTitle("Variables and expressions");
        topic.setDescription("What a variable is, and how to read an expression.");
        topic.setOrderIndex(0);

        TopicResponseDto savedTopic = topicService.create(topic);

        LectureRequestDto lecture = LectureRequestDto.builder()
                .topicId(savedTopic.getId())
                .title("What is a variable?")
                .content("A variable is a letter that stands for a number we do not know yet. "
                        + "In 'x + 3 = 7', the letter x is the variable.")
                .orderIndex(0)
                .build();

        lectureService.create(lecture);

        QuizRequestDto quiz = QuizRequestDto.builder()
                .topicId(savedTopic.getId())
                .quizType(QuizType.MCQ)
                .title("Algebra check")
                .instruction("Choose the correct answer for each question.")
                .orderIndex(1)
                .totalMarks(2)
                .passPercentage(50.0)
                .questions(List.of(
                        QuizQuestionRequestDto.builder()
                                .questionText("What is 2 + 3?")
                                .type(QuestionType.MULTIPLE_CHOICE)
                                .orderIndex(0)
                                .marks(1)
                                .options(List.of(
                                        QuizOptionRequestDto.builder().optionText("4").correct(false).build(),
                                        QuizOptionRequestDto.builder().optionText("5").correct(true).build(),
                                        QuizOptionRequestDto.builder().optionText("6").correct(false).build()))
                                .build(),
                        QuizQuestionRequestDto.builder()
                                .questionText("If x + 3 = 7, what is x?")
                                .type(QuestionType.MULTIPLE_CHOICE)
                                .orderIndex(1)
                                .marks(1)
                                .options(List.of(
                                        QuizOptionRequestDto.builder().optionText("3").correct(false).build(),
                                        QuizOptionRequestDto.builder().optionText("4").correct(true).build(),
                                        QuizOptionRequestDto.builder().optionText("5").correct(false).build()))
                                .build()))
                .build();

        quizService.create(quiz);
    }

    /**
     * An INTERACTIVE product deliberately: it carries its content as HTML, so it
     * needs no uploaded file and no storage provider. A PDF product would require
     * a real upload and would fail on a default local setup.
     */
    private void seedShopProduct(Level level, ClassEntity classEntity, SubjectResponseDto subject) {
        ShopProductRequestDto product = ShopProductRequestDto.builder()
                .title("Algebra — interactive notes")
                .description("Visual notes covering variables and simple equations.")
                .type(ShopProductType.NOTES)
                .format(ShopProductFormat.INTERACTIVE)
                .price(100.0)
                .levelId(level.getId())
                .classId(classEntity.getId())
                .subjectId(subject.getId())
                .status(ShopItemStatus.PUBLISHED)
                .featured(true)
                .preview("Preview: a variable is a placeholder for a number.")
                .content(ShopProductRequestDto.ContentDto.builder()
                        .html("<h2>Algebra</h2><p>A variable is a placeholder for a number we do not know yet.</p>")
                        .build())
                .build();

        shopAdminService.createProduct(product);
    }

    private void seedNotice() {
        noticeService.createNotice(NoticeRequestDto.builder()
                .title("Admissions open for the new session")
                .message("Applications are now open for the upcoming academic session.")
                .type(NoticeType.INFO)
                .link("/classes")
                .active(true)
                .orderIndex(0)
                .build());
    }
}
