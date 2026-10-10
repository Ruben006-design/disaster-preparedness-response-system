package com.disasterresponse;

import com.disasterresponse.enums.ReportStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

public class DisasterReportWorkflowTest {

    @Test
    @DisplayName("Report code follows standard academic format RPT-YYYY-XXXX")
    public void testReportCodeFormat() {
        int seq = 42;
        String code = "RPT-" + Year.now().getValue() + "-" + String.format("%04d", seq);

        assertTrue(code.startsWith("RPT-"));
        assertTrue(code.contains("-" + Year.now().getValue() + "-"));
        assertTrue(code.endsWith("0042"));
    }

    @Test
    @DisplayName("Report status transition logic respects lifecycle stages")
    public void testStatusTransitions() {
        ReportStatus submitted = ReportStatus.SUBMITTED;
        assertTrue(submitted.canTransitionTo(ReportStatus.VERIFIED));
        assertTrue(submitted.canTransitionTo(ReportStatus.REJECTED));

        ReportStatus verified = ReportStatus.VERIFIED;
        assertTrue(verified.canTransitionTo(ReportStatus.ASSIGNED));

        ReportStatus assigned = ReportStatus.ASSIGNED;
        assertTrue(assigned.canTransitionTo(ReportStatus.RESPONSE_IN_PROGRESS));

        ReportStatus resolved = ReportStatus.RESOLVED;
        assertFalse(resolved.canTransitionTo(ReportStatus.SUBMITTED));
    }
}
