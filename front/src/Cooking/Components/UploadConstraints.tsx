import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { submitConstraints } from '../RestAPI/CookConstraintsRestAPI';
import '../Styles/DonorCookConsraintsSub.css';
import Logo from '../Images/logo.png';
import background from '../Images/background.png';

interface TimeSlot {
  start: string;
  end: string;
}

interface DaySchedule {
  canVisit: boolean;
  timeSlots: TimeSlot[];
}

interface SubmitData {
  mealCount: string;
  schedule: {
    thursday: DaySchedule;
    friday: DaySchedule;
  };
}

const CookConstraints: React.FC = () => {
  const [mealCount, setMealCount] = useState<string>('');
  const [schedule, setSchedule] = useState<Record<string, DaySchedule>>({
    thursday: { canVisit: false, timeSlots: [] },
    friday: { canVisit: false, timeSlots: [] }
  });

  const setCanVisit = (day: string, value: boolean) => {
    setSchedule(prev => ({
      ...prev,
      [day]: {
        canVisit: value,
        timeSlots: value ? [] : []
      }
    }));
  };

  const addTimeSlot = (day: string) => {
    setSchedule(prev => ({
      ...prev,
      [day]: {
        ...prev[day],
        timeSlots: [...prev[day].timeSlots, { start: '', end: '' }]
      }
    }));
  };

  const updateTimeSlot = (day: string, index: number, field: 'start' | 'end', value: string) => {
    setSchedule(prev => ({
      ...prev,
      [day]: {
        ...prev[day],
        timeSlots: prev[day].timeSlots.map((slot, i) =>
          i === index ? { ...slot, [field]: value } : slot
        )
      }
    }));
  };

  const removeTimeSlot = (day: string, index: number) => {
    setSchedule(prev => ({
      ...prev,
      [day]: {
        ...prev[day],
        timeSlots: prev[day].timeSlots.filter((_, i) => i !== index)
      }
    }));
  };

  const handleSubmit = async () => {
    const sentData ={
        cookId: 1, //need to get this from jwt somehow?
        takingTime: null,
        platesNum: parseInt(mealCount),
        location: "need to get this",
        date: null //CHANGE ALSO
    }

    try {
      console.log(mealCount);
      console.log(schedule);
      await submitConstraints(sentData);
      console.log('Successfully sent data');
      alert('הנתונים נשלחו בהצלחה!');
    } catch (error) {
      console.error('Error:', error);
      alert(error);
    }
  };

  return (
    <div className="meal-planner-container">
      {/* Meal Planner Section */}
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
              defaultValue="1"
            />
          </div>
        </div>

        <div className="days-container">
          {['thursday', 'friday'].map((day) => (
            <div key={day} className="day-wrapper">
              <div className="day-card">
                <h2>{day === 'thursday' ? 'חמישי' : 'שישי'}</h2>
                <div className="visit-buttons">
                  <button
                    className={schedule[day].canVisit === false ? 'active' : ''}
                    onClick={() => setCanVisit(day, false)}
                  >
                    לא ניתן לבקר
                  </button>
                  <button
                    className={schedule[day].canVisit === true ? 'active' : ''}
                    onClick={() => setCanVisit(day, true)}
                  >
                    ניתן לבקר
                  </button>
                </div>

                {schedule[day].canVisit && (
                  <div className="new-time-slot">
                    <div className="time-inputs">
                      <div className="time-input-group">
                        <label>התחלה:</label>
                        <select
                          value=""
                          onChange={(e) => {
                            addTimeSlot(day);
                            updateTimeSlot(day, schedule[day].timeSlots.length, 'start', e.target.value);
                          }}
                        >
                          <option value="">בחר שעה</option>
                          {Array.from({ length: 48 }, (_, i) => {
                            const hour = Math.floor(i / 2);
                            const minute = i % 2 === 0 ? '00' : '30';
                            return (
                              <option key={i} value={`${hour}:${minute}`}>
                                {`${hour}:${minute}`}
                              </option>
                            );
                          })}
                        </select>
                      </div>
                      <div className="time-input-group">
                        <label>סיום:</label>
                        <select
                          value=""
                          onChange={(e) => {
                            if (schedule[day].timeSlots.length > 0) {
                              updateTimeSlot(day, schedule[day].timeSlots.length - 1, 'end', e.target.value);
                            }
                          }}
                        >
                          <option value="">בחר שעה</option>
                          {Array.from({ length: 48 }, (_, i) => {
                            const hour = Math.floor(i / 2);
                            const minute = i % 2 === 0 ? '00' : '30';
                            return (
                              <option key={i} value={`${hour}:${minute}`}>
                                {`${hour}:${minute}`}
                              </option>
                            );
                          })}
                        </select>
                      </div>
                      <button className="add-time" onClick={() => addTimeSlot(day)}>
                        +
                      </button>
                    </div>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>

        <div className="submit-container">
          <button className="submit-button" onClick={() => handleSubmit()}>
            להגיש
          </button>
        </div>
      </div>

      {/* Time Slots Section (Moved Outside) */}
      <div className="time-slots-column">
        <h3>זמנים שנבחרו</h3>
        <div className="time-slots-list">
          {['thursday', 'friday'].map((day) =>
            schedule[day].canVisit &&
            schedule[day].timeSlots.map((slot, index) => (
              <div key={`${day}-${index}`} className="time-slot-item">
                <span>
                  {day === 'thursday' ? 'חמישי' : 'שישי'} {slot.start} - {slot.end}
                </span>
                <button
                  className="remove-time"
                  onClick={() => removeTimeSlot(day, index)}
                >
                  ✕
                </button>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default CookConstraints;