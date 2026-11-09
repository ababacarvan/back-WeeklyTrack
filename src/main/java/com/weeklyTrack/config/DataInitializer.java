// com.weeklytrack.config.DataInitializer.java
package com.weeklyTrack.config;

import com.weeklyTrack.entity.*;
import com.weeklyTrack.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private WeeklyReportRepository reportRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            initData();
        }
    }

    private void initData() {
        // Créer les équipes
        Team backendTeam = new Team();
        backendTeam.setName("Équipe Backend");
        backendTeam.setDescription("Développement des API et services backend");
        teamRepository.save(backendTeam);

        Team frontendTeam = new Team();
        frontendTeam.setName("Équipe Frontend");
        frontendTeam.setDescription("Développement des interfaces utilisateur");
        teamRepository.save(frontendTeam);

        // Créer les projets
        Project backendProject = new Project();
        backendProject.setName("WeeklyTrack API");
        backendProject.setDescription("API REST pour l'application WeeklyTrack");
        backendProject.setTeam(backendTeam);
        backendProject.setActive(true);
        projectRepository.save(backendProject);

        Project frontendProject = new Project();
        frontendProject.setName("WeeklyTrack UI");
        frontendProject.setDescription("Interface Angular pour WeeklyTrack");
        frontendProject.setTeam(frontendTeam);
        frontendProject.setActive(true);
        projectRepository.save(frontendProject);

        // Créer les utilisateurs
        User admin = createUser("admin@weeklytrack.com", "Admin", "Principal", "ADMIN", null, null);
        User director = createUser("director@weeklytrack.com", "Jean", "Dupont", "DIRECTOR", null, null);
        User techLeadBackend = createUser("techlead.backend@weeklytrack.com", "Pierre", "Martin", "TECH_LEAD", backendTeam, backendProject);
        User techLeadFrontend = createUser("techlead.frontend@weeklytrack.com", "Marie", "Bernard", "TECH_LEAD", frontendTeam, frontendProject);

        // Développeurs
        User dev1 = createUser("dev1.backend@weeklytrack.com", "Sophie", "Dubois", "DEVELOPER", backendTeam, backendProject);
        User dev2 = createUser("dev2.backend@weeklytrack.com", "Thomas", "Moreau", "DEVELOPER", backendTeam, backendProject);
        User dev3 = createUser("dev1.frontend@weeklytrack.com", "Nicolas", "Simon", "DEVELOPER", frontendTeam, frontendProject);

        // Mettre à jour les tech leads
        backendTeam.setTechLead(techLeadBackend);
        teamRepository.save(backendTeam);
        frontendTeam.setTechLead(techLeadFrontend);
        teamRepository.save(frontendTeam);

        // Créer des reportings
        WeeklyReport report1 = new WeeklyReport();
        report1.setUser(dev1);
        report1.setWeekNumber(23);
        report1.setYear(2026);
        report1.setHighlights("Mise en place de l'authentification JWT");
        report1.setCompletedTasks("- Spring Security configuré\n- JWT implémenté\n- Endpoints d'auth créés");
        report1.setPlannedTasks("- Refresh tokens\n- Logs d'audit");
        report1.setStatus(ReportStatus.SUBMITTED);
        reportRepository.save(report1);

        // Créer des notifications
        Notification notification = new Notification();
        notification.setUser(techLeadBackend);
        notification.setTitle("Nouveau reporting");
        notification.setMessage("Un nouveau reporting a été soumis");
        notification.setType(NotificationType.REPORT_SUBMITTED);
        notification.setReferenceId(report1.getId());
        notificationRepository.save(notification);

        System.out.println("=== Données de test initialisées avec succès ===");
        System.out.println("Utilisateurs créés:");
        System.out.println("- Admin: admin@weeklytrack.com / password123");
        System.out.println("- Director: director@weeklytrack.com / password123");
        System.out.println("- Tech Lead Backend: techlead.backend@weeklytrack.com / password123");
        System.out.println("- Tech Lead Frontend: techlead.frontend@weeklytrack.com / password123");
        System.out.println("- Développeurs: dev1.backend@weeklytrack.com / password123, etc.");
    }

    private User createUser(String email, String firstName, String lastName, String role, Team team, Project project) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(Role.valueOf(role));
        user.setTeam(team);
        user.setProject(project);
        user.setActive(true);
        return userRepository.save(user);
    }
}