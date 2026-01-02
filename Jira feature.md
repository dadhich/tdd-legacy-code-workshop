**Feature Request:** High Risk Flag

**As a** Risk Manager 
**I want to** flag applications from young applicants requesting large sums 
**So that** we can manually review them before approval.

**Acceptance Criteria:**
- If Applicant Age < 25 AND Loan Amount > 50,000
- Then Result Status should be "HIGH_RISK_REQUIRED"
- And Result Message should be "Manual approval needed"