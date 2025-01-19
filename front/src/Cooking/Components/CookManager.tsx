import React, { useState, useEffect } from 'react';
import { PendingCookDTO, getPendingRequests, approveCookRequest, rejectCookRequest } from '../RestAPI/CookManagerRestAPI';
import '../Styles/CookManager.css'

const PendingRequests: React.FC = () => {
    const [pendingRequests, setPendingRequests] = useState<PendingCookDTO[]>([]);
    const [selectedDate, setSelectedDate] = useState<string>('');
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>('');
    const [expandedRequestId, setExpandedRequestId] = useState<number | null>(null);

    const fetchPendingRequests = async (date: string) => {
        try {
            setLoading(true);
            setError('');
            const data = await getPendingRequests(date);
            setPendingRequests(data);
        } catch (err) {
            setError('בעיה בטעינת הבקשות');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleDateChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const newDate = event.target.value;
        setSelectedDate(newDate);
        if (newDate) {
            fetchPendingRequests(newDate);
        }
    };

    const handleApprove = async (requestId: number) => {
        try {
            await approveCookRequest(requestId);
            fetchPendingRequests(selectedDate);
            alert('הבקשה אושרה בהצלחה');
        } catch (err) {
            alert('שגיאה באישור הבקשה');
            console.error(err);
        }
    };

    const handleReject = async (requestId: number) => {
        try {
            await rejectCookRequest(requestId);
            fetchPendingRequests(selectedDate);
            alert('הבקשה נדחתה בהצלחה');
        } catch (err) {
            alert('שגיאה בדחיית הבקשה');
            console.error(err);
        }
    };

    const toggleRequestDetails = (requestId: number) => {
        setExpandedRequestId(expandedRequestId === requestId ? null : requestId);
    };

    const renderConstraints = (constraints: Record<string, number>) => {
        return Object.entries(constraints).map(([constraint, amount]) => (
            <div key={constraint} className="constraint-item">
                <span className="constraint-name">{constraint}</span>
                <span className="constraint-amount">{amount}</span>
            </div>
        ));
    };

    return (
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
                                    <React.Fragment key={request.id}>
                                        <tr className={expandedRequestId === request.id ? 'expanded-row' : ''}>
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
                                                    onClick={(e) => {
                                                        e.stopPropagation();
                                                        toggleRequestDetails(request.id);
                                                    }}
                                                >
                                                    {expandedRequestId === request.id ? 'הסתר פרטים' : 'הצג פרטים'}
                                                </button>
                                            </td>
                                            <td className="actions-column">
                                                <div className="action-buttons">
                                                    <button
                                                        className="approve-button"
                                                        onClick={(e) => {
                                                            e.stopPropagation();
                                                            handleApprove(request.id);
                                                        }}
                                                    >
                                                        אישור
                                                    </button>
                                                    <button
                                                        className="reject-button"
                                                        onClick={(e) => {
                                                            e.stopPropagation();
                                                            handleReject(request.id);
                                                        }}
                                                    >
                                                        דחייה
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                        {expandedRequestId === request.id && (
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
    );
};

export default PendingRequests;