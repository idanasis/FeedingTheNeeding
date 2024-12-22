import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { login } from './loginRestAPI';
import './Login.css';
import Logo from '../Images/logo.png';

const Login: React.FC = () => {
    const [email, setEmail] = useState<string>('');
    const [password, setPassword] = useState<string>('');
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(false); // State for loading
    const navigate = useNavigate(); // Navigate function to handle routing

    const handleLogin = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError(null);
        setLoading(true); // Set loading to true when starting the request

        try {
            const { token } = await login(email, password);
            localStorage.setItem('token', token);
            navigate('/'); // Navigate to home page after successful login
        } catch (err) {
            setError('אימייל או סיסמא לא נכונים');
            console.error('Login error:', err);
        } finally {
            setLoading(false); // Set loading to false when the request is finished
        }
    };

    return (
        <div className="login-container">
            <h2>התחברות</h2>
            <img src={Logo} alt="Logo"/>
            <form onSubmit={handleLogin} className="login-form">
                <div className="form-group">
                    <label htmlFor="email" className="form-label-right">אימייל:</label>
                    <input
                        id="email"
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)} // updates the email
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="password" className="form-label-right">סיסמה:</label>
                    <input
                        id="password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                {error && <p className="error-message">{error}</p>}
                <button type="submit" className="submit-button" disabled={loading}>
                    {loading ? 'טוען...' : 'התחבר'}
                </button>
            </form>
            <p className="login-redirect">
                עדיין לא הצטרפת אלינו?{' '}
                <span className="login-link" onClick={() => navigate('/donorRegister')}>
                            הצטרף כאן
                        </span>
            </p>
        </div>
    );
};

export default Login;
