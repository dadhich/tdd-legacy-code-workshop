import { useState } from 'react';

// THE SPAGHETTI FRONTEND COMPONENT
const LoanApplicationForm = () => {
    // 1. State explosion
    const [name, setName] = useState('');
    const [taxId, setTaxId] = useState('');
    const [amount, setAmount] = useState('');
    const [age, setAge] = useState('');
    const [errors, setErrors] = useState({});
    const [submissionStatus, setSubmissionStatus] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    // 2. Massive handler function doing everything
    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrors({});
        setSubmissionStatus(null);
        let currentErrors = {};

        // ---- Mixed Concern: Validation Logic embedded in submit handler ----
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

        // Arbitrary UI rule that should be unit tested, but can't be easily.
        if (name.toLowerCase().startsWith("test")) {
             currentErrors.name = "We do not accept test accounts.";
        }

        if (Object.keys(currentErrors).length > 0) {
            setErrors(currentErrors);
            return;
        }

        setIsLoading(true);

        // ---- Hardcoded API Call & State Management ----
        try {
            const payload = {
                applicantName: name,
                taxId: taxId,
                amount: Number(amount),
                applicantAge: Number(age)
            };

            // Hardcoded URL to localhost:8080
            const response = await fetch('http://localhost:8080/api/loan/apply', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const data = await response.json();

            if (data.status === 'ERROR' || data.status === 'REJECTED') {
                 setSubmissionStatus({ type: 'error', msg: data.message });
            } else {
                 setSubmissionStatus({ type: 'success', msg: `${data.message} (ID: ${data.loanId})` });
            }

        } catch (err) {
            setSubmissionStatus({ type: 'error', msg: "Network Error: Could not reach backend." });
        } finally {
            setIsLoading(false);
        }
    };

    // 3. The View mixed with logic
    return (
        <div style={{ maxWidth: '500px', border: '1px solid #ccc', padding: '20px' }}>
            <h2>Apply for a Loan</h2>
            {submissionStatus && (
                <div style={{ 
                    padding: '10px', 
                    marginBottom: '10px',
                    backgroundColor: submissionStatus.type === 'error' ? '#ffdddd' : '#ddffdd',
                    color: submissionStatus.type === 'error' ? 'red' : 'green'
                }}>
                    {submissionStatus.msg}
                </div>
            )}

            <form onSubmit={handleSubmit}>
                <div style={{ marginBottom: '10px' }}>
                    <label>Full Name:</label><br/>
                    <input type="text" value={name} onChange={e => setName(e.target.value)} style={{ width: '100%' }} />
                    {errors.name && <small style={{color: 'red'}}>{errors.name}</small>}
                </div>
                
                <div style={{ marginBottom: '10px' }}>
                    <label>Tax ID (use '999...' to fail API):</label><br/>
                    <input type="text" value={taxId} onChange={e => setTaxId(e.target.value)} style={{ width: '100%' }} />
                    {errors.taxId && <small style={{color: 'red'}}>{errors.taxId}</small>}
                </div>

                <div style={{ marginBottom: '10px' }}>
                    <label>Loan Amount ($):</label><br/>
                    <input type="number" value={amount} onChange={e => setAmount(e.target.value)} style={{ width: '100%' }} />
                    {errors.amount && <small style={{color: 'red'}}>{errors.amount}</small>}
                </div>

                 <div style={{ marginBottom: '10px' }}>
                    <label>Applicant Age:</label><br/>
                    <input type="number" value={age} onChange={e => setAge(e.target.value)} style={{ width: '100%' }} />
                </div>

                <button type="submit" disabled={isLoading} style={{ padding: '10px 20px' }}>
                    {isLoading ? 'Processing...' : 'Submit Application'}
                </button>
            </form>
        </div>
    );
};

export default LoanApplicationForm;