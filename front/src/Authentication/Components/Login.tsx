import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../RestAPI/loginRestAPI';
import '../Styles/Login.css';
import Logo from '../Images/logo.png';

const Login: React.FC = () => {
    const [phoneNumber, setPhoneNumber] = useState<string>('');
    const [password, setPassword] = useState<string>('');
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(false);
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

    return (
        <div className="login-page">
            <div className="login-container">
                <h2>התחברות</h2>
                <img src={Logo} alt="Logo" />
                <form onSubmit={handleLogin} className="login-form">
                    <div className="form-group">
                        <label htmlFor="phoneNumber" className="form-label-right">
                            מספר טלפון:
                        </label>
                        <input
                            id="phoneNumber"
                            type="tel"
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
                </div>
            </div>
        </div>
    );
};

export default Login;