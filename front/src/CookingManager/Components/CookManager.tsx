import React, { useState, useEffect } from 'react';
import {
    PendingCookDTO,
    getAllRequests,
    approveCookRequest,
    rejectCookRequest,
    getFoodConstraints,
    getAcceptedConstraints,
    updateConstraint,
    undoAction,
    getDonorApproved,
    addNewConstraint
} from '../RestAPI/CookManagerRestAPI';
import { Donor } from "../../DrivingManager/models/Donor";
import DiveHeader from '../../GoPage/DiveHeader';
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
    
    const getUpcomingFriday = () => {
        const today = new Date();
        const dayOfWeek = today.getDay(); // Sunday = 0, Monday = 1, ..., Saturday = 6
        const daysUntilFriday = (5 - dayOfWeek + 7) % 7; // Calculate days until next Friday
        const nextFriday = new Date(today);
        nextFriday.setDate(today.getDate() + daysUntilFriday);
        return nextFriday.toISOString().split('T')[0]; // Format as YYYY-MM-DD
    };

    const [selectedDate, setSelectedDate] = useState<string>(getUpcomingFriday());
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>('');
    const [expandedConstraintIds, setExpandedConstraintIds] = useState<Set<number>>(new Set());
    const [summaryData, setSummaryData] = useState<SummaryData>({ needed: {}, accepted: {} });
    const [editableConstraints, setEditableConstraints] = useState<Record<number, EditableConstraints>>({});
    const [isAddingConstraint, setIsAddingConstraint] = useState(false);
    const [donors, setDonors] = useState<Donor[]>([]);
    const [selectedDonor, setSelectedDonor] = useState<number | null>(null);
    const [newStreet, setNewStreet] = useState<string>('');
    const [newStartTime, setNewStartTime] = useState('');
    const [newEndTime, setNewEndTime] = useState('');
    const [newConstraints, setNewConstraints] = useState<Record<string, number>>({});
    const [whatsappNumber, setWhatsappNumber] = useState<string>('');
    

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
            fetchAllData(selectedDate);
            const intervalId = setInterval(() => {
                fetchAllData(selectedDate);
            }, 60000);

            return () => clearInterval(intervalId);
        }
    }, [selectedDate]);

    useEffect(() => {
        const fetchDonors = async () => {
            try {
                const approvedDonors = await getDonorApproved();
                setDonors(approvedDonors);
            } catch (err) {
                console.error('Error fetching donors:', err);
            }
        };

        if (isAddingConstraint) {
            fetchDonors();
        }
    }, [isAddingConstraint]);

    const handleDateChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const newDate = event.target.value;
        setSelectedDate(newDate);
        if (newDate) {
            fetchAllData(newDate);
        }
    };

    const handleDonorChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const donorId = Number(e.target.value);
    setSelectedDonor(donorId);
    
    if (donorId) {
        const selectedDonorData = donors.find(donor => donor.id === donorId);
        if (selectedDonorData && selectedDonorData.street) {
            setNewStreet(selectedDonorData.street);
        } else {
            setNewStreet('');
        }
    } else {
        setNewStreet('');
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
            if (selectedDate) {
                await fetchAllData(selectedDate);
            }
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
            if (selectedDate) {
                await fetchAllData(selectedDate);
            }
            alert('הבקשה נדחתה בהצלחה');
        } catch (err) {
            alert('שגיאה בדחיית הבקשה');
            console.error(err);
        }
    };

    const handleUndo = async (constraintId: number) => {
        try {
            await undoAction(constraintId);
            setAllRequests(prev => prev.map(request =>
                request.constraintId === constraintId
                    ? { ...request, status: 'Pending' }
                    : request
            ));
            if (selectedDate) {
                await fetchAllData(selectedDate);
            }
         
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
            if (selectedDate) {
                await fetchAllData(selectedDate);
            }
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

                <div className="sidebar-actions mt-4">
                    <div className="whatsapp-section">
                        <input
                            type="text"
                            placeholder="מספר וואטסאפ (כולל קידומת)"
                            value={whatsappNumber}
                            onChange={(e) => setWhatsappNumber(e.target.value)}
                            className="whatsapp-input p-2 border rounded mb-2 w-full text-right"
                        />
                        <button
                            onClick={handleSendToWhatsApp}
                            disabled={!whatsappNumber || !selectedDate || allRequests.filter(r => r.status === 'Accepted').length === 0}
                            className="send-whatsapp-button bg-green-500 text-white p-2 rounded w-full hover:bg-green-600 disabled:bg-gray-400"
                        >
                            שלח מה אנשים מכינים
                        </button>
                    </div>
                </div>
            </div>
        );
    };

    const handleSendToWhatsApp = () => {
        const acceptedRequests = allRequests.filter(request => request.status === 'Accepted');
        if (acceptedRequests.length === 0) {
            alert('אין בקשות מאושרות להצגה');
            return;
        }

        const formattedDate = new Date(selectedDate).toLocaleDateString('he-IL');
        
        let message = `סיכום בישולים לתאריך ${formattedDate}:\n\n`;
        
        acceptedRequests.forEach(request => {
            message += `${request.name} (${request.startTime}-${request.endTime}):\n`;
            Object.entries(request.constraints).forEach(([food, amount]) => {
                if (amount > 0) {
                    message += `- ${food}: ${amount}\n`;
                }
            });
            message += `\n`;
        });
        
        // Add summary
        message += `סיכום כולל:\n`;
        const foodSummary: Record<string, number> = {};
        
        acceptedRequests.forEach(request => {
            Object.entries(request.constraints).forEach(([food, amount]) => {
                if (amount > 0) {
                    foodSummary[food] = (foodSummary[food] || 0) + amount;
                }
            });
        });
        
        Object.entries(foodSummary).forEach(([food, amount]) => {
            message += `- ${food}: ${amount}\n`;
        });
        
        // Encode the message for URL
        const encodedMessage = encodeURIComponent(message);
        
        // Open WhatsApp with the predefined message
        const whatsappUrl = `https://wa.me/${whatsappNumber}?text=${encodedMessage}`;
        window.open(whatsappUrl, '_blank');
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

    const handleAddNewConstraint = async () => {
        if (!selectedDonor || !newStartTime || !newEndTime) {
            alert('נא למלא את כל השדות');
            return;
        }

        try {
            await addNewConstraint(
                selectedDonor,
                selectedDate,
                newStartTime,
                newEndTime,
                newConstraints,
                newStreet

                
            );

            setIsAddingConstraint(false);
            setSelectedDonor(null);
            setNewConstraints({});
            setNewStartTime('');
            setNewEndTime('');
            fetchAllData(selectedDate);
        } catch (err) {
            alert('שגיאה בהוספת הבקשה');
            console.error(err);
        }
    };

    function getTimeDifferenceInHours(start: string, end: string): number {
        // Parse the time strings into hours and minutes
        const [startHour, startMinute] = start.split(':').map(Number);
        const [endHour, endMinute] = end.split(':').map(Number);
    
        // Convert the time to minutes since midnight
        const startTotalMinutes = startHour * 60 + startMinute;
        const endTotalMinutes = endHour * 60 + endMinute;
    
        const differenceInMinutes = endTotalMinutes - startTotalMinutes;
        return differenceInMinutes / 60;
    }

    return (
        <>
            <DiveHeader />
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
                                <button
                                    onClick={() => setIsAddingConstraint(true)}
                                    className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 mr-4"
                                >
                                    הוסף בקשת בישול
                                </button>
                            </div>

                            {isAddingConstraint && (
                                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                                    <div className="bg-white p-6 rounded-lg w-96 max-h-[90vh] overflow-y-auto">
                                        <h2 className="text-xl font-bold mb-4 text-right">הוספת בקשת בישול חדשה</h2>

                                        <div className="space-y-4">
                                            <div>
                                                <label className="block text-right mb-2">בחר מבשל:</label>
                                               <select
                                                className="w-full p-2 border rounded text-right"
                                                value={selectedDonor || ''}
                                                onChange={handleDonorChange}
                                            >
                                                <option value="">בחר מבשל</option>
                                                {donors.map(donor => (
                                                    <option key={donor.id} value={donor.id}>
                                                        {donor.firstName} {donor.lastName}
                                                    </option>
                                                ))}
                                            </select>
                                            </div>

                                            <div className="grid grid-cols-2 gap-4">
                                                <div>
                                                    <label className="block text-right mb-2">שעת סיום:</label>
                                                    <input
                                                        type="time"
                                                        className="w-full p-2 border rounded text-right"
                                                        value={newEndTime}
                                                        onChange={(e) => setNewEndTime(e.target.value)}
                                                    />
                                                </div>
                                                <div>
                                                    <label className="block text-right mb-2">שעת התחלה:</label>
                                                    <input
                                                        type="time"
                                                        className="w-full p-2 border rounded text-right"
                                                        value={newStartTime}
                                                        onChange={(e) => setNewStartTime(e.target.value)}
                                                    />
                                                </div>
                                            </div>

                                            <div>
                                                <label className="block text-right mb-2">הגדר מנות:</label>
                                                {Object.keys(summaryData.needed).map(constraint => (
                                                    <div key={constraint} className="flex justify-between items-center mb-2 p-2 bg-gray-50 rounded">
                                                        <div className="flex items-center gap-2">
                                                            <button
                                                                onClick={() => {
                                                                    const currentValue = newConstraints[constraint] || 0;
                                                                    setNewConstraints({
                                                                        ...newConstraints,
                                                                        [constraint]: Math.max(0, currentValue - 1)
                                                                    });
                                                                }}
                                                                className="w-8 h-8 bg-gray-200 rounded"
                                                            >
                                                                -
                                                            </button>
                                                            <span>{newConstraints[constraint] || 0}</span>
                                                            <button
                                                                onClick={() => {
                                                                    const currentValue = newConstraints[constraint] || 0;
                                                                    setNewConstraints({
                                                                        ...newConstraints,
                                                                        [constraint]: currentValue + 1
                                                                    });
                                                                }}
                                                                className="w-8 h-8 bg-gray-200 rounded"
                                                            >
                                                                +
                                                            </button>
                                                        </div>
                                                        <span>{constraint}</span>
                                                    </div>
                                                ))}
                                            </div>

                                            <div className="flex justify-end gap-2 mt-4">
                                                <button
                                                    disabled={!selectedDonor || !newStartTime || !newEndTime || !newConstraints || getTimeDifferenceInHours(newStartTime, newEndTime) < 0}
                                                    onClick={handleAddNewConstraint}
                                                    className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
                                                >
                                                    שמור
                                                </button>
                                                <button
                                                    onClick={() => {
                                                        setIsAddingConstraint(false);
                                                        setSelectedDonor(null);
                                                        setNewConstraints({});
                                                        setNewStartTime('');
                                                        setNewEndTime('');
                                                    }}
                                                    className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600"
                                                >
                                                    ביטול
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            )}
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
        </>
    );
};

export default PendingRequests;