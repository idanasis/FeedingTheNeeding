import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { slideUpVariants, zoomInVariants } from './animation';
import { createOrUpdateNeedy } from '../homeApi'; // Import the API function

const Contact = () => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    address: '',
    phoneNumber: '',
    familySize: 0,
    notes: '',
  });

  const [errors, setErrors] = useState({
    firstName: false,
    lastName: false,
    address: false,
    phoneNumber: false,
    familySize: false,
  });

  const [loading, setLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'familySize' ? Number(value) : value, // Convert familySize to a number
    }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: false }));
    }
  };

  const validateForm = () => {
    const newErrors = {
      firstName: !formData.firstName.trim(),
      lastName: !formData.lastName.trim(),
      address: !formData.address.trim(),
      phoneNumber: !formData.phoneNumber.trim(),
      familySize: formData.familySize <= 0,
    };
    setErrors(newErrors);
    return !Object.values(newErrors).some((error) => error);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSuccessMessage('');
    setErrorMessage('');

    if (!validateForm()) {
      return;
    }

    setLoading(true);
    try {
      await createOrUpdateNeedy({
        ...formData,
        city: 'Unknown', // Add default value if city is not included in the form
        role: 'NEEDY', // Default role
        confirmStatus: 'PENDING', // Default status
      });
      setSuccessMessage('בקשה נשלחה בהצלחה!');
      setFormData({
        firstName: '',
        lastName: '',
        address: '',
        phoneNumber: '',
        familySize: 0,
        notes: '',
      });
    } catch (error) {
      setErrorMessage('שגיאה בשליחת הבקשה. נסה שוב.');
      console.error('Error submitting form:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div id="בקשת סיוע">
      <div className="lg:w-[80%] w-[90%] m-auto py-[60px] flex lg:flex-row flex-col items-start gap-[50px] justify-between">
        <motion.div
          initial="hidden"
          whileInView="visible"
          variants={slideUpVariants}
          className="lg:w-[60%] w-full flex flex-col justify-center items-start gap-6"
        >
          <motion.h1 variants={slideUpVariants} className="text-red-500 text-3xl">
            לבקשת סיוע
          </motion.h1>
          <motion.h1 variants={slideUpVariants} className="text-white uppercase text-[40px] font-bold">
            אנחנו כאן לעזור
          </motion.h1>
          <div className="w-[120px] h-[6px] bg-red-500"></div>
          <p className="text-3xl italic text-red-100 mt-[60px]">
            אם אתם מתקשים כלכלית ורוצים עזרה מהעמותה שלנו, זה המקום לבקש. מי שבמקש יכנס לרשימת המתנה, במידה ויהיה מקום וימצא מתאים ניצור איתכם קשר.
          </p>
        </motion.div>
        <motion.div
          initial="hidden"
          whileInView="visible"
          variants={slideUpVariants}
          className="lg:w-[40%] w-full flex flex-col justify-center items-start gap-6"
        >
          <motion.form
            initial="hidden"
            whileInView="visible"
            variants={zoomInVariants}
            className="flex flex-col justify-center items-start gap-4 w-full"
            onSubmit={handleSubmit}
          >
            <input
              type="text"
              name="firstName"
              placeholder="הכנס שם פרטי"
              value={formData.firstName}
              onChange={handleInputChange}
              className={`px-6 py-3 border-[2px] ${
                errors.firstName ? 'border-red-500' : 'border-black'
              } text-black rounded-lg w-full`}
            />
            <input
              type="text"
              name="lastName"
              placeholder="הכנס שם משפחה"
              value={formData.lastName}
              onChange={handleInputChange}
              className={`px-6 py-3 border-[2px] ${
                errors.lastName ? 'border-red-500' : 'border-black'
              } text-black rounded-lg w-full`}
            />
            <input
              type="text"
              name="address"
              placeholder="הכנס כתובת מגורים"
              value={formData.address}
              onChange={handleInputChange}
              className={`px-6 py-3 border-[2px] ${
                errors.address ? 'border-red-500' : 'border-black'
              } text-black rounded-lg w-full`}
            />
            <input
              type="number"
              name="phoneNumber"
              placeholder="הכנס פלאפון"
              value={formData.phoneNumber}
              onChange={handleInputChange}
              className={`px-6 py-3 border-[2px] ${
                errors.phoneNumber ? 'border-red-500' : 'border-black'
              } text-black rounded-lg w-full`}
            />
            <input
              type="number"
              name="familySize"
              placeholder="הכנס מספר נפשות"
              value={formData.familySize || ''}
              onChange={handleInputChange}
              className={`px-6 py-3 border-[2px] ${
                errors.familySize ? 'border-red-500' : 'border-black'
              } text-black rounded-lg w-full`}
            />
            <textarea
              name="notes"
              placeholder="הערות נוספות"
              value={formData.notes}
              onChange={handleInputChange}
              rows={4}
              className="px-6 py-3 border-[2px] border-black text-black rounded-lg w-full"
            ></textarea>
            <motion.button
              variants={zoomInVariants}
              type="submit"
              className="bg-red-500 hover:bg-black hover:text-white px-10 py-4 rounded-lg text-black font-bold"
              disabled={loading}
            >
              {loading ? 'שולח...' : 'שליחה'}
            </motion.button>
          </motion.form>
          {successMessage && <p className="text-green-500 mt-4">{successMessage}</p>}
          {errorMessage && <p className="text-red-500 mt-4">{errorMessage}</p>}
        </motion.div>
      </div>
    </div>
  );
};

export default Contact;
