import React, { useState, useEffect } from 'react';
import {
    PendingCookDTO,
    getPendingRequests,
    approveCookRequest,
    rejectCookRequest,
    getFoodConstraints,
    getAcceptedConstraints
} from '../RestAPI/CookManagerRestAPI';

interface SummaryData {
    needed: Record<string, number>;
    accepted: Record<string, number>;
}

const PendingRequests: React.FC = () => {
    const [pendingRequests, setPendingRequests] = useState<PendingCookDTO[]>([]);
    const [selectedDate, setSelectedDate] = useState<string>('');
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>('');
    const [expandedConstraintIds, setExpandedConstraintIds] = useState<Set<number>>(new Set());
    const [summaryData, setSummaryData] = useState<SummaryData>({ needed: {}, accepted: {} });

    const fetchAllData = async (date: string) => {
        try {
            setLoading(true);
            setError('');

            const [pendingData, neededFood, acceptedFood] = await Promise.all([
                getPendingRequests(date),
                getFoodConstraints(date),
                getAcceptedConstraints(date)
            ]);

            setPendingRequests(pendingData);
            setSummaryData({
                needed: neededFood,
                accepted: acceptedFood
            });
        } catch (err) {
            setError('בעיה בטעינת הנתונים');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (selectedDate) {
            const intervalId = setInterval(() => {
                fetchAllData(selectedDate);
            }, 60000);

            return () => clearInterval(intervalId);
        }
    }, [selectedDate]);

    const handleDateChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const newDate = event.target.value;
        setSelectedDate(newDate);
        if (newDate) {
            fetchAllData(newDate);
        }
    };

    const handleApprove = async (constraintId: number) => {
        try {
            await approveCookRequest(constraintId);
            fetchAllData(selectedDate);
            alert('הבקשה אושרה בהצלחה');
        } catch (err) {
            alert('שגיאה באישור הבקשה');
            console.error(err);
        }
    };

    const handleReject = async (constraintId: number) => {
        try {
            await rejectCookRequest(constraintId);
            fetchAllData(selectedDate);
            alert('הבקשה נדחתה בהצלחה');
        } catch (err) {
            alert('שגיאה בדחיית הבקשה');
            console.error(err);
        }
    };

    const toggleRequestDetails = (constraintId: number, event: React.MouseEvent) => {
        event.stopPropagation();
        setExpandedConstraintIds(prev => {
            const newSet = new Set(prev);
            if (newSet.has(constraintId)) {
                newSet.delete(constraintId);
            } else {
                newSet.add(constraintId);
            }
            return newSet;
        });
    };

    const renderConstraints = (constraints: Record<string, number>) => {
        return Object.entries(constraints).map(([constraint, amount]) => (
            <div key={constraint} className="constraint-item">
                <span className="constraint-name">{constraint}</span>
                <span className="constraint-amount">{amount}</span>
            </div>
        ));
    };

    const renderSidebar = () => {
        const { needed, accepted } = summaryData;
        const hasConstraints = Object.keys(needed).length > 0;

        return (
            <div className="constraints-sidebar">
                <div className="sidebar-header">
                    <h3>סיכום מנות</h3>
                    <div className="sidebar-date">
                        {selectedDate ? new Date(selectedDate).toLocaleDateString('he-IL') : 'לא נבחר תאריך'}
                    </div>
                </div>

                <div className="sidebar-content">
                    {!hasConstraints && (
                        <div className="no-constraints">
                            אין מנות להיום
                        </div>
                    )}

                    {hasConstraints && Object.keys(needed).map((constraint) => {
                        const neededAmount = needed[constraint] || 0;
                        const acceptedAmount = accepted[constraint] || 0;
                        const remainingAmount = Math.max(0, neededAmount - acceptedAmount);

                        return (
                            <div key={constraint} className="sidebar-item">
                                <div className="constraint-header">
                                    <span className="sidebar-constraint-name">{constraint}</span>
                                </div>
                                <div className="constraint-stats">
                                    <div className="stat-item">
                                        <span className="stat-label">נדרש:</span>
                                        <span className="stat-value needed">{neededAmount}</span>
                                    </div>
                                    <div className="stat-item">
                                        <span className="stat-label">אושר:</span>
                                        <span className="stat-value accepted">{acceptedAmount}</span>
                                    </div>
                                    {remainingAmount > 0 && (
                                        <div className="stat-item">
                                            <span className="stat-label">חסר:</span>
                                            <span className="stat-value remaining">{remainingAmount}</span>
                                        </div>
                                    )}
                                </div>
                            </div>
                        );
                    })}
                </div>
            </div>
        );
    };

    const isExpanded = (constraintId: number): boolean => {
        return expandedConstraintIds.has(constraintId);
    };

    return (
        <div className="app-container">
            {selectedDate && renderSidebar()}

            <div className="pending-requests-container">
                <div className="content-wrapper">
                    <div className="date-selection-section">
                        <h1>בקשות בישול ממתינות</h1>
                        <div className="date-input-wrapper">
                            <input
                                type="date"
                                value={selectedDate}
                                onChange={handleDateChange}
                                className="date-input"
                            />
                        </div>
                    </div>

                    {loading && <div className="loading">טוען...</div>}
                    {error && <div className="error-message">{error}</div>}

                    {!loading && !error && pendingRequests.length === 0 && (
                        <div className="no-requests">אין בקשות ממתינות לתאריך זה</div>
                    )}

                    {pendingRequests.length > 0 && (
                        <div className="table-section">
                            <table className="requests-table">
                                <thead>
                                    <tr>
                                        <th className="name-column">שם המבשל</th>
                                        <th className="date-column">תאריך</th>
                                        <th className="time-column">שעות</th>
                                        <th className="address-column">כתובת</th>
                                        <th className="constraints-column">דרישות תזונה</th>
                                        <th className="actions-column">פעולות</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {pendingRequests.map((request) => (
                                        <React.Fragment key={request.constraintId}>
                                            <tr>
                                                <td className="name-column">{request.name}</td>
                                                <td className="date-column">
                                                    {new Date(request.date).toLocaleDateString('he-IL')}
                                                </td>
                                                <td className="time-column">
                                                    {request.startTime} - {request.endTime}
                                                </td>
                                                <td className="address-column">{request.addr}</td>
                                                <td className="constraints-column">
                                                    <button
                                                        className="view-constraints-button"
                                                        onClick={(e) => toggleRequestDetails(request.constraintId, e)}
                                                    >
                                                        {isExpanded(request.constraintId) ? 'הסתר פרטים' : 'הצג פרטים'}
                                                    </button>
                                                </td>
                                                <td className="actions-column">
                                                    <div className="action-buttons">
                                                        <button
                                                            className="approve-button"
                                                            onClick={() => handleApprove(request.constraintId)}
                                                        >
                                                            אישור
                                                        </button>
                                                        <button
                                                            className="reject-button"
                                                            onClick={() => handleReject(request.constraintId)}
                                                        >
                                                            דחייה
                                                        </button>
                                                    </div>
                                                </td>
                                            </tr>
                                            {isExpanded(request.constraintId) && (
                                                <tr className="constraints-details-row">
                                                    <td colSpan={6}>
                                                        <div className="constraints-details">
                                                            {renderConstraints(request.constraints)}
                                                        </div>
                                                    </td>
                                                </tr>
                                            )}
                                        </React.Fragment>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default PendingRequests;