import React, { useState } from "react";
import "../styles/DonorListPopup.css"; // Include styles for the popup
import { Donor } from "../models/Donor";
import dayjs from "dayjs";

// Props for the DonorListPopup component
interface DonorListPopupProps {
  donors: Donor[];
  onClose: () => void; 
  onClick:(donor:Donor)=>void;
}

const DonorListPopup = ({ donors, onClose, onClick}: DonorListPopupProps) => {
  const [selectedStreet, setSelectedStreet] = useState<string>("");
  
  const BEER_SHEVA_STREETS = [
    'העיר העתיקה',
    'נווה עופר',
    'המרכז האזרחי',
    'א\'',
    'ב\'',
    'ג\'',
    'ד\'',
    'ה\'',
    'ו\'',
    'ט\'',
    'י"א',
    'נאות לון',
    'נווה זאב',
    'נווה נוי',
    'נחל בקע',
    'נחל עשן (נווה מנחם)',
    'רמות',
    'נאות אברהם (פלח 6)',
    'נווה אילן (פלח 7)',
    'הכלניות',
    'סיגליות',
    'פארק הנחל'
  ];

  const filteredDonors = selectedStreet 
    ? donors.filter(donor => donor.street === selectedStreet)
    : donors;

  return (
    <div className="popup-overlay">
      <div className="popup-content">
        <h2>רשימת מתנדבים</h2>
        
        <div className="street-filter">
          <select 
            value={selectedStreet} 
            onChange={(e) => setSelectedStreet(e.target.value)}
          >
            <option value="">כל הרחובות</option>
            {BEER_SHEVA_STREETS.map((street) => (
              <option key={street} value={street}>
                {street}
              </option>
            ))}
          </select>
        </div>

        <div className="donor-list">
          {filteredDonors.map((donor) => (
            <div key={donor.id} className="donor-card" onClick={()=>{onClick(donor);onClose()}}>
              <h4>
                {donor.firstName} {donor.lastName}
              </h4>
              <p>
                <strong>טלפון:</strong> {donor.phoneNumber}
              </p>
              <p>
                <strong>כתובת:</strong> {donor.address}
              </p>
            </div>
          ))}
          <button className="close-btn" onClick={onClose} justify-self="center" align-self="center">
            סגור
          </button>
        </div>
      </div>
    </div>
  );
};

export default DonorListPopup;