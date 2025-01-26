import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { submitDriverConstraints, DriverConstraintsData } from '../models/DriverConstraintsRestAPI';
import DiveHeader from '../../GoPage/DiveHeader';
import '../styles/constraints.css'

interface TimeSlot {
  start: string;  // Changed to number
  end: string;    // Changed to number
}

interface DaySchedule {
  canDrive: boolean;
  timeSlots: TimeSlot[];
  selectedDate: string | null;
}

const DriverConstraints: React.FC = () => {
  const navigate = useNavigate();
  const [schedule, setSchedule] = useState<DaySchedule>({
    canDrive: false,
    timeSlots: [],
    selectedDate: null
  });
  const [startLocation, setStartLocation] = useState<string>('');
  const [description, setDescription] = useState<string>('');
  const [tempTimeSlot, setTempTimeSlot] = useState<{start: string, end: string}>({
    start: '',
    end: ''
  });

  // Convert HH:MM format to hours in decimal
  const timeToDecimal = (time: string): number => {
    const [hours, minutes] = time.split(':').map(Number);
    return hours + (minutes / 60);
  };

  // Convert decimal hours to HH:MM format for display
  const decimalToTime = (decimal: number): string => {
    const hours = Math.floor(decimal);
    const minutes = Math.round((decimal - hours) * 60);
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`;
  };

  const getNextFridays = () => {
    const fridays = [];
    let currentDate = new Date();

    while (fridays.length < 4) {
      currentDate.setDate(currentDate.getDate() + 1);
      if (currentDate.getDay() === 5) {
        fridays.push(new Date(currentDate));
      }
    }
    return fridays;
  };

  const handleDateSelect = (date: string) => {
    setSchedule(prev => ({
      ...prev,
      selectedDate: date,
      canDrive: true,
      timeSlots: []
    }));
    setTempTimeSlot({ start: '', end: '' });
  };

  const addTimeSlot = () => {
    if (tempTimeSlot.start && tempTimeSlot.end) {
      const startDecimal = tempTimeSlot.start;
      const endDecimal = tempTimeSlot.end;

      if (endDecimal <= startDecimal) {
        alert('שעת הסיום חייבת להיות מאוחרת משעת ההתחלה');
        return;
      }

      setSchedule(prev => ({
        ...prev,
        timeSlots: [...prev.timeSlots, {
          start: startDecimal,
          end: endDecimal
        }]
      }));
      setTempTimeSlot({ start: '', end: '' });
    }
  };

  const removeTimeSlot = (index: number) => {
    setSchedule(prev => ({
      ...prev,
      timeSlots: prev.timeSlots.filter((_, i) => i !== index)
    }));
  };

  const handleSubmit = async () => {
    if (!schedule.selectedDate || schedule.timeSlots.length === 0) {
      return;
    }

    const driverData: DriverConstraintsData = {
      driverId: 1,
      date: schedule.selectedDate,
      startHour: schedule.timeSlots[0].start,
      endHour: schedule.timeSlots[0].end,
      startLocation: startLocation,
      requests: description
    };

    try {
      await submitDriverConstraints(driverData);
      alert('הנתונים נשלחו בהצלחה!');
    } catch (error) {
      console.error('Error:', error);
      alert('הייתה תקלה בעת שליחת הנתונים. אנא נסו שוב במועד מאוחר יותר');
    }
  };

  const isAddButtonDisabled = !tempTimeSlot.start || !tempTimeSlot.end;

  return (
  <>
  <DiveHeader />
    <div className="driver-planner-container">
      {/* Navigation buttons section */}
      <div className="driver-navigation-buttons">
        <button
          className="driver-nav-button"
          onClick={() => navigate('/cookConstraints')}
        >
          טבחים
        </button>
        <button
          className="driver-nav-button active"
          onClick={() => {/* Already on this page */}}
        >
          נהגים
        </button>
      </div>

      <div className="driver-content-wrapper">
        <div className="driver-planner">
          <div className="driver-header-container">
            <h1>זמינות לנהיגה</h1>
          </div>

          {/* Date selection */}
          <div className="driver-day-selection">
            <select
              value={schedule.selectedDate || ''}
              onChange={(e) => handleDateSelect(e.target.value)}
              className="driver-date-select"
            >
              <option value="">בחר תאריך</option>
              {getNextFridays().map((friday) => (
                <option
                  key={friday.toISOString()}
                  value={friday.toISOString().split('T')[0]}
                >
                  {friday.toLocaleDateString('he-IL', {
                    weekday: 'long',
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric'
                  })}
                </option>
              ))}
            </select>
          </div>

          {/* Location input */}
          <div className="driver-location-input">
            <label>נקודת התחלה</label>
            <input
              type="text"
              value={startLocation}
              onChange={(e) => setStartLocation(e.target.value)}
              placeholder="הכנס את כתובת נקודת ההתחלה"
            />
          </div>

          {/* Description input */}
          <div className="driver-description-input">
            <label>הערות נוספות</label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="הוסף הערות או מידע נוסף"
            />
          </div>

          {/* Time slots section */}
          {schedule.selectedDate && (
            <div className="driver-time-slots-section">
              <div className="driver-new-time-slot">
                <div className="driver-time-inputs">
                  <div className="driver-time-input-group">
                    <label>התחלה:</label>
                    <select
                      value={tempTimeSlot.start}
                      onChange={(e) => setTempTimeSlot(prev => ({ ...prev, start: e.target.value }))}
                    >
                      <option value="">בחר שעה</option>
                      {Array.from({ length: 48 }, (_, i) => {
                        const hour = Math.floor(i / 2);
                        const minute = i % 2 === 0 ? '00' : '30';
                        return (
                          <option key={i} value={`${hour.toString().padStart(2, '0')}:${minute}`}>
                            {`${hour.toString().padStart(2, '0')}:${minute}`}
                          </option>
                        );
                      })}
                    </select>
                  </div>
                  <div className="driver-time-input-group">
                    <label>סיום:</label>
                    <select
                      value={tempTimeSlot.end}
                      onChange={(e) => setTempTimeSlot(prev => ({ ...prev, end: e.target.value }))}
                    >
                      <option value="">בחר שעה</option>
                      {Array.from({ length: 48 }, (_, i) => {
                        const hour = Math.floor(i / 2);
                        const minute = i % 2 === 0 ? '00' : '30';
                        return (
                          <option key={i} value={`${hour.toString().padStart(2, '0')}:${minute}`}>
                            {`${hour.toString().padStart(2, '0')}:${minute}`}
                          </option>
                        );
                      })}
                    </select>
                  </div>
                  <button
                    className={`driver-add-time ${isAddButtonDisabled ? 'disabled' : ''}`}
                    onClick={addTimeSlot}
                    disabled={isAddButtonDisabled}
                  >
                    +
                  </button>
                </div>
              </div>
            </div>
          )}

          {/* Submit button */}
          <div className="driver-submit-container">
            <button
              className="driver-submit_button"
              onClick={handleSubmit}
              disabled={!schedule.selectedDate || schedule.timeSlots.length === 0 || !startLocation}
            >
              להגיש
            </button>
          </div>
        </div>

        {/* Selected time slots display */}
        <div className="driver-time-slots-column">
          <h3>זמנים שנבחרו</h3>
          <div className="driver-time-slots-list">
            {schedule.timeSlots.map((slot, index) => (
              <div key={index} className="driver-time-slot-item">
                <span>
                  {slot.start} - {slot.end}
                </span>
                <button
                  className="driver-remove-time"
                  onClick={() => removeTimeSlot(index)}
                >
                  ✕
                </button>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
    </>
  );
};

export default DriverConstraints;