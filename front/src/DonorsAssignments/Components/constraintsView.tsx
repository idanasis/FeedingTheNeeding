import React, { useState, useEffect, useCallback } from 'react';
import { PendingCookDTO, DriverConstraints, DriverRoutes, getCookConstraints, getDriverConstraints, getDriverRoutes } from '../RestAPI/constraintsViewRestAPI';
import DiveHeader from '../../GoPage/DiveHeader';
import '../Styles/constraintsView.css';

interface UserConstraintsProps {
    userId?: number;
}

const getStatusInHebrew = (status: string): string => {
    switch (status) {
        case 'Start':
            return 'התחלה';
        case 'Pickup':
            return 'איסוף';
        case 'Deliver':
            return 'מסירה';
        default:
            return status;
    }
};

const CollapsibleConstraints: React.FC<{ constraints: Record<string, number> }> = ({ constraints }) => {
    const [isExpanded, setIsExpanded] = useState(false);
    const constraintsArray = Object.entries(constraints);
    const previewCount = 2;

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

const UserConstraints: React.FC<UserConstraintsProps> = () => {
    const [cookConstraints, setCookConstraints] = useState<PendingCookDTO[]>([]);
    const [driverConstraints, setDriverConstraints] = useState<DriverConstraints[]>([]);
    const [driverRoutes, setDriverRoutes] = useState<DriverRoutes[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>('');

    const fetchConstraints = useCallback(async () => {
        try {
            console.log('Starting to fetch constraints');
            setLoading(true);
            setError('');

            try {
                console.log('Fetching cook constraints...');
                const cookData = await getCookConstraints();
                console.log('Received cook data:', cookData);
                setCookConstraints(cookData);
            } catch (err) {
                console.log('No cook constraints found or not authorized:', err);
            }

            try {
                console.log('Fetching driver constraints...');
                const driverData = await getDriverConstraints();
                console.log('Received driver data:', driverData);
                setDriverConstraints(driverData);
            } catch (err) {
                console.log('No driver constraints found or not authorized:', err);
            }

            try {
                console.log('Fetching driver routes for next 14 days...');
                const allRoutes: DriverRoutes[] = [];
                const today = new Date();

                for (let i = 0; i < 14; i++) {
                    const date = new Date(today);
                    date.setDate(today.getDate() + i);
                    const formattedDate = date.toISOString().split('T')[0];

                    try {
                        const routeData = await getDriverRoutes(formattedDate);
                        if (routeData) {
                            allRoutes.push(routeData);
                        }
                    } catch (routeErr) {
                        console.log(`No routes found for date ${formattedDate}`);
                    }
                }

                console.log('Retrieved all driver routes:', allRoutes);
                setDriverRoutes(allRoutes);
            } catch (err) {
                console.log('Error fetching driver routes:', err);
            }

        } catch (err) {
            console.error('Error in fetchConstraints:', err);
            setError('שגיאה בטעינת האילוצים');
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchConstraints();
    }, [fetchConstraints]);

    return (
        <>
            <DiveHeader />
            <div className="constraints-container">
                <div className="content-wrapper">
                    <div className="header-section">
                        <h1>האילוצים שלי</h1>
                    </div>

                    {loading && <div className="loading">טוען...</div>}
                    {error && <div className="error-message">{error}</div>}

                    {!loading && !error && (
                        <>
                            {cookConstraints.length > 0 && (
                                <div className="table-section">
                                    <h2>אילוצי בישול</h2>
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
                                                        <td>{constraint.address}</td>
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
                                </div>
                            )}

                            {driverConstraints.length > 0 && (
                                <div className="table-section">
                                    <h2>אילוצי נהיגה (ממתינים לאישור)</h2>
                                    <div className="table-wrapper">
                                        <table className="constraints-table">
                                            <thead>
                                                <tr>
                                                    <th>תאריך</th>
                                                    <th>שעת התחלה</th>
                                                    <th>שעת סיום</th>
                                                    <th>מיקום התחלה</th>
                                                    <th>בקשות</th>
                                                    <th>סטטוס</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {driverConstraints.map((constraint, index) => (
                                                    <tr key={index}>
                                                        <td>{new Date(constraint.date).toLocaleDateString('he-IL')}</td>
                                                        <td>{constraint.startHour}</td>
                                                        <td>{constraint.endHour}</td>
                                                        <td>{constraint.startLocation}</td>
                                                        <td>{constraint.requests || 'אין בקשות'}</td>
                                                        <td>
                                                            <span className='status-pending'>
                                                                ממתין לאישור
                                                            </span>
                                                        </td>
                                                    </tr>
                                                ))}
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            )}

                            {driverRoutes.length > 0 && (
                                <div className="table-section">
                                    <h2>מסלולי נהיגה (מאושרים)</h2>
                                    <div className="table-wrapper">
                                        {driverRoutes.map((routeDay, dayIndex) => (
                                            <div key={dayIndex} className="route-day-section">
                                                <h3 className="route-date">
                                                    {new Date(routeDay.date).toLocaleDateString('he-IL')}
                                                </h3>
                                                <table className="constraints-table">
                                                    <thead>
                                                        <tr>
                                                            <th>ביקור</th>
                                                            <th>כתובת</th>
                                                            <th>שם מלא</th>
                                                            <th>טלפון</th>
                                                            <th>שעה מקסימלית</th>
                                                            <th>סטטוס</th>
                                                            <th>עדיפות</th>
                                                            <th>הערה</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        {routeDay.visit?.map((visit, index) => (
                                                            <tr key={index}>
                                                                <td>{visit.visitId}</td>
                                                                <td>{visit.address}</td>
                                                                <td>{`${visit.firstName} ${visit.lastName}`}</td>
                                                                <td>{visit.phoneNumber}</td>
                                                                <td>{visit.endHour}</td>
                                                                <td>
                                                                    <span className={`status-approved status-${visit.status.toLowerCase()}`}>
                                                                        {getStatusInHebrew(visit.status)}
                                                                    </span>
                                                                </td>
                                                                <td>{visit.priority}</td>
                                                                <td>{visit.note || 'אין הערות'}</td>
                                                            </tr>
                                                        ))}
                                                    </tbody>
                                                </table>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            )}

                            {!loading && !error &&
                                cookConstraints.length === 0 &&
                                driverConstraints.length === 0 &&
                                driverRoutes.length === 0 && (
                                <div className="no-constraints">אין אילוצים או מסלולים להצגה</div>
                            )}
                        </>
                    )}
                </div>
            </div>
        </>
    );
};
export default UserConstraints;