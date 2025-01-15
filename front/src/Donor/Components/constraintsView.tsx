import React, { useState, useEffect } from 'react';
import { PendingCookDTO, DriverConstraints, getCookConstraints, getDriverConstraints } from '../RestAPI/constraintsViewRestAPI.ts';
import '../Styles/constraintsView.css'

interface UserConstraintsProps {
    userId: number;
    userType: 'cook' | 'driver' | 'both';
}

const UserConstraints: React.FC<UserConstraintsProps> = ({ userId, userType }) => {
    const [cookConstraints, setCookConstraints] = useState<PendingCookDTO[]>([]);
    const [driverConstraints, setDriverConstraints] = useState<DriverConstraints[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>('');

    const fetchConstraints = async () => {
        try {
            setLoading(true);
            setError('');

            //if (userType === 'cook' || userType === 'both') {
            const cookData = await getCookConstraints(1);
            setCookConstraints(cookData);
            //}

            //if (userType === 'driver' || userType === 'both') {
              //  const driverData = await getDriverConstraints(userId);
               // setDriverConstraints(driverData);
            //}
        } catch (err) {
            setError('שגיאה בטעינת האילוצים');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchConstraints();
    }, [userId, userType]);

    const formatTime = (time: string | number) => {
        return typeof time === 'number' ? `${time}:00` : time;
    };

    return (
        <div className="constraints-container">
            <div className="content-wrapper">
                <div className="header-section">
                    <h1>האילוצים שלי</h1>
                </div>

                {loading && <div className="loading">טוען...</div>}
                {error && <div className="error-message">{error}</div>}

                {!loading && !error && /*(userType === 'cook' || userType === 'both') &&*/ (
                    <div className="table-section">
                        <h2>אילוצי בישול</h2>
                        {cookConstraints.length === 0 ? (
                            <div className="no-constraints">אין אילוצי בישול</div>
                        ) : (
                            <table className="constraints-table">
                                <thead>
                                    <tr>
                                        <th>תאריך</th>
                                        <th>שעת התחלה</th>
                                        <th>שעת סיום</th>
                                        <th>כמות מנות</th>
                                        <th>כתובת</th>
                                        <th>סטטוס</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {cookConstraints.map((constraint, index) => (
                                        <tr key={index}>
                                            <td>{new Date(constraint.date).toLocaleDateString('he-IL')}</td>
                                            <td>{formatTime(constraint.startTime)}</td>
                                            <td>{formatTime(constraint.endTime)}</td>
                                            <td>{constraint.platesNum}</td>
                                            <td>{constraint.location}</td>
                                            <td>
                                                <span className={constraint.status === "Accepted" ? 'status-approved' : 'status-pending'}>
                                                    {constraint.status === "Accepted" ? 'מאושר' : 'ממתין'}
                                                </span>
                                            </td>
                                             </tr>
                                    ))}
                                </tbody>
                            </table>
                        )}
                    </div>
                )}

                {!loading && !error && (userType === 'driver' || userType === 'both') && (
                    <div className="table-section">
                        <h2>אילוצי נהיגה</h2>
                        {driverConstraints.length === 0 ? (
                            <div className="no-constraints">אין אילוצי נהיגה</div>
                        ) : (
                            <table className="constraints-table">
                                <thead>
                                    <tr>
                                        <th>תאריך</th>
                                        <th>שעת התחלה</th>
                                        <th>שעת סיום</th>
                                        <th>מיקום התחלה</th>
                                        <th>בקשות</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {driverConstraints.map((constraint, index) => (
                                        <tr key={index}>
                                            <td>{new Date(constraint.date).toLocaleDateString('he-IL')}</td>
                                            <td>{formatTime(constraint.start_hour)}</td>
                                            <td>{formatTime(constraint.end_hour)}</td>
                                            <td>{constraint.startLocation}</td>
                                            <td>{constraint.requests || 'אין בקשות'}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default UserConstraints;