import React, { useState, useEffect } from 'react';
import {
    PendingCookDTO,
    getAllRequests,
    approveCookRequest,
    rejectCookRequest,
    getFoodConstraints,
    getAcceptedConstraints,
    updateConstraint
} from '../RestAPI/CookManagerRestAPI';
import '../Styles/CookManager.css';

interface SummaryData {
    needed: Record<string, number>;
    accepted: Record<string, number>;
}

interface EditableConstraints {
    [key: string]: {
        originalValue: number;
        currentValue: number;
    };
}

type RequestStatus = 'Pending' | 'Accepted' | 'Declined';

const PendingRequests: React.FC = () => {
    const [allRequests, setAllRequests] = useState<PendingCookDTO[]>([]);
    const [selectedDate, setSelectedDate] = useState<string>('');
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>('');
    const [expandedConstraintIds, setExpandedConstraintIds] = useState<Set<number>>(new Set());
    const [summaryData, setSummaryData] = useState<SummaryData>({ needed: {}, accepted: {} });
    const [editableConstraints, setEditableConstraints] = useState<Record<number, EditableConstraints>>({});

    const fetchAllData = async (date: string) => {
        try {
            setLoading(true);
            setError('');

            const [requests, neededFood, acceptedFood] = await Promise.all([
                getAllRequests(date),
                getFoodConstraints(date),
                getAcceptedConstraints(date)
            ]);

            setAllRequests(requests);
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
            setAllRequests(prev => prev.map(request =>
                request.constraintId === constraintId
                    ? { ...request, status: 'Accepted' }
                    : request
            ));
            alert('הבקשה אושרה בהצלחה');
        } catch (err) {
            alert('שגיאה באישור הבקשה');
            console.error(err);
        }
    };

    const handleReject = async (constraintId: number) => {
        try {
            await rejectCookRequest(constraintId);
            setAllRequests(prev => prev.map(request =>
                request.constraintId === constraintId
                    ? { ...request, status: 'Declined' }
                    : request
            ));
            alert('הבקשה נדחתה בהצלחה');
        } catch (err) {
            alert('שגיאה בדחיית הבקשה');
            console.error(err);
        }
    };

    const handleUndo = async (constraintId: number) => {
        try {
            setAllRequests(prev => prev.map(request =>
                request.constraintId === constraintId
                    ? { ...request, status: 'Pending' }
                    : request
            ));
            alert('הפעולה בוטלה בהצלחה');
        } catch (err) {
            alert('שגיאה בביטול הפעולה');
            console.error(err);
        }
    };

    const handleUpdateAndApprove = async (constraintId: number) => {
        try {
            const updatedConstraints: Record<string, number> = {};
            Object.entries(editableConstraints[constraintId] || {}).forEach(([key, value]) => {
                updatedConstraints[key] = value.currentValue;
            });

            await updateConstraint(constraintId, updatedConstraints);
            await approveCookRequest(constraintId);
            setAllRequests(prev => prev.map(request =>
                request.constraintId === constraintId
                    ? { ...request, status: 'Accepted' }
                    : request
            ));
            alert('הבקשה עודכנה ואושרה בהצלחה');
        } catch (err) {
            alert('שגיאה בעדכון ואישור הבקשה');
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
                const request = allRequests.find(r => r.constraintId === constraintId);
                if (request) {
                    setEditableConstraints(prev => ({
                        ...prev,
                        [constraintId]: Object.entries(request.constraints).reduce((acc, [key, value]) => ({
                            ...acc,
                            [key]: { originalValue: value, currentValue: value }
                        }), {})
                    }));
                }
            }
            return newSet;
        });
    };

    const handleConstraintChange = (constraintId: number, constraintKey: string, newValue: number) => {
        setEditableConstraints(prev => ({
            ...prev,
            [constraintId]: {
                ...prev[constraintId],
                [constraintKey]: {
                    ...prev[constraintId][constraintKey],
                    currentValue: newValue
                }
            }
        }));
    };

    const renderConstraints = (constraintId: number, constraints: Record<string, number>) => {
        const editableData = editableConstraints[constraintId] || {};

        return Object.entries(constraints).map(([constraint, amount]) => (
            <div key={constraint} className="constraint-item">
                <span className="constraint-name">{constraint}</span>
                <div className="constraint-controls">
                    <button
                        className="constraint-button"
                        onClick={() => handleConstraintChange(
                            constraintId,
                            constraint,
                            Math.max(0, (editableData[constraint]?.currentValue || amount) - 1)
                        )}
                    >
                        -
                    </button>
                    <span className="constraint-amount">
                        {editableData[constraint]?.currentValue ?? amount}
                    </span>
                    <button
                        className="constraint-button"
                        onClick={() => handleConstraintChange(
                            constraintId,
                            constraint,
                            (editableData[constraint]?.currentValue || amount) + 1
                        )}
                    >
                        +
                    </button>
                </div>
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

    const hasConstraintChanges = (constraintId: number): boolean => {
        const editableData = editableConstraints[constraintId];
        if (!editableData) return false;

        return Object.values(editableData).some(
            ({ originalValue, currentValue }) => originalValue !== currentValue
        );
    };

    const renderActionButtons = (request: PendingCookDTO) => {
        if (request.status === 'Pending') {
            return (
                <div className="action-buttons">
                    {isExpanded(request.constraintId) && hasConstraintChanges(request.constraintId) ? (
                        <button
                            className="update-approve-button"
                            onClick={() => handleUpdateAndApprove(request.constraintId)}
                        >
                            עדכן ואשר
                        </button>
                    ) : (
                        <button
                            className="approve-button"
                            onClick={() => handleApprove(request.constraintId)}
                        >
                            אישור
                        </button>
                    )}
                    <button
                        className="reject-button"
                        onClick={() => handleReject(request.constraintId)}
                    >
                        דחייה
                    </button>
                </div>
            );
        }

        return (
            <div className="action-buttons">
                <div className={`status-badge ${request.status.toLowerCase()}`}>
                    {request.status === 'Accepted' ? 'אושר' : 'נדחה'}
                </div>
                <button
                    className="undo-button"
                    onClick={() => handleUndo(request.constraintId)}
                >
                    ביטול
                </button>
            </div>
        );
    };

    return (
        <div className="app-container">
            {selectedDate && renderSidebar()}

            <div className="pending-requests-container">
                <div className="content-wrapper">
                    <div className="date-selection-section">
                        <h1>בקשות בישול</h1>
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

                    {!loading && !error && allRequests.length === 0 && (
                        <div className="no-requests">אין בקשות לתאריך זה</div>
                    )}

                    {allRequests.length > 0 && (
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
                                    {allRequests.map((request) => (
                                        <React.Fragment key={request.constraintId}>
                                            <tr className={request.status.toLowerCase()}>
                                                <td className="name-column">{request.name}</td>
                                                <td className="date-column">
                                                    {new Date(request.date).toLocaleDateString('he-IL')}
                                                </td>
                                                <td className="time-column">
                                                    {request.startTime} - {request.endTime}
                                                </td>
                                                <td className="address-column">{request.address}</td>
                                                <td className="constraints-column">
                                                    <button
                                                        className="view-constraints-button"
                                                        onClick={(e) => toggleRequestDetails(request.constraintId, e)}
                                                    >
                                                        {isExpanded(request.constraintId) ? 'הסתר פרטים' : 'הצג פרטים'}
                                                    </button>
                                                </td>
                                                <td className="actions-column">
                                                    {renderActionButtons(request)}
                                                </td>
                                            </tr>
                                            {isExpanded(request.constraintId) && (
                                                <tr className="constraints-details-row">
                                                    <td colSpan={6}>
                                                        <div className="constraints-details">
                                                            {renderConstraints(request.constraintId, request.constraints)}
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