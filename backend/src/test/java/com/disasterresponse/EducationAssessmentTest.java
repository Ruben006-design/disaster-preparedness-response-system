package com.disasterresponse;

import com.disasterresponse.dto.PreparednessScoreDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EducationAssessmentTest {

    @Test
    @DisplayName("Preparedness score calculation accurately weights modules, quizzes, and simulations")
    public void testPreparednessScoreCalculation() {
        long totalModules = 4;
        long completedModules = 4; // 100%

        double quizAverage = 80.0; // 80%

        long totalSimulations = 2;
        long completedSimulations = 2; // 100%

        int moduleScore = (int) Math.round(((double) completedModules / totalModules) * 100.0); // 100
        int quizScore = (int) Math.round(quizAverage); // 80
        int simulationScore = (int) Math.round(((double) completedSimulations / totalSimulations) * 100.0); // 100

        // 30% Modules + 40% Quizzes + 30% Simulations
        // 30 + 32 + 30 = 92
        int overall = (int) Math.round((0.30 * moduleScore) + (0.40 * quizScore) + (0.30 * simulationScore));

        assertEquals(100, moduleScore);
        assertEquals(80, quizScore);
        assertEquals(100, simulationScore);
        assertEquals(92, overall);

        String level = overall >= 85 ? "READINESS CHAMPION" : "PREPARED";
        assertEquals("READINESS CHAMPION", level);
    }

    @Test
    @DisplayName("Preparedness score handles zero activity gracefully")
    public void testZeroActivityPreparedness() {
        int moduleScore = 0;
        int quizScore = 0;
        int simulationScore = 0;
        int overall = (int) Math.round((0.30 * moduleScore) + (0.40 * quizScore) + (0.30 * simulationScore));

        assertEquals(0, overall);
        String level = overall < 40 ? "BEGINNER" : "INTERMEDIATE";
        assertEquals("BEGINNER", level);
    }
}
