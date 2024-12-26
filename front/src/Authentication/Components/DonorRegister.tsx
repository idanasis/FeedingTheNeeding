import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../Styles/DonorRegister.css';
import FeedingLogo from '../Images/logo.png';
import { registerDonor, validateEmail, validatePhone, DonorRegistrationData } from '../RestAPI/donorRegRestAPI';

const DonorRegister: React.FC = () => {
    const [formData, setFormData] = useState<DonorRegistrationData>({
        email: '',
        password: '',
        confirmPassword: '',
        firstName: '',
        lastName: '',
        phoneNumber: '',
        address: '',
        city: ''
    });

    const [hasNoCriminalRecord, setHasNoCriminalRecord] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { id, value } = e.target;
        setFormData((prevData) => ({ ...prevData, [id]: value }));
    };

    const handleCheckboxChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setHasNoCriminalRecord(e.target.checked);
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
            setSuccessMessage(true);
            setTimeout(() => navigate('/login'), 2000);
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
                <form onSubmit={handleRegister} className="register-form">
                    <div className="form-logo">
                        <img src={FeedingLogo} alt="Logo" className="logo-image"/>
                    </div>
                    <h2>הרשמה</h2>

                    <div className="form-row">
                        <div className="form-group">
                            <label htmlFor="email">אימייל (אופציונלי)</label>
                            <input
                                id="email"
                                type="email"
                                value={formData.email}
                                onChange={handleChange}
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="phoneNumber">
                                מספר טלפון <span className="required-asterisk">*</span>
                            </label>
                            <input
                                id="phoneNumber"
                                type="tel"
                                value={formData.phoneNumber}
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
                            <input
                                id="password"
                                type="password"
                                value={formData.password}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="confirmPassword">
                                אישור סיסמא <span className="required-asterisk">*</span>
                            </label>
                            <input
                                id="confirmPassword"
                                type="password"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

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

                        <div className="form-group">
                            <label htmlFor="city">
                                עיר מגורים <span className="required-asterisk">*</span>
                            </label>
                            <input
                                id="city"
                                type="text"
                                value={formData.city}
                                onChange={handleChange}
                                required
                            />
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

                    <p className="login-redirect">
                        הצטרפת כבר אלינו?{' '}
                        <span className="login-link" onClick={() => navigate('/login')}>
                            התחבר כאן
                        </span>
                    </p>
                </form>
            </div>

            {successMessage && (
                <div className="success-popup">
                    <p>ההרשמה בוצעה בהצלחה!</p>
                </div>
            )}
        </div>
    );
};

export default DonorRegister;
