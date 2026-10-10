package com.disasterresponse.enums;

import java.util.EnumSet;
import java.util.Set;

public enum ReportStatus {
    SUBMITTED,
    UNDER_REVIEW,
    VERIFIED,
    REJECTED,
    ASSIGNED,
    RESPONSE_IN_PROGRESS,
    SITUATION_CONTROLLED,
    RESOLVED;

    public boolean canTransitionTo(ReportStatus next) {
        if (this == next) {
            return true;
        }
        return switch (this) {
            case SUBMITTED -> next == UNDER_REVIEW || next == VERIFIED || next == REJECTED;
            case UNDER_REVIEW -> next == VERIFIED || next == REJECTED;
            case VERIFIED -> next == ASSIGNED;
            case ASSIGNED -> next == RESPONSE_IN_PROGRESS;
            case RESPONSE_IN_PROGRESS -> next == SITUATION_CONTROLLED || next == RESOLVED;
            case SITUATION_CONTROLLED -> next == RESOLVED;
            case REJECTED, RESOLVED -> false; // Terminal states
        };
    }
}
