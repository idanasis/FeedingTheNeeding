import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { submitConstraints } from '../RestAPI/CookConstraintsRestAPI';
import '../Styles/DonorCookConsraintsSub.css'

interface TimeSlot {
  start: string;
  end: string;
}

interface DaySchedule {
  canVisit: boolean;
  timeSlots: TimeSlot[];
  selectedDate: string | null;
}

const CookConstraints: React.FC = () => {
  const [mealCount, setMealCount] = useState<string>('');
  const [schedule, setSchedule] = useState<DaySchedule>({
    canVisit: false,
    timeSlots: [],
    selectedDate: null
  });
  const [tempTimeSlot, setTempTimeSlot] = useState<TimeSlot>({
    start: '',
    end: ''
  });

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
      canVisit: true,
      timeSlots: []
    }));
    setTempTimeSlot({ start: '', end: '' });
  };

  const addTimeSlot = () => {
    if (tempTimeSlot.start && tempTimeSlot.end) {
      // Validate that end time is after start time
      const [startHour, startMinute] = tempTimeSlot.start.split(':').map(Number);
      const [endHour, endMinute] = tempTimeSlot.end.split(':').map(Number);
      const startTime = startHour * 60 + startMinute;
      const endTime = endHour * 60 + endMinute;

      if (endTime <= startTime) {
        alert('שעת הסיום חייבת להיות מאוחרת משעת ההתחלה');
        return;
      }

      setSchedule(prev => ({
        ...prev,
        timeSlots: [...prev.timeSlots, tempTimeSlot]
      }));
      setTempTimeSlot({ start: '', end: '' });
    }
  };

  const updateTempTimeSlot = (field: 'start' | 'end', value: string) => {
    setTempTimeSlot(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const removeTimeSlot = (index: number) => {
    setSchedule(prev => ({
      ...prev,
      timeSlots: prev.timeSlots.filter((_, i) => i !== index)
    }));
  };

  const handleSubmit = async () => {

    const sentData = {
      cookId: 1,
      startTime: schedule.timeSlots[0].start,
      endTime: schedule.timeSlots[0].end,
      platesNum: parseInt(mealCount),
      location: "need to get this",
      date: schedule.selectedDate
    };

    try {
      await submitConstraints(sentData);
      alert('הנתונים נשלחו בהצלחה!');
    } catch (error) {
      console.error('Error:', error);
      alert('הייתה תקלה בעת שליחת הנתונים. אנא נסו שוב במועד מאוחר יותר');
    }
  };

  const isAddButtonDisabled = !tempTimeSlot.start || !tempTimeSlot.end;

  return (
    <div className="meal-planner-container">
      <div className="content-wrapper">
        <div className="meal-planner">
          <div className="header-container">
            <h1>כמה מנות אבשל</h1>
            <div className="meals-input">
              <input
                type="number"
                value={mealCount}
                onChange={(e) => {
                  const value = parseInt(e.target.value);
                  if (value >= 1) {
                    setMealCount(e.target.value);
                  }
                }}
                min="1"
              />
            </div>
          </div>

          <div className="day-selection">
            <select
              value={schedule.selectedDate || ''}
              onChange={(e) => handleDateSelect(e.target.value)}
              className="date-select"
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

          {schedule.selectedDate && (
            <div className="time-slots-section">
              <div className="new-time-slot">
                <div className="time-inputs">
                  <div className="time-input-group">
                    <label>התחלה:</label>
                    <select
                      value={tempTimeSlot.start}
                      onChange={(e) => updateTempTimeSlot('start', e.target.value)}
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
                  <div className="time-input-group">
                    <label>סיום:</label>
                    <select
                      value={tempTimeSlot.end}
                      onChange={(e) => updateTempTimeSlot('end', e.target.value)}
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
                    className={`add-time ${isAddButtonDisabled ? 'disabled' : ''}`}
                    onClick={addTimeSlot}
                    disabled={isAddButtonDisabled}
                  >
                    +
                  </button>
                </div>
              </div>
            </div>
          )}

          <div className="submit-container">
            <button
              className="submit-button"
              onClick={handleSubmit}
              disabled={!schedule.selectedDate || schedule.timeSlots.length === 0 || !mealCount}
            >
              להגיש
            </button>
          </div>
        </div>

        <div className="time-slots-column">
          <h3>זמנים שנבחרו</h3>
          <div className="time-slots-list">
            {schedule.timeSlots.map((slot, index) => (
              <div key={index} className="time-slot-item">
                <span>
                  {slot.start} - {slot.end}
                </span>
                <button
                  className="remove-time"
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
  );
};

export default CookConstraints;