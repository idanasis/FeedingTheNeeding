import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom'; // Import Link
import './Login.css';
import Logo from './logo.png';

interface LoginResponse {
    token: string;
    expirationTime: number;
}

const Login: React.FC = () => {
    const [email, setEmail] = useState<string>('');
    const [password, setPassword] = useState<string>('');
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(false); // State for loading
    const navigate = useNavigate();

    const handleLogin = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError(null);
        setLoading(true); // Set loading to true when starting the request

        try {
            const response = await axios.post<LoginResponse>('http://localhost:8080/auth/login', {
                email,
                password,
            });

            const { token } = response.data;
            localStorage.setItem('token', token);
            navigate('/dashboard'); // Navigate to dashboard after successful login
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
                        onChange={(e) => setEmail(e.target.value)}
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
