import { useState } from 'react';

const LoanApplicationForm = () => {
    const [name, setName] = useState('');
    const [taxId, setTaxId] = useState('');
    const [amount, setAmount] = useState('');
    const [age, setAge] = useState('');
    const [errors, setErrors] = useState({});
    const [submissionStatus, setSubmissionStatus] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    // Helper to determine alert styles
    const getAlertStyle = (type) => {
        switch (type) {
            case 'error':
                return { backgroundColor: '#ffdddd', color: 'red' };
            case 'success':
                return { backgroundColor: '#ddffdd', color: 'green' };
            case 'warning': // NEW STYLE
                return { backgroundColor: '#fff3cd', color: '#856404' };
            default:
                return {};
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrors({});
        setSubmissionStatus(null);
        let currentErrors = {};

        if (!name) currentErrors.name = "Name required";
        if (!taxId) {
            currentErrors.taxId = "Tax ID required";
        } else if (taxId.length < 6) {
            currentErrors.taxId = "Tax ID must be at least 6 chars";
        }

        if (!amount) {
            currentErrors.amount = "Amount required";
        } else if (Number(amount) <= 0) {
            currentErrors.amount = "Amount must be positive";
        }

        if (name.toLowerCase().startsWith("test")) {
            currentErrors.name = "We do not accept test accounts.";
        }

        if (Object.keys(currentErrors).length > 0) {
            setErrors(currentErrors);
            return;
        }

        setIsLoading(true);

        try {
            const payload = {
                applicantName: name,
                taxId: taxId,
                amount: Number(amount),
                applicantAge: Number(age)
            };

            const response = await fetch('http://localhost:8080/api/loan/apply', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const data = await response.json();

            // --- CHANGED LOGIC HERE ---
            if (data.status === 'HIGH_RISK_REQUIRED') {
                setSubmissionStatus({ type: 'warning', msg: data.message });
            } else if (data.status === 'ERROR' || data.status === 'REJECTED') {
                setSubmissionStatus({ type: 'error', msg: data.message });
            } else {
                setSubmissionStatus({ type: 'success', msg: `${data.message} (ID: ${data.loanId})` });
            }
            // --------------------------

        } catch (err) {
            setSubmissionStatus({ type: 'error', msg: "Network Error: Could not reach backend." });
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div style={{ maxWidth: '500px', border: '1px solid #ccc', padding: '20px' }}>
            <h2>Apply for a Loan</h2>
            {submissionStatus && (
                <div role="alert" style={{
                    padding: '10px',
                    marginBottom: '10px',
                    ...getAlertStyle(submissionStatus.type) // Use helper function
                }}>
                    {submissionStatus.msg}
                </div>
            )}

            <form onSubmit={handleSubmit}>
                <div style={{ marginBottom: '10px' }}>
                    <label htmlFor="name">Full Name:</label><br/>
                    <input id="name" type="text" value={name} onChange={e => setName(e.target.value)} style={{ width: '100%' }} />
                    {errors.name && <small style={{color: 'red'}}>{errors.name}</small>}
                </div>

                <div style={{ marginBottom: '10px' }}>
                    <label htmlFor="taxId">Tax ID (use '999...' to fail API):</label><br/>
                    <input id="taxId" type="text" value={taxId} onChange={e => setTaxId(e.target.value)} style={{ width: '100%' }} />
                    {errors.taxId && <small style={{color: 'red'}}>{errors.taxId}</small>}
                </div>

                <div style={{ marginBottom: '10px' }}>
                    <label htmlFor="amount">Loan Amount ($):</label><br/>
                    <input id="amount" type="number" value={amount} onChange={e => setAmount(e.target.value)} style={{ width: '100%' }} />
                    {errors.amount && <small style={{color: 'red'}}>{errors.amount}</small>}
                </div>

                <div style={{ marginBottom: '10px' }}>
                    <label htmlFor="age">Applicant Age:</label><br/>
                    <input id="age" type="number" value={age} onChange={e => setAge(e.target.value)} style={{ width: '100%' }} />
                </div>

                <button type="submit" disabled={isLoading} style={{ padding: '10px 20px' }}>
                    {isLoading ? 'Processing...' : 'Submit Application'}
                </button>
            </form>
        </div>
    );
};

export default LoanApplicationForm;