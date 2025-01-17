import React, { useEffect, useState } from "react";
import "../styles/DonorTable.css";
import dayjs from "dayjs";
import { Donor } from "../../models/DonorModel";
import { deleteDonor, getDonorApproved, updateDonor } from "../../Restapi/DonorRestapi";
import FeedingLogo from '../../Authentication/Images/logo.png';

const DonorTable = () => {
  const [editableDonors, setEditableDonors] = useState<Donor[]>([]);
  const fetchDonors = async () => {
    try{
    const donors = await getDonorApproved();
    console.log("Donors:", donors);
    setEditableDonors(donors);
    }catch(e){
      alert("שגיאה בטעינת רשימת המתנדבים");
      console.log(e);
    }
  };
  useEffect(() => {
    
    fetchDonors();
  }, []);

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>,
    id: number,
    field: keyof Donor
  ) => {
    const updatedDonors = editableDonors.map((donor) =>
      donor.id === id ? { ...donor, [field]: e.target.value } : donor
    );
    setEditableDonors(updatedDonors);
  };

  const handleSave = async (donor: Donor) => {
    try{
    await updateDonor(donor);
    console.log("Updated donor:", donor);
    fetchDonors();
    }catch(e){
      alert("שגיאה בעדכון פרטי המתנדב");
      console.log(e);
    }
  };
  const handleDelete=async(donor:Donor)=>{
    try{
      await deleteDonor(donor);
      fetchDonors();
    }
    catch(e){
      alert("שגיאה במחיקת המתנדב");
      console.log(e);
    }
  }

  return (
    <div className="donor-table-container">
      <img
        src={FeedingLogo}
        alt="Logo"
        style={{
          position: "absolute",
          top: "10px",
          left: "5px",
          height: "60px",
        }}
      />
      <table className="donor-table">
  <thead>
    <tr>
      <th className="first-name-column">שם פרטי</th>
      <th className="last-name-column">שם משפחה</th>
      <th className="phone-number-column">מספר טלפון</th>
      <th className="address-column">כתובת</th>
      <th className="email-column">אימייל</th>
      <th className="last-donation-date-column">תאריך תרומה אחרון</th>
      <th className="role-column">תפקיד</th>
      <th className="action-column"></th>
    </tr>
  </thead>
  <tbody>
    {editableDonors.map((donor) => (
      <tr key={donor.id}>
        <td className="first-name-column">
          <input
            type="text"
            value={donor.firstName}
            onChange={(e) => handleInputChange(e, donor.id, "firstName")}
          />
        </td>
        <td className="last-name-column">
          <input
            type="text"
            value={donor.lastName}
            onChange={(e) => handleInputChange(e, donor.id, "lastName")}
          />
        </td>
        <td className="phone-number-column">
          <input
            type="text"
            value={donor.phoneNumber}
            onChange={(e) => handleInputChange(e, donor.id, "phoneNumber")}
          />
        </td>
        <td className="address-column">
          <input
            type="text"
            value={donor.address}
            onChange={(e) => handleInputChange(e, donor.id, "address")}
          />
        </td>
        <td className="email-column">
          <input
            type="email"
            value={donor.email || ""}
            onChange={(e) => handleInputChange(e, donor.id, "email")}
          />
        </td>
        <td className="last-donation-date-column">
          <input
            type="text"
            disabled={true}
            value={donor.lastDonationDate ? dayjs(donor.lastDonationDate).format("DD/MM/YYYY").toString() : "לא התנדב החודש"}
            onChange={(e) => handleInputChange(e, donor.id, "lastDonationDate")}
          />
        </td>
        <td className="role-column">
          <select
            value={donor.role==="ADMIN" ? "ADMIN" : donor.role==="STAFF" ? "STAFF" : "DONOR"}
            onChange={(e) => handleInputChange(e, donor.id, "role")}
          >
            <option value="ADMIN">מנכל</option>
            <option value="STAFF">רכז</option>
            <option value="DONOR">מתנדב</option>
          </select>
        </td>
        <td className="action-column">
        <div className="button-group">
          <button className="save-button" onClick={() => handleSave(donor)}>שמור</button>
          <button className="delete-button" onClick={() => handleDelete(donor)}>מחק</button>
        </div>
      </td>


      </tr>
    ))}
  </tbody>
</table>

    </div>
  );
};

export default DonorTable;
