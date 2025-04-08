import React, { useEffect, useState } from "react";
import "../styles/NeedyTable.css";
import { Needy } from "../../models/NeedyModel";
import { getNeedyList, updateNeedy, deleteNeedy, registerNeedy } from "../../Restapi/NeedyRestapi";
import FeedingLogo from '../../Authentication/Images/logo.png';
import DiveHeader from "../../GoPage/DiveHeader";

// List of Beer Sheva neighborhoods
const BEER_SHEVA_STREETS = [
    'העיר העתיקה', 'נווה עופר', 'המרכז האזרחי', 'א\'', 'ב\'', 'ג\'', 'ד\'', 'ה\'', 
    'ו\'', 'ט\'', 'י"א', 'נאות לון', 'נווה זאב', 'נווה נוי', 'נחל בקע', 
    'נחל עשן (נווה מנחם)', 'רמות', 'נאות אברהם (פלח 6)', 'נווה אילן (פלח 7)',
    'הכלניות', 'סיגליות', 'פארק הנחל'
];

const NeedyTable = () => {
  const [needyList, setNeedyList] = useState<Needy[]>([]);
  const [showAddForm, setShowAddForm] = useState(false);
  const [newNeedy, setNewNeedy] = useState<Needy>({
    id: 0,
    firstName: "",
    lastName: "",
    phoneNumber: "",
    address: "",
    familySize: 1,
    confirmStatus: "PENDING",
    street: ""
  });

  useEffect(() => {
    fetchNeedy();
  }, []);

  const fetchNeedy = async () => {
    try {
      const needy = await getNeedyList();
      setNeedyList(needy);
    } catch (e) {
      alert("שגיאה בטעינת הרשימה");
      console.error(e);
    }
  };

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>,
    id: number,
    field: keyof Needy
  ) => {
    setNeedyList(needyList.map(needy => 
      needy.id === id ? { ...needy, [field]: e.target.value } : needy
    ));
  };

  const handleSave = async (needy: Needy) => {
    try {
      await updateNeedy(needy);
      fetchNeedy();
    } catch (e) {
      alert("שגיאה בעדכון נתוני הנזקק");
      console.error(e);
    }
  };

  const handleDelete = async (needy: Needy) => {
    try {
      await deleteNeedy(needy);
      fetchNeedy();
    } catch (e) {
      alert("שגיאה במחיקת נזקק");
      console.error(e);
    }
  };

  const handleAddNeedyChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setNewNeedy((prev) => ({ ...prev, [name]: value }));
  };

  const handleAddNeedySubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await registerNeedy(newNeedy);
      setNewNeedy({ id: 0, firstName: "", lastName: "", phoneNumber: "", address: "", familySize: 1, confirmStatus: "PENDING", street: "" });
      setShowAddForm(false);
      fetchNeedy();
      alert("הנזקק נוסף בהצלחה");
    } catch (e) {
      alert("שגיאה בהוספת נזקק");
      console.error(e);
    }
  };

  return (
    <>
      <DiveHeader />
      <div className="needy-table-container">


        <div className="table-actions">
          <button className="add-needy-button" onClick={() => setShowAddForm(!showAddForm)}>
            {showAddForm ? "בטל הוספה" : "הוסף נזקק חדש"}
          </button>
        </div>

        {showAddForm && (
          <div className="add-needy-form-container">
            <h2>הוספת נזקק חדש</h2>
            <form onSubmit={handleAddNeedySubmit} className="add-needy-form">
              <div className="form-group">
                <label>שם פרטי</label>
                <input type="text" name="firstName" value={newNeedy.firstName} onChange={handleAddNeedyChange} required />
              </div>
              <div className="form-group">
                <label>שם משפחה</label>
                <input type="text" name="lastName" value={newNeedy.lastName} onChange={handleAddNeedyChange} required />
              </div>
              <div className="form-group">
                <label>מספר טלפון</label>
                <input type="text" name="phoneNumber" value={newNeedy.phoneNumber} onChange={handleAddNeedyChange} required />
              </div>
              <div className="form-group">
                <label>שכונה</label>
                <select name="street" value={newNeedy.street} onChange={handleAddNeedyChange} required>
                  <option value="">בחר שכונה</option>
                  {BEER_SHEVA_STREETS.map((street) => (
                    <option key={street} value={street}>{street}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>כתובת</label>
                <input type="text" name="address" value={newNeedy.address} onChange={handleAddNeedyChange} required />
              </div>
              <div className="form-group">
                <label>גודל משפחה</label>
                <input type="number" name="familySize" value={newNeedy.familySize} onChange={handleAddNeedyChange} required min="1" />
              </div>
              <button type="submit" className="submit-button">הוסף נזקק</button>
            </form>
          </div>
        )}

        <table className="needy-table">
          <thead>
            <tr>
              <th>שם פרטי</th>
              <th>שם משפחה</th>
              <th>טלפון</th>
              <th>שכונה</th>
              <th>כתובת</th>
              <th>גודל משפחה</th>
              <th>סטטוס</th>
              <th>פעולות</th>
            </tr>
          </thead>
          <tbody>
            {needyList.map((needy) => (
              <tr key={needy.id}>
                <td><input type="text" value={needy.firstName} onChange={(e) => handleInputChange(e, needy.id, "firstName")} /></td>
                <td><input type="text" value={needy.lastName} onChange={(e) => handleInputChange(e, needy.id, "lastName")} /></td>
                <td><input type="text" value={needy.phoneNumber} onChange={(e) => handleInputChange(e, needy.id, "phoneNumber")} /></td>
                <td><select name="street" value={needy.street} onChange={(e) => handleInputChange(e, needy.id, "street")}>
                  {BEER_SHEVA_STREETS.map((street) => (
                    <option key={street} value={street}>{street}</option>
                  ))}
                </select></td>
                <td><input type="text" value={needy.address} onChange={(e) => handleInputChange(e, needy.id, "address")} /></td>
                <td><input type="number" value={needy.familySize} onChange={(e) => handleInputChange(e, needy.id, "familySize")} /></td>
                <td>{needy.confirmStatus}</td>
                <td>
                  <button className="save-button" onClick={() => handleSave(needy)}>שמור</button>
                  <button className="delete-button" onClick={() => handleDelete(needy)}>מחק</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
};

export default NeedyTable;
