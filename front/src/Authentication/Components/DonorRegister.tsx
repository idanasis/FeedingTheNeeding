import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../Styles/DonorRegister.css';
import FeedingLogo from '../Images/logo.png';
import { registerDonor, validateEmail, validatePhone, DonorRegistrationData, verifyDonor, resendVerificationSMSCode } from '../RestAPI/donorRegRestAPI';

const DonorRegister: React.FC = () => {
    const [formData, setFormData] = useState<DonorRegistrationData>({
        email: '',
        password: '',
        confirmPassword: '',
        firstName: '',
        lastName: '',
        phoneNumber: '',
        address: '',
    });

    const [hasNoCriminalRecord, setHasNoCriminalRecord] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [showVerificationPopup, setShowVerificationPopup] = useState(false);
    const [verificationCode, setVerificationCode] = useState('');
    const [successMessage, setSuccessMessage] = useState(false);
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);

    const navigate = useNavigate();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { id, value } = e.target;
        setFormData((prevData) => ({ ...prevData, [id]: value }));
    };

    const togglePasswordVisibility = () => setShowPassword((prev) => !prev);
    const toggleConfirmPasswordVisibility = () => setShowConfirmPassword((prev) => !prev);

    const handleCheckboxChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setHasNoCriminalRecord(e.target.checked);
    };

    const handleSuccessConfirmation = () => {
        setSuccessMessage(false);
        navigate('/login');
    };

    const handleVerification = async () => {
        setError(null);
        try{
            await verifyDonor(formData.phoneNumber, verificationCode);
            setShowVerificationPopup(false);
            setSuccessMessage(true)
        }catch (err: any) {
            setError("קוד האימות שהוזן שגוי. אנא נסה/י שוב.")
        }
    }

    const handleResendCode = async () => {
        setError(null);
        try {
            await resendVerificationSMSCode(formData.phoneNumber);
            console.log('Verification code resent successfully');
        } catch (err: any) {
            setError(err.message || 'שגיאה בשליחת קוד האימות מחדש. אנא נסה שוב.');
        }
    };

    const handleRegister = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError(null);

        if (formData.email && !validateEmail(formData.email)) {
            setError('כתובת האימייל אינה תקינה');
            return;
        }

        if (formData.password.length < 8) {
            setError('הסיסמה חייבת להיות לפחות 8 תווים');
            return;
        }

        if (formData.password !== formData.confirmPassword) {
            setError('הסיסמאות אינן תואמות');
            return;
        }

        if (!validatePhone(formData.phoneNumber)) {
            setError('מספר הטלפון אינו תקין');
            return;
        }

        if (!hasNoCriminalRecord) {
            setError('עליך לאשר כי אינך בעל עבר פלילי כדי להצטרף אלינו');
            return;
        }

        try {
            await registerDonor(formData);
            setShowVerificationPopup(true)
        } catch (error: any){
            if(error.message === "User already exists")
                setError("מספר הטלפון כבר קיים במערכת")
            else
                setError('ההרשמה נכשלה. נסה שוב מאוחר יותר.');
        }
    };

    return (
        <div className="register-page">
            <div className="register-container">
                <button className="home-button" onClick={() => navigate('/')}>
                    חזרה למסך בית &#8592;
                </button>

                <form onSubmit={handleRegister} className="register-form">
                    <div className="form-logo">
                        <img src={FeedingLogo} alt="Logo" className="logo-image"/>
                    </div>
                    <h2>הצטרפות לעמותה</h2>

                    <div className="form-row">
                        <div className="form-group">
                            <label htmlFor="firstName">
                                שם פרטי <span className="required-asterisk">*</span>
                            </label>
                            <input
                                id="firstName"
                                type="text"
                                value={formData.firstName}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="lastName">
                                שם משפחה <span className="required-asterisk">*</span>
                            </label>
                            <input
                                id="lastName"
                                type="text"
                                value={formData.lastName}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label htmlFor="phoneNumber">
                                מספר טלפון <span className="required-asterisk">*</span>
                            </label>
                            <input
                                id="phoneNumber"
                                type="tel"
                                maxLength={10}
                                value={formData.phoneNumber}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="address">
                                כתובת <span className="required-asterisk">*</span>
                            </label>
                            <input
                                id="address"
                                type="text"
                                value={formData.address}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label htmlFor="password">
                                סיסמא <span className="required-asterisk">*</span>
                            </label>
                            <div className="password-input-wrapper">
                                <input
                                    id="password"
                                    type={showPassword ? 'text' : 'password'}
                                    value={formData.password}
                                    onChange={handleChange}
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

                        <div className="form-group">
                            <label htmlFor="confirmPassword">
                                אישור סיסמא <span className="required-asterisk">*</span>
                            </label>
                            <div className="password-input-wrapper">
                                <input
                                    id="confirmPassword"
                                    type={showConfirmPassword ? 'text' : 'password'}
                                    value={formData.confirmPassword}
                                    onChange={handleChange}
                                    required
                                />
                                <button
                                    type="button"
                                    className="toggle-password-button"
                                    onClick={toggleConfirmPasswordVisibility}
                                >
                                    {showConfirmPassword ? '👁️‍🗨️' : '🗨️'}
                                </button>
                            </div>
                        </div>
                    </div>

                    <div className="form-group checkbox-group">
                        <label htmlFor="noCriminalRecord" className="checkbox-label">
                            <input
                                id="noCriminalRecord"
                                type="checkbox"
                                checked={hasNoCriminalRecord}
                                onChange={handleCheckboxChange}
                                required
                            />
                            אני מאשר/ת שאיני בעל עבר פלילי <span className="required-asterisk">*</span>
                        </label>
                    </div>


                    {error && <p className="error-message">{error}</p>}

                    <button type="submit" className="submit-button">הירשם</button>

                    <div className="extra-options">
                        <span>הצטרפת כבר אלינו? </span>
                        <a onClick={() => navigate('/login')}>
                            התחבר כאן
                        </a>
                    </div>
                </form>
            </div>

            {showVerificationPopup && (
                <>
                    <div className="overlay"></div>
                    <div className="success-popup">
                        <p>אנא הזן את קוד האימות שנשלח אליך:</p>
                        {error && <p className="error-message">{error}</p>}
                        <input
                            type="text"
                            maxLength={6}
                            placeholder="הזן קוד אימות"
                            value={verificationCode}
                            onChange={(e) => {
                                const val = e.target.value.replace(/\D/g, ''); // digits only
                                setVerificationCode(val);
                            }}
                            style={{
                                width: "100%",
                                padding: "10px",
                                border: "1px solid #ddd",
                                borderRadius: "6px",
                                marginBottom: "15px",
                                fontSize: "14px",
                                textAlign: "center"
                            }}
                        />
                        <button onClick={handleVerification}>אמת קוד</button>
                        <p className="resend-container">
                            לא קיבלת קוד אימות?
                            <button className="resend-link" onClick={handleResendCode}>
                                שלח שוב
                            </button>
                        </p>
                    </div>
                </>
            )}

            {successMessage && (
                <>
                    <div className="overlay"></div>
                    <div className="success-popup">
                        <p>הנתונים נקלטו במערכת בהצלחה!</p>
                        <p>פרטיך הועברו לרכזת, ניצור איתך קשר לאחר אישורך.</p>
                        <button onClick={handleSuccessConfirmation}>אישור</button>
                    </div>
                </>
            )}


        </div>
    );

};

export default DonorRegister;
