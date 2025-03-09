import React, { useState } from "react";
import "../styles/DonorListPopup.css"; // Include styles for the popup
import { Donor } from "../models/Donor";
import dayjs from "dayjs";
import { Direction } from "@dnd-kit/core/dist/types";

// Props for the DonorListPopup component
interface DonorListPopupProps {
  donors: Donor[];
  onClose: () => void;
  onClick: (donor: Donor) => void;
}

const DonorListPopup = ({ donors, onClose, onClick }: DonorListPopupProps) => {
  const [selectedStreet, setSelectedStreet] = useState("");
  const [searchQuery, setSearchQuery] = useState("");
  
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

  const filteredDonors = donors
    .filter(donor => selectedStreet ? donor.street === selectedStreet : true)
    .filter(donor => {
      if (!searchQuery) return true;
      
      const searchLower = searchQuery.toLowerCase();
      const fullName = `${donor.firstName} ${donor.lastName}`.toLowerCase();
      const address = `${donor.street} ${donor.address}`.toLowerCase();
      
      return (
        fullName.includes(searchLower) ||
        donor.phoneNumber.includes(searchQuery) ||
        address.includes(searchLower)
      );
    });

  return (
    <div className="popup-overlay">
      <div className="popup-content">
        <h1>רשימת מתנדבים</h1>
        
        <div style={{ marginBottom: "15px", display: "flex", gap: "10px" ,flexDirection: "column"}}>
            <button className="close-btn" onClick={onClose} style={{ width: "20%" }}>סגור</button>

          <input
            type="text"
            placeholder="חיפוש לפי שם, טלפון או כתובת..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={{ 
              padding: "8px", 
              borderRadius: "4px", 
              border: "1px solid #ddd",
              flexGrow: 1
            }}
          />
          
          <select
            value={selectedStreet}
            onChange={(e) => setSelectedStreet(e.target.value)}
            style={{ 
              padding: "8px", 
              borderRadius: "4px", 
              border: "1px solid #ddd" 
            }}
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
            <div
              key={donor.phoneNumber}
              className="donor-card"
              onClick={() => {
                onClick(donor);
                onClose();
              }}
            >
              <h4>{donor.firstName} {donor.lastName}</h4>
              <p><strong>טלפון:</strong> {donor.phoneNumber}</p>
              <p><strong>כתובת:</strong> שכונה: {donor.street}, רחוב: {donor.address}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default DonorListPopup;