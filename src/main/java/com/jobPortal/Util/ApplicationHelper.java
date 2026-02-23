package com.jobPortal.Util;

import com.jobPortal.Enums.ApplicationStatus;

public class ApplicationHelper {

    public static boolean canMoveForward(ApplicationStatus current, ApplicationStatus next) {
        switch (current) {
            case PENDING:
                return next == ApplicationStatus.SHORTLISTED || next == ApplicationStatus.REJECTED;
            case SHORTLISTED:
                return next == ApplicationStatus.INTERVIEW_SCHEDULED || next == ApplicationStatus.REJECTED;
            case INTERVIEW_SCHEDULED:
                return next == ApplicationStatus.SELECTED || next == ApplicationStatus.REJECTED;
            case SELECTED:
            case REJECTED:
                return false;
            default:
                return false;
        }
    }
}
