package com.ankitsaahariya.domain;

public enum AccountStatus {
    PENDING_VERIFICATION, // Account create ho chuka hai, par email/phone verification abhi pending hai
    ACTIVE,               // Account active hai aur user system use kar sakta hai
    SUSPENDED,            // Account temporarily band hai due to rule violation ya admin action
    DEACTIVATED,          // Account user ya system ne deactivate kar diya hai, ab use nahi ho sakta
    BANNED,               // Account permanently ban ho chuka hai due to serious violations
    CLOSED                // Account permanently close ho gaya hai aur dobara activate nahi ho sakta
}
