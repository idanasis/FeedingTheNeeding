import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './DonorRegister.css';
import FeedingLogo from '../Images/logo.png';

const Register: React.FC = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [phone, setPhone] = useState('');
    const [address, setAddress] = useState('');
    const [city, setCity] = useState('');
    const [error, setError] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState(false);
    const navigate = useNavigate();

    const validateEmail = (email: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    const validatePhone = (phone: string) => /^05\d{8}$/.test(phone);

    const handleRegister = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError(null);

        if (!validateEmail(email)) {
            setError('כתובת האימייל אינה תקינה');
            return;
        }

        if (password.length < 8) {
            setError('הסיסמה חייבת להיות לפחות 8 תווים');
            return;
        }

        if (password !== confirmPassword) {
            setError('הסיסמאות אינן תואמות');
            return;
        }

        if (!validatePhone(phone)) {
            setError('מספר הטלפון אינו תקין');
            return;
        }

        // שליחה לשרת (לצורך הדוגמה נתוני הטופס נשלחים לקונסול)
        console.log({ email, password, firstName, lastName, phone, address, city });

        setSuccessMessage(true); // after registrarion

        setTimeout(() => {
            navigate('/login');
        }, 2000);
    };

    return (
        <div className="register-page">
            <div className="register-container">
                <form onSubmit={handleRegister} className="register-form">
                    <div className="form-logo">
                        <img src={FeedingLogo} alt="Logo" className="logo-image" />
                    </div>
                    <h2>הרשמה</h2>

                    <div className="form-row">
                        <div className="form-group">
                            <label htmlFor="email">אימייל:</label>
                            <input
                                id="email"
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="phone">מספר טלפון:</label>
                            <input
                                id="phone"
                                type="tel"
                                value={phone}
                                onChange={(e) => setPhone(e.target.value)}
                                required
                            />
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label htmlFor="password">סיסמא:</label>
                            <input
                                id="password"
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="confirmPassword">אישור סיסמא:</label>
                            <input
                                id="confirmPassword"
                                type="password"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                required
                            />
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label htmlFor="lastName">שם משפחה:</label>
                            <input
                                id="lastName"
                                type="text"
                                value={lastName}
                                onChange={(e) => setLastName(e.target.value)}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="firstName">שם פרטי:</label>
                            <input
                                id="firstName"
                                type="text"
                                value={firstName}
                                onChange={(e) => setFirstName(e.target.value)}
                                required
                            />
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label htmlFor="address">כתובת:</label>
                            <input
                                id="address"
                                type="text"
                                value={address}
                                onChange={(e) => setAddress(e.target.value)}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="city">עיר מגורים:</label>
                            <input
                                id="city"
                                type="text"
                                value={city}
                                onChange={(e) => setCity(e.target.value)}
                                required
                            />
                        </div>
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

export default Register;
