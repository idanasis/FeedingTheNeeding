import React, { useState, useEffect } from 'react';
import { PendingCookDTO, DriverConstraints, getCookConstraints, getDriverConstraints } from '../RestAPI/constraintsViewRestAPI.ts';
import '../Styles/constraintsView.css';

interface UserConstraintsProps {
    userId: number;
    userType: 'cook' | 'driver' | 'both';
}

const CollapsibleConstraints: React.FC<{ constraints: Record<string, number> }> = ({ constraints }) => {
    const [isExpanded, setIsExpanded] = useState(false);
    const constraintsArray = Object.entries(constraints);
    const previewCount = 2; // Show first 2 constraints when collapsed

    const formatConstraints = (entries: [string, number][]) => {
        return entries.map(([key, value]) => (
            <span key={key} className="constraint-item">
                {key}: {value}
            </span>
        ));
    };

    if (constraintsArray.length <= previewCount) {
        return (
            <div className="constraints-cell">
                {formatConstraints(constraintsArray)}
            </div>
        );
    }

    return (
        <div className="constraints-cell">
            <div className="constraints-content">
                {isExpanded ? (
                    formatConstraints(constraintsArray)
                ) : (
                    <>
                        {formatConstraints(constraintsArray.slice(0, previewCount))}
                        <span className="constraints-ellipsis">...</span>
                    </>
                )}
            </div>
            <button
                className="expand-button"
                onClick={() => setIsExpanded(!isExpanded)}
            >
                {isExpanded ? 'הצג פחות' : `הצג עוד (${constraintsArray.length - previewCount})`}
            </button>
        </div>
    );
};

const UserConstraints: React.FC<UserConstraintsProps> = ({ userId, userType }) => {
    const [cookConstraints, setCookConstraints] = useState<PendingCookDTO[]>([]);
    const [driverConstraints, setDriverConstraints] = useState<DriverConstraints[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>('');

    const fetchConstraints = async () => {
        try {
            setLoading(true);
            setError('');

            const cookData = await getCookConstraints();
            setCookConstraints(cookData);

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

    return (
        <div className="constraints-container">
            <div className="content-wrapper">
                <div className="header-section">
                    <h1>האילוצים שלי</h1>
                </div>

                {loading && <div className="loading">טוען...</div>}
                {error && <div className="error-message">{error}</div>}

                {!loading && !error && (
                    <div className="table-section">
                        <h2>אילוצי בישול</h2>
                        {cookConstraints.length === 0 ? (
                            <div className="no-constraints">אין אילוצי בישול</div>
                        ) : (
                            <div className="table-wrapper">
                                <table className="constraints-table">
                                    <thead>
                                        <tr>
                                            <th>תאריך</th>
                                            <th>שעת התחלה</th>
                                            <th>שעת סיום</th>
                                            <th>כתובת</th>
                                            <th>אילוצים</th>
                                            <th>סטטוס</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {cookConstraints.map((constraint, index) => (
                                            <tr key={index}>
                                                <td>{new Date(constraint.date).toLocaleDateString('he-IL')}</td>
                                                <td>{constraint.startTime}</td>
                                                <td>{constraint.endTime}</td>
                                                <td>{constraint.location}</td>
                                                <td>
                                                    <CollapsibleConstraints constraints={constraint.constraints} />
                                                </td>
                                                <td>
                                                    <span className={constraint.status === "Accepted" ? 'status-approved' : 'status-pending'}>
                                                        {constraint.status === "Accepted" ? 'מאושר' : 'ממתין'}
                                                    </span>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </div>
                )}

                {!loading && !error && (userType === 'driver' || userType === 'both') && (
                    <div className="table-section">
                        <h2>אילוצי נהיגה</h2>
                        {driverConstraints.length === 0 ? (
                            <div className="no-constraints">אין אילוצי נהיגה</div>
                        ) : (
                            <div className="table-wrapper">
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
                                                <td>{constraint.start_hour}:00</td>
                                                <td>{constraint.end_hour}:00</td>
                                                <td>{constraint.startLocation}</td>
                                                <td>{constraint.requests || 'אין בקשות'}</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default UserConstraints;