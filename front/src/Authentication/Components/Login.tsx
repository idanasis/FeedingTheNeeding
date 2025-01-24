import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login, requestPasswordReset, resetPassword } from '../RestAPI/loginRestAPI';
import '../Styles/Login.css';
import Logo from '../Images/logo.png';
import {verifyDonor} from '../RestAPI/donorRegRestAPI';

const Login: React.FC = () => {
    const [phoneNumber, setPhoneNumber] = useState<string>('');
    const [password, setPassword] = useState<string>('');
    const [showPassword, setShowPassword] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(false);

    const [showResetPopup, setShowResetPopup] = useState<boolean>(false);
    const [resetStep, setResetStep] = useState<number>(1); // 1: Request code, 2: Enter code & new password
    const [resetPhoneNumber, setResetPhoneNumber] = useState<string>('');
    const [verificationCode, setVerificationCode] = useState<string>('');
    const [newPassword, setNewPassword] = useState<string>('');
    const [resetError, setResetError] = useState<string | null>(null);
    const [resetSuccess, setResetSuccess] = useState<string | null>(null);
    const [resetLoading, setResetLoading] = useState<boolean>(false);

    const navigate = useNavigate();

    const handleLogin = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            const { token } = await login(phoneNumber, password);
            localStorage.setItem('token', token);
            navigate('/');
        } catch (err: any) {
            if (err.response && err.response.status === 409)
                setError("המשתמש אינו מאומת, אנא המתן ליצירת קשר מהרכזת");
            else
                setError('מספר טלפון או סיסמא לא נכונים');

            console.error('Login error:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleShowResetPopup = () => {
        setShowResetPopup(true);
        setResetStep(1);
        setResetPhoneNumber('');
        setVerificationCode('');
        setNewPassword('');
        setResetError(null);
        setResetSuccess(null);
    };

    const handleCloseResetPopup = () => {
        setShowResetPopup(false);
        setResetStep(1);
        setResetPhoneNumber('');
        setVerificationCode('');
        setNewPassword('');
        setResetError(null);
        setResetSuccess(null);
    };

    const handleRequestReset = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setResetError(null);
        setResetSuccess(null);
        setResetLoading(true);

        try {
            await requestPasswordReset(resetPhoneNumber);
            setResetSuccess('קוד האימות נשלח בהצלחה לאימייל שלך הקיים במערכת.');
            setResetStep(2);
        } catch (err: any) {
            setResetError('שגיאה בעת שליחת קוד האימות.');
        } finally {
            setResetLoading(false);
        }
    };

    // Handle confirming password reset
    const handleConfirmReset = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setResetError(null);
        setResetSuccess(null);
        setResetLoading(true);

        try {
            await resetPassword(resetPhoneNumber, newPassword, verificationCode);
            //await verifyDonor(resetPhoneNumber, verificationCode);
            setResetSuccess('הסיסמה אופסה בהצלחה! אנא התחבר/י עם הסיסמה החדשה.');
            // Optionally, close the popup after a delay
            setTimeout(() => {
                handleCloseResetPopup();
            }, 1000);
        } catch (err: any) {
            setResetError('שגיאה בעת איפוס הסיסמה.');
        } finally {
            setResetLoading(false);
        }
    };

    const togglePasswordVisibility = () => {
        setShowPassword((prev) => !prev);
    };

    return (
        <div className="login-page-wrapper">
            <div className="login-page">
                <div className="login-container">
                    <button className="home-button" onClick={() => navigate('/')}>
                        חזרה למסך בית  &#8592;
                    </button>

                    <h2>התחברות למערכת</h2>
                    <img src={Logo} alt="Logo" style={{ maxWidth: '200px', marginBottom: '20px' }} />
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
                                dir="ltr"
                                required
                            />
                        </div>
                        <div className="form-group password-group">
                            <label htmlFor="password" className="form-label-right">
                                סיסמה:
                            </label>
                            <div className="password-input-wrapper">
                                <input
                                    id="password"
                                    type={showPassword ? 'text' : 'password'}
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    dir="ltr"
                                    required
                                />
                                <button
                                    type="button"
                                    className="toggle-password-button"
                                    onClick={togglePasswordVisibility}
                                >
                                    {showPassword ? '👁️‍🗨️' : '🗨️'}
                                </button>
                            </div>
                        </div>

                        {error && <p className="error-message">{error}</p>}

                        <button type="submit" className="submit-button" disabled={loading}>
                            {loading ? 'טוען...' : 'התחבר'}
                        </button>
                    </form>

                    <div className="extra-options">
                        <span>עדיין לא הצטרפת אלינו? </span>
                        <a onClick={() => navigate('/donorRegister')}>הצטרף כאן</a>
                        <br />
                        <span className="forgot-password" onClick={handleShowResetPopup}>
                            שכחתי סיסמה
                        </span>
                    </div>
                </div>
            </div>

            {showResetPopup && (
                <div className="reset-popup-overlay">
                    <div className="reset-popup">
                        <button className="close-button" onClick={handleCloseResetPopup}>
                            &times;
                        </button>
                        {resetStep === 1 && (
                            <>
                                <h2>איפוס סיסמה</h2>
                                <form onSubmit={handleRequestReset} className="reset-form">
                                    <div className="form-group">
                                        <label htmlFor="resetPhoneNumber" className="form-label-right">
                                            מספר טלפון:
                                        </label>
                                        <input
                                            id="resetPhoneNumber"
                                            type="tel"
                                            maxLength={10}
                                            value={resetPhoneNumber}
                                            onChange={(e) => setResetPhoneNumber(e.target.value)}
                                            dir="ltr"
                                            required
                                        />
                                    </div>

                                    {resetError && <p className="error-message">{resetError}</p>}
                                    {resetSuccess && <p className="success-message">{resetSuccess}</p>}

                                    <button type="submit" className="submit-button" disabled={resetLoading}>
                                        {resetLoading ? 'טוען...' : 'שלח קוד אימות'}
                                    </button>
                                </form>
                            </>
                        )}

                        {resetStep === 2 && (
                            <>
                                <h2>אישור איפוס סיסמה</h2>
                                <form onSubmit={handleConfirmReset} className="reset-form">
                                    <div className="form-group">
                                        <label htmlFor="verificationCode" className="form-label-right">
                                            קוד אימות:
                                        </label>
                                        <input
                                            id="verificationCode"
                                            type="text"
                                            maxLength={6}
                                            value={verificationCode}
                                            onChange={(e) => setVerificationCode(e.target.value)}
                                            dir="ltr"
                                            required
                                        />
                                    </div>
                                    <div className="form-group">
                                        <label htmlFor="newPassword" className="form-label-right">
                                            סיסמה חדשה:
                                        </label>
                                        <input
                                            id="newPassword"
                                            type="password"
                                            value={newPassword}
                                            onChange={(e) => setNewPassword(e.target.value)}
                                            dir="ltr"
                                            required
                                        />
                                    </div>

                                    {resetError && <p className="error-message">{resetError}</p>}
                                    {resetSuccess && <p className="success-message">{resetSuccess}</p>}

                                    <button type="submit" className="submit-button" disabled={resetLoading}>
                                        {resetLoading ? 'טוען...' : 'אפס סיסמה'}
                                    </button>
                                </form>
                            </>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

export default Login;
