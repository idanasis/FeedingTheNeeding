import React, { useEffect, useState } from "react";
import "../Styles/DonorTable.css";
import dayjs from "dayjs";
import { Donor } from "../../commons/models/DonorModel";
import { deleteDonor, getDonorApproved, updateDonor } from "../../commons/Restapi/DonorRestapi";
import FeedingLogo from '../../Home/Images/FeedingLogo.png';
import DiveHeader from "../../GoPage/DiveHeader";
import {registerDonor} from '../../DonorRegister/RestAPI/donorRegRestAPI';
import {getPendingDonors} from '../../commons/Restapi/DonorRestapi';

// Beer Sheva streets array
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

const DonorTable = () => {
  const [editableDonors, setEditableDonors] = useState<Donor[]>([]);
  const [showAddForm, setShowAddForm] = useState(false);
  const [newDonor, setNewDonor] = useState({
    email: "selfAddedUser@gmail.com",
    password: "NewUser1234",
    confirmPassword: "NewUser1234",
    firstName: "",
    lastName: "",
    phoneNumber: "",
    street: "",
    address: ""
  });

  const fetchDonors = async () => {
    try {
      const donors = await getDonorApproved();
      console.log("Donors:", donors);
      setEditableDonors(donors);
    } catch (e) {
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
    try {
      await updateDonor(donor);
      console.log("Updated donor:", donor);
      fetchDonors();
    } catch (e) {
      alert("שגיאה בעדכון פרטי המתנדב");
      console.log(e);
    }
  };

  const handleDelete = async (donor: Donor) => {
    try {
      await deleteDonor(donor);
      fetchDonors();
    } catch (e) {
      alert("שגיאה במחיקת המתנדב");
      console.log(e);
    }
  };

  const handleAddDonorChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setNewDonor((prev) => ({ ...prev, [name]: value }));
  };

  const handleAddDonorSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      // Register the donor first
      const registeredDonor = await registerDonor(newDonor);
      console.log("Donor registered:", registeredDonor);
      
      // Assuming the donor is now in a pending state
      // Now fetch the newly added donor and set status to AVAILABLE
      const pendingDonors = await getPendingDonors(); // You may need to adjust this endpoint
      const addedDonor = pendingDonors.find(
        (donor: Donor) => donor.phoneNumber === newDonor.phoneNumber
      );
      
      if (addedDonor) {
        // Update the donor status to AVAILABLE
        addedDonor.status = "AVAILABLE";
        await updateDonor(addedDonor);
        console.log("Donor approved:", addedDonor);
      }
      
      // Reset form
      setNewDonor({
        email: "",
        password: "",
        confirmPassword: "",
        firstName: "",
        lastName: "",
        phoneNumber: "",
        street: "",
        address: ""
        
      });
      setShowAddForm(false);
      // Refresh donor list
      fetchDonors();
      alert("המתנדב נוסף בהצלחה");
    } catch (e) {
      if ((e as any).response && (e as any).response.status === 409) {
        alert("מתנדב עם מספר זה כבר קיים במערכת");
      } else {
        alert("שגיאה בהוספת המתנדב");
      }
      console.log(e);
    }
  };

  return (
    <>
      <DiveHeader />
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
        
        <div className="table-actions">
          <button 
            className="add-donor-button" 
            onClick={() => setShowAddForm(!showAddForm)}
          >
            {showAddForm ? "בטל הוספה" : "הוסף מתנדב חדש"}
          </button>
        </div>
        
        {showAddForm && (
          <div className="add-donor-form-container">
            <h2>הוספת מתנדב חדש</h2>
            <form onSubmit={handleAddDonorSubmit} className="add-donor-form">
              <div className="form-col">
                <div className="form-group">
                  <label htmlFor="firstName">שם פרטי</label>
                  <input
                    type="text"
                    id="firstName"
                    name="firstName"
                    value={newDonor.firstName}
                    onChange={handleAddDonorChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="lastName">שם משפחה</label>
                  <input
                    type="text"
                    id="lastName"
                    name="lastName"
                    value={newDonor.lastName}
                    onChange={handleAddDonorChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="phoneNumber">מספר טלפון</label>
                  <input
                    type="text"
                    id="phoneNumber"
                    name="phoneNumber"
                    value={newDonor.phoneNumber}
                    onChange={handleAddDonorChange}
                    required
                  />
                </div>
                
                {/* Street selection dropdown */}
                <div className="form-group">
                  <label htmlFor="street">שכונה</label>
                  <select
                    id="street"
                    name="street"
                    value={newDonor.street}
                    onChange={handleAddDonorChange}
                    required
                    className="street-select"
                  >
                    <option value="">בחר שכונה</option>
                    {BEER_SHEVA_STREETS.map((street) => (
                      <option key={street} value={street}>
                        {street}
                      </option>
                    ))}
                  </select>
                </div>
                
                <div className="form-group full-width">
                  <label htmlFor="address">רחוב</label>
                  <input
                    type="text"
                    id="address"
                    name="address"
                    value={newDonor.address}
                    onChange={handleAddDonorChange}
                    required
                  />
                </div>
              </div>
              
              <div className="form-actions">
                <button type="submit" className="submit-button">הוסף מתנדב</button>
                <button 
                  type="button" 
                  className="cancel-button"
                  onClick={() => setShowAddForm(false)}
                >
                  בטל
                </button>
              </div>
            </form>
          </div>
        )}
        
        <table className="donor-table">
          <thead>
            <tr>
              <th className="first-name-column">שם פרטי</th>
              <th className="last-name-column">שם משפחה</th>
              <th className="phone-number-column">מספר טלפון</th>
              <th className="street-column">שכונה</th>
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
                <td className="street-column">
                  <select
                    value={donor.street || ""}
                    onChange={(e) => handleInputChange(e, donor.id, "street")}
                    className="street-select"
                  >
                    <option value="">בחר שכונה</option>
                    {BEER_SHEVA_STREETS.map((street) => (
                      <option key={street} value={street}>
                        {street}
                      </option>
                    ))}
                  </select>
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
      
      
    </>
  );
};

export default DonorTable;