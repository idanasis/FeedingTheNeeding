import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { submitConstraints, getFoodConstraints } from '../RestAPI/CookConstraintsRestAPI';
import '../Styles/DonorCookConsraintsSub.css';

interface TimeSlot {
  start: string;
  end: string;
}

interface DaySchedule {
  canVisit: boolean;
  timeSlots: TimeSlot[];
  selectedDate: string | null;
}

interface FoodSelection {
  [key: string]: number;
}

const CookConstraints: React.FC = () => {
  const navigate = useNavigate();
  const [schedule, setSchedule] = useState<DaySchedule>({
    canVisit: false,
    timeSlots: [],
    selectedDate: null
  });
  const [tempTimeSlot, setTempTimeSlot] = useState<TimeSlot>({
    start: '',
    end: ''
  });
  const [neededFood, setNeededFood] = useState<Record<string, number>>({});
  const [selectedFood, setSelectedFood] = useState<FoodSelection>({});

  useEffect(() => {
    if (schedule.selectedDate) {
      fetchNeededFood(schedule.selectedDate);
    }
  }, [schedule.selectedDate]);

  const fetchNeededFood = async (date: string) => {
    try {
      const foodData = await getFoodConstraints(date);
      setNeededFood(foodData);
      setSelectedFood({});
    } catch (error) {
      console.error('Error fetching food constraints:', error);
      alert('שגיאה בטעינת נתוני המזון הנדרש');
    }
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
      canVisit: true,
      timeSlots: []
    }));
    setTempTimeSlot({ start: '', end: '' });
  };

  const handleFoodQuantityChange = (foodType: string, quantity: number) => {
    if (quantity >= 0 && quantity <= (neededFood[foodType] || 0)) {
      setSelectedFood(prev => ({
        ...prev,
        [foodType]: quantity
      }));
    }
  };

  const addTimeSlot = () => {
    if (tempTimeSlot.start && tempTimeSlot.end) {
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

  const getTotalSelectedMeals = () => {
    return Object.values(selectedFood).reduce((sum, quantity) => sum + quantity, 0);
  };

  const handleSubmit = async () => {
    const sentData = {
      cookId: 1,
      startTime: schedule.timeSlots[0].start,
      endTime: schedule.timeSlots[0].end,
      constraints: selectedFood,
      location: "default_location",
      date: schedule.selectedDate!
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
      <div className="navigation-buttons">
        <button className="nav-button active">
          טבחים
        </button>
        <button
          className="nav-button"
          onClick={() => navigate('/driversConstraints')}
        >
          נהגים
        </button>
      </div>

      <div className="content_wrapper">
        <div className="meal-planner">
          <div className="header-container">
            <h1>בחירת תפריט ומועד הגעה</h1>
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
            <>
              <div className="food-selection-container">
                {Object.entries(neededFood).map(([foodType, neededQuantity]) => (
                  <div key={foodType} className="food-item">
                    <span className="food-type">{foodType}</span>
                    <span className="food-quantity">נדרש: {neededQuantity}</span>
                    <div className="quantity-input">
                      <input
                        type="number"
                        value={selectedFood[foodType] || 0}
                        onChange={(e) => handleFoodQuantityChange(foodType, parseInt(e.target.value))}
                        min="0"
                        max={neededQuantity}
                      />
                    </div>
                  </div>
                ))}
              </div>

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
            </>
          )}

          <div className="submit-container">
            <button
              className="submit_button"
              onClick={handleSubmit}
              disabled={!schedule.selectedDate ||
                       schedule.timeSlots.length === 0 ||
                       getTotalSelectedMeals() === 0}
            >
              להגיש
            </button>
          </div>
        </div>

        <div className="time-slots-column">
          <h3>סיכום בחירות</h3>
          <div className="selected-foods">
            <h4>מנות שנבחרו:</h4>
            {Object.entries(selectedFood)
              .filter(([_, quantity]) => quantity > 0)
              .map(([foodType, quantity]) => (
                <div key={foodType} className="selected-food-item">
                  <span>{foodType}</span>
                  <span>{quantity}</span>
                </div>
              ))}
            <div className="total-meals">
              <strong>סה"כ מנות: {getTotalSelectedMeals()}</strong>
            </div>
          </div>
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