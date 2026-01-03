import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, beforeAll, afterAll, afterEach } from 'vitest';
import { setupServer } from 'msw/node';
import { http, HttpResponse } from 'msw';
import LoanApplicationForm from './LoanApplicationForm';

// 1. Define the Network Mock
const handlers = [
    http.post('http://localhost:8080/api/loan/apply', async ({ request }) => {
        const body = await request.json();

        // Simulate the new logic we built in the backend
        if (body.applicantAge < 25 && body.amount > 50000) {
            return HttpResponse.json({
                status: 'HIGH_RISK_REQUIRED',
                message: 'Manual approval needed',
                loanId: null
            });
        }

        return HttpResponse.json({
            status: 'APPROVED',
            message: 'Loan approved',
            loanId: 123
        });
    })
];

const server = setupServer(...handlers);

// 2. Lifecycle Hooks
beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('LoanApplicationForm Integration', () => {

    it('displays High Risk warning when backend flags application', async () => {
        render(<LoanApplicationForm />);
        const user = userEvent.setup();

        // Fill in the form with "High Risk" data
        await user.type(screen.getByLabelText(/full name/i), 'Young Investor');
        await user.type(screen.getByLabelText(/tax id/i), '123456');
        await user.type(screen.getByLabelText(/loan amount/i), '60000'); // > 50k
        await user.type(screen.getByLabelText(/applicant age/i), '20');  // < 25

        // Submit
        await user.click(screen.getByRole('button', { name: /submit/i }));

        // Assert
        // This will FAIL initially because the UI creates a generic green/red box
        // but we want a specific yellow warning box for this case.
        await waitFor(() => {
            const alert = screen.getByRole('alert');
            expect(alert).toHaveTextContent('Manual approval needed');
            // We want a specific style for warnings, not just generic error/success
            expect(alert).toHaveStyle({ backgroundColor: '#fff3cd' }); // Yellow
        });
    });
});