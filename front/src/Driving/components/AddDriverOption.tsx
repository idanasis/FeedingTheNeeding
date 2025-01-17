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

const DonorListPopup = ({ donors, onClose,onClick}: DonorListPopupProps) => {

  return (
    <div className="popup-overlay">
      <div className="popup-content">
        <h2>רשימת מתנדבים</h2>

        <div className="donor-list">
          {donors.map((donor) => (
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