import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../Styles/EditDonorDetails.css';
import FeedingLogo from '../Images/logo.png';
import { Donor, getAuthenticatedDonor, updateDonor } from '../RestAPI/EditDonorDetailsRestAPI';

const DonorEdit: React.FC = () => {
    const [formData, setFormData] = useState<Donor | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchDonorData = async () => {
            try {
                const donor = await getAuthenticatedDonor();
                setFormData(donor);
            } catch (err: any) {
                setError(err.message);
                // // Optionally redirect to login if unauthorized
                // if (err.message === 'No authentication token found.' || err.message === 'Failed to fetch donor data.') {
                //     navigate('/login');
                // }
            } finally {
                setLoading(false);
            }
        };

        fetchDonorData();
    }, [navigate]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (!formData) return;
        const { id, value, type, checked } = e.target;
        setFormData({
            ...formData,
            [id]: type === 'checkbox' ? checked : value,
        });
    };

    const validateEmail = (email: string): boolean => {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    };

    const validatePhone = (phoneNumber: string): boolean => {
        return /^05\d{8}$/.test(phoneNumber);
    };

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError(null);
        setSuccessMessage(null);

        if (formData) {
            // Basic validation
            if (formData.email && !validateEmail(formData.email)) {
                setError('כתובת האימייל אינה תקינה');
                return;
            }

            if (!validatePhone(formData.phoneNumber)) {
                setError('מספר הטלפון אינו תקין');
                return;
            }

            try {
                await updateDonor(formData);
                setSuccessMessage('הנתונים עודכנו בהצלחה!');
            } catch (err: any) {
                setError(err.message || 'עדכון נתונים נכשלה. נסה שוב מאוחר יותר.');
            }
        }
    };

    if (loading) {
        return <div className="edit-page"><p>טוען...</p></div>;
    }

    return (
        <div className="edit-page">
            <div className="edit-container">
                {/* Home Button */}
                <button className="home-button" onClick={() => navigate('/')}>
                    חזרה למסך בית &#8592;
                </button>

                <form onSubmit={handleSubmit} className="edit-form">
                    <div className="form-logo">
                        <img src={FeedingLogo} alt="Logo" className="logo-image" />
                    </div>
                    <h2>עריכת פרטים אישיים</h2>

                    <div className="form-row">
                        <div className="form-group">
                            <label htmlFor="firstName">
                                שם פרטי <span className="required-asterisk">*</span>
                            </label>
                            <input
                                id="firstName"
                                type="text"
                                value={formData?.firstName || ''}
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
                                value={formData?.lastName || ''}
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
                                value={formData?.phoneNumber || ''}
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
                                value={formData?.address || ''}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    {error && <p className="error-message">{error}</p>}
                    {successMessage && <p className="success-message">{successMessage}</p>}

                    <button type="submit" className="submit-button">שמירת שינויים</button>

                    <div className="extra-options">
                        <span>מעוניין לשנות סיסמה? </span>
                        <a onClick={() => navigate('/reset-password')}>
                            לחץ כאן
                        </a>
                    </div>
                </form>
            </div>

            {successMessage && (
                <>
                    <div className="overlay"></div>
                    <div className="success-popup">
                        <p>הנתונים עודכנו בהצלחה!</p>
                        <button onClick={() => setSuccessMessage(null)}>אישור</button>
                    </div>
                </>
            )}
        </div>
    );

};

export default DonorEdit;
