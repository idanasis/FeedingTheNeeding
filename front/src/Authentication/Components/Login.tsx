import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login, resetPassword } from '../RestAPI/loginRestAPI';
import '../Styles/Login.css';
import Logo from '../Images/logo.png';

const Login: React.FC = () => {
    const [phoneNumber, setPhoneNumber] = useState<string>('');
    const [password, setPassword] = useState<string>('');
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(false);

    const [showResetPopup, setShowResetPopup] = useState<boolean>(false);
    const [resetPhoneNumber, setResetPhoneNumber] = useState<string>('');
    const [newPassword, setNewPassword] = useState<string>('');
    const [resetError, setResetError] = useState<string | null>(null);
    const [resetSuccess, setResetSuccess] = useState<string | null>(null);

    const navigate = useNavigate();

    const handleLogin = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            const { token } = await login(phoneNumber, password);
            localStorage.setItem('token', token);
            navigate('/');
        } catch (err) {
            setError('מספר טלפון או סיסמא לא נכונים');
            console.error('Login error:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleShowResetPopup = () => {
        setShowResetPopup(true);
        setResetPhoneNumber('');
        setNewPassword('');
        setResetError(null);
        setResetSuccess(null);
    }

    const handleCloseResetPopup = () => {
        setShowResetPopup(false);
        setResetError(null);
        setResetSuccess(null);
    };

    const handleResetPassword = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setResetError(null);
        setResetSuccess(null);

        try {
            await resetPassword(resetPhoneNumber, newPassword);
            setResetSuccess('הסיסמה אופסה בהצלחה!\n אנא התחבר/י עם הסיסמה החדשה.');
        } catch (err: any) {
            setResetError(err.message || 'שגיאה בעת איפוס הסיסמה.');
        }
    };

    return (
        <div className="login-page">
            <div className="login-container">
                <h2>התחברות למערכת</h2>
                <img src={Logo} alt="Logo"/>
                <form onSubmit={handleLogin} className="login-form">
                    <div className="form-group">
                        <label htmlFor="phoneNumber" className="form-label-right">
                            מספר טלפון:
                        </label>
                        <input
                            id="phoneNumber"
                            type="tel"
                            maxLength={10}
                            value={phoneNumber}
                            onChange={(e) => setPhoneNumber(e.target.value)}
                            dir="rtl"
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="password" className="form-label-right">
                            סיסמה:
                        </label>
                        <input
                            id="password"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            dir="rtl"
                            required
                        />
                    </div>

                    {error && <p className="error-message">{error}</p>}

                    <button type="submit" className="submit-button" disabled={loading}>
                        {loading ? 'טוען...' : 'התחבר'}
                    </button>
                </form>

                <div className="extra-options">
                    <span>עדיין לא הצטרפת אלינו? </span>
                    <a onClick={() => navigate('/donorRegister')}>
                        הצטרף כאן
                    </a>
                    <br/>
                    <span className="forgot-password" onClick={handleShowResetPopup}>
                        שכחתי סיסמה
                    </span>
                </div>
            </div>

            {showResetPopup && (
                <div className="reset-password-popup">
                    <div className="reset-popup-content">
                        <h3>איפוס סיסמה</h3>
                        <form onSubmit={handleResetPassword}>
                            <label htmlFor="resetPhoneNumber">מספר טלפון:</label>
                            <input
                                type="tel"
                                id="resetPhoneNumber"
                                maxLength={10}
                                value={resetPhoneNumber}
                                onChange={(e) => setResetPhoneNumber(e.target.value)}
                                required
                            />

                            <label htmlFor="newPassword">סיסמה חדשה:</label>
                            <input
                                type="password"
                                id="newPassword"
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                                required
                            />

                            {resetError && <p className="error-message">{resetError}</p>}
                            {resetSuccess && <p className="success-message">{resetSuccess}</p>}

                            <div className="reset-popup-buttons">
                                <button type="submit" className="submit-button">
                                    אפס סיסמה
                                </button>
                                <button
                                    type="button"
                                    className="close-button"
                                    onClick={handleCloseResetPopup}
                                >
                                    בטל
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Login;