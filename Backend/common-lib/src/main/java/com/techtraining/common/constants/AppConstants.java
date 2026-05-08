package com.techtraining.common.constants;

public class AppConstants {
    // Batch
    public static final String BATCH_CREATED   = "Batch created successfully";
    public static final String BATCH_UPDATED   = "Batch updated successfully";
    public static final String BATCH_DELETED   = "Batch deleted successfully";
    public static final String BATCH_NOT_FOUND = "Batch not found with id: ";
    public static final String BATCH_DUPLICATE = "Batch already exists with name: ";

    // Technology
    public static final String TECH_CREATED   = "Technology created successfully";
    public static final String TECH_UPDATED   = "Technology updated successfully";
    public static final String TECH_DELETED   = "Technology deleted successfully";
    public static final String TECH_NOT_FOUND = "Technology not found with id: ";

    // User
    public static final String USER_CREATED       = "User account created successfully";
    public static final String USER_UPDATED       = "User updated successfully";
    public static final String USER_DELETED       = "User deactivated successfully";
    public static final String USER_NOT_FOUND     = "User not found with id: ";
    public static final String PASSWORD_RESET     = "Password reset successfully";

    // Participant
    public static final String PARTICIPANT_CREATED = "Participant added successfully";
    public static final String PARTICIPANT_DELETED = "Participant removed successfully";
    public static final String PARTICIPANT_NOT_FOUND = "Participant not found with id: ";

    // Enrollment
    public static final String ENROLLMENT_CREATED  = "Participant enrolled successfully";
    public static final String ENROLLMENT_DELETED  = "Enrollment removed successfully";
    public static final String ENROLLMENT_DUPLICATE = "Participant already enrolled in this batch-technology";

    // Evaluation
    public static final String ASSIGNMENT_CREATED  = "Evaluator assigned successfully";
    public static final String EVAL_SUBMITTED      = "Evaluation submitted successfully";
    public static final String EVAL_UPDATED        = "Evaluation updated successfully";
    public static final String EVAL_ALREADY_DONE   = "Evaluation already submitted for this assignment";
    public static final String EVAL_NOT_ASSIGNED   = "You are not assigned to this evaluation";
    public static final String ASSIGNMENT_NOT_FOUND = "Assignment not found with id: ";

    // Auth
    public static final String LOGIN_SUCCESS   = "Login successful";
    public static final String LOGOUT_SUCCESS  = "Logged out successfully";
    public static final String INVALID_CREDS   = "Invalid email or password";
    public static final String TOKEN_EXPIRED   = "Session expired, please login again";
    public static final String ACCESS_DENIED   = "You do not have permission to perform this action";
}
